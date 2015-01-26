/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.unsafe.NBTTools;

/**
 *
 * @author antony
 */
public class ProtocolLibPacketHooksFactory extends DependencyFactory<Instances, PacketHooks> {
    public static final String PLUGIN_NAME = "ProtocolLib";

    public ProtocolLibPacketHooksFactory(Instances thisPlugin) {
        super (thisPlugin, PLUGIN_NAME);
    }

    @Override
    public Class<PacketHooks> getInterfaceClass() {
        return PacketHooks.class;
    }

    @Override
    protected PacketHooks createInterface(Plugin plugin) throws Exception {
        return new ProtocolLibPacketHooks(plugin);
    }

    private final class ProtocolLibPacketHooks implements PacketHooks {
        private final ProtocolLibrary protocolLib;
        private final ProtocolManager protocolManager;
        private final PacketAdapter packetAdapter;
        public ProtocolLibPacketHooks(Plugin plugin) {
            this.protocolLib = (ProtocolLibrary) plugin;
            this.protocolManager = ProtocolLibrary.getProtocolManager();
            this.packetAdapter = new PacketAdapterImpl((Instances)getThisPlugin(), ConnectionSide.CLIENT_SIDE);
        }

        @Override
        public void install() {
            protocolManager.addPacketListener(packetAdapter);
        }

        @Override
        public void uninstall() {
            protocolManager.removePacketListener(packetAdapter);
        }

        @Override
        public Plugin getPlugin() {
            return protocolLib;
        }
    }
    

    private static final class PacketAdapterImpl extends PacketAdapter {
        private final Instances instances;
        private final boolean newProtocol;

        public PacketAdapterImpl(Instances plugin, ConnectionSide connectionSide) {
            super(plugin, PacketType.Play.Client.CUSTOM_PAYLOAD);
            this.instances = plugin;
            newProtocol = PacketType.Play.Client.CUSTOM_PAYLOAD.getCurrentVersion().compareTo(MinecraftVersion.WORLD_UPDATE) >= 0;
        }

        private String decodeCommand(ByteBuffer in) throws IOException {
            if (newProtocol) {
                int length = in.get() & 0xff;
                return new String(in.array(), in.arrayOffset() + in.position(), length, Charsets.UTF_8);
            } else {
                int sLength = in.getShort();
                if (sLength < 0 || sLength > 256) {
                    throw new IOException("Expected string length between 0 and 256 inclusive, got " + sLength);
                }
                StringBuilder commandString = new StringBuilder(sLength);
                for (int a = 0; a < sLength; a++) {
                    commandString.append(in.getChar());
                }
                return commandString.toString();
            }
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            try {
                PacketContainer container = event.getPacket();
                StructureModifier<String> strings = container.getStrings();
                String tag = strings.readSafely(0);
                if ("MC|AdvCdm".equals(tag)) {
                    event.setCancelled(true);
                    final int length = container.getIntegers().readSafely(0);
                    final byte[] data = container.getByteArrays().readSafely(0);
                    ByteBuffer in = ByteBuffer.wrap(data, 0, length);
                    final byte b = newProtocol ? in.get() : 0;
                    if (b == 0) {
                        final int i = in.getInt();
                        final int j = in.getInt();
                        final int k = in.getInt();
                        final String command = decodeCommand(in);
                        final Player player = event.getPlayer();
                        getPlugin().getServer().getScheduler().runTask(getPlugin(), new UpdateCommandBlock(player, i, j, k, command));
                    } else if (b == 1) {
                        final int id = in.getInt();
                        final String command = decodeCommand(in);
                        final Player player = event.getPlayer();
                        getPlugin().getServer().getScheduler().runTask(getPlugin(), new UpdateCommandMinecart(player, id, command));
                    }
                }
            } catch (IOException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Error decoding command block edit packet", ex);
            }
        }
        
        private abstract class AbstractUpdateCommand implements Runnable {
            protected final Player player;
            protected final String command;

            public AbstractUpdateCommand(Player player, String command) {
                this.player = player;
                this.command = command;
            }

            public boolean checkAccess() {
                if (instances.getEditCommandInCreative() && player.getGameMode() != GameMode.CREATIVE) {
                    player.sendMessage("You need to be in creative to edit command blocks.");
                    return false;
                }
                String[] parts = command.split(" ");
                if (parts.length > 0) {
                    if (!player.hasPermission("instances.general.cmd.set." + parts[0])) {
                        player.sendMessage("You do not have permission to use " + parts[0] + " with command blocks.");
                        return false;
                    }
                } else {
                    if (!player.hasPermission("instances.general.cmd.reset")) {
                        player.sendMessage("You do not have permission to reset command blocks.");
                        return false;
                    }
                }
                return true;
            }
        }

        private class UpdateCommandMinecart extends AbstractUpdateCommand {
            private final int entityId;

            public UpdateCommandMinecart(Player player, int entityId, String command) {
                super (player, command);
                this.entityId = entityId;
            }

            @Override
            public void run() {
                NBTTools nbtTools = instances.getNBTTools();
                Entity e = nbtTools.getEntityById(player.getWorld(), entityId);
                if (e instanceof CommandMinecart && checkAccess()) {
                    CompoundTag readEntity = nbtTools.readEntity(e);
                    readEntity.setString("Command", command);
                    nbtTools.updateEntity(e, readEntity);
                    player.sendMessage("Command set: " + command);
                }
            }
        }

        private class UpdateCommandBlock extends AbstractUpdateCommand {
            private final int x;
            private final int y;
            private final int z;
            
            public UpdateCommandBlock(Player player, int x, int y, int z, String command) {
                super(player, command);
                this.x = x;
                this.y = y;
                this.z = z;
            }
            
            @Override
            public void run() {
                Block b = player.getWorld().getBlockAt(x, y, z);
                if (b.getType() == Material.COMMAND && checkAccess()) {
                    // Set command.
                    NBTTools nbtTools = instances.getNBTTools();
                    CompoundTag e = nbtTools.readTileEntity(b);
                    e.setString("Command", command);
                    nbtTools.writeTileEntity(b, e);
                    player.sendMessage("Command set: " + command);
                }
            }
        }
    }

}