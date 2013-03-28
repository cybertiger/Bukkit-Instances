/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.unsafe.PacketHooks;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.unsafe.NBTTools;

/**
 *
 * @author antony
 */
public class ProtocolLibPacketHooksFactory extends DependencyFactory<PacketHooks> {
    public static final String PLUGIN_NAME = "ProtocolLib";

    public ProtocolLibPacketHooksFactory(Plugin thisPlugin) {
        super (thisPlugin, PLUGIN_NAME);
    }

    @Override
    public Class<PacketHooks> getInterfaceClass() {
        return PacketHooks.class;
    }

    @Override
    protected PacketHooks createInterface(Plugin plugin) throws Exception {
        return new ProtocolLibPacketHooks(plugin, ((Instances)getThisPlugin()).getEditCommandInCreative());
    }

    private final class ProtocolLibPacketHooks implements PacketHooks {
        private final ProtocolLibrary protocolLib;
        private final boolean editInCreative;
        private final ProtocolManager protocolManager;
        private final PacketAdapter packetAdapter;
        public ProtocolLibPacketHooks(Plugin plugin, boolean editInCreative) {
            this.protocolLib = (ProtocolLibrary) plugin;
            this.editInCreative = editInCreative;
            this.protocolManager = ProtocolLibrary.getProtocolManager();
            this.packetAdapter = new PacketAdapterImpl((Instances)getThisPlugin(), ConnectionSide.SERVER_SIDE, 250);
        }

        public void install() {
            protocolManager.addPacketListener(packetAdapter);
        }

        public void uninstall() {
            protocolManager.removePacketListener(packetAdapter);
        }

        public Plugin getPlugin() {
            return protocolLib;
        }
    }

    private static final class PacketAdapterImpl extends PacketAdapter {
        private final Instances instances;

        public PacketAdapterImpl(Instances plugin, ConnectionSide connectionSide, Integer... packets) {
            super(plugin, connectionSide, packets);
            this.instances = plugin;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer container = event.getPacket();
            StructureModifier<String> strings = container.getStrings();
            String tag = strings.readSafely(0);
            if ("MC|AdvCdm".equals(tag)) {
                event.setCancelled(true);
                final int length = container.getIntegers().readSafely(0);
                final byte[] data = container.getByteArrays().readSafely(0);
                try {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(data, 0, length));
                    try {
                        final int i = in.readInt();
                        final int j = in.readInt();
                        final int k = in.readInt();
                        final int sLength = in.readShort();
                        if (sLength < 0 || sLength > 256) {
                            throw new IOException("Expected string length between 0 and 256 inclusive, got " + sLength);
                        }
                        StringBuilder commandString = new StringBuilder(sLength);
                        for (int a = 0; a < sLength; a++) {
                            commandString.append(in.readChar());
                        }
                        final String command = commandString.toString();
                        final Player player = event.getPlayer();
                        getPlugin().getServer().getScheduler().runTask(getPlugin(), new Runnable() {
                            public void run() {
                                /* Skip this check for protocol lib.
                                if (!((CraftServer) plugin.getServer()).getServer().getEnableCommandBlock()) { player.sendMessage("Command blocks are not enabled, set enable-command-block=true in server.properties.");
                                return false;
                                }
                                 */
                                if (instances.getEditCommandInCreative() && player.getGameMode() != GameMode.CREATIVE) {
                                    player.sendMessage("You need to be in creative to edit command blocks.");
                                    return;
                                }
                                Block b = player.getWorld().getBlockAt(i, j, k);
                                if (b.getType() != Material.COMMAND) {
                                    return;
                                }
                                String[] parts = command.split(" ");
                                if (parts.length > 0) {
                                    if (!player.hasPermission("instances.general.cmd.set." + parts[0])) {
                                        player.sendMessage("You do not have permission to use " + parts[0] + " with command blocks.");
                                        return;
                                    }
                                } else {
                                    if (!player.hasPermission("instances.general.cmd.reset")) {
                                        player.sendMessage("You do not have permission to reset command blocks.");
                                        return;
                                    }
                                }
                                // Set command.
                                NBTTools nbtTools = instances.getNBTTools();
                                CompoundTag e = nbtTools.readTileEntity(b);
                                e.setString("Command", command);
                                nbtTools.writeTileEntity(b, e);
                                player.sendMessage("Command set: " + command);
                            }
                        });
                    } finally {
                        in.close();
                    }
                } catch (IOException ex) {
                    getPlugin().getLogger().log(Level.WARNING, "Error decoding command block edit packet", ex);
                }
            }
        }
    }
    
}
