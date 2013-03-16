/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.v1_5_R1;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.NetworkManager;
import net.minecraft.server.v1_5_R1.Packet;
import net.minecraft.server.v1_5_R1.Packet250CustomPayload;
import net.minecraft.server.v1_5_R1.Packet9Respawn;
import net.minecraft.server.v1_5_R1.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R1.CraftServer;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.unsafe.v1_5_R1.NBTTools;

/**
 *
 * @author antony
 */
public class PacketHooks implements org.cyberiantiger.minecraft.instances.unsafe.PacketHooks, Listener {

    private static final NBTTools nbtTools = new NBTTools();
    private static final Field INBOUND_QUEUE;

    static {
        Field f = null;
        try {
            f = NetworkManager.class.getDeclaredField("inboundQueue");
            f.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        }
        INBOUND_QUEUE = f;
    }
    private AtomicBoolean installed = new AtomicBoolean(false);
    private Plugin plugin;
    private boolean editInCreative;

    public PacketHooks() {
    }

    public void configure(Plugin plugin, boolean editInCreative) {
        this.plugin = plugin;
        this.editInCreative = editInCreative;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (installed.get()) {
            installPacketHooks(e.getPlayer());
        }
    }

    public void setInstalled(boolean installed) {
        if (plugin == null) {
            throw new IllegalStateException("Not configured");
        }
        if (installed != this.installed.get()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            this.installed.set(installed);
            if (installed) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    installPacketHooks(p);
                }
            } else {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    uninstallPacketHooks(p);
                }
            }
        }
    }

    private void installPacketHooks(Player player) {
        try {
            EntityPlayer handle = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = handle.playerConnection;
            NetworkManager netMan = (NetworkManager) connection.networkManager;
            Queue inboundQueue = (Queue) INBOUND_QUEUE.get(netMan);
            Queue hackedInboundQueue = new HackedInboundQueue(player, this);
            INBOUND_QUEUE.set(netMan, hackedInboundQueue);
            // XXX: May reorder packets.
            while (!inboundQueue.isEmpty()) {
                hackedInboundQueue.add(inboundQueue.poll());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void uninstallPacketHooks(Player player) {
        try {
            EntityPlayer handle = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = handle.playerConnection;
            NetworkManager netMan = (NetworkManager) connection.networkManager;
            Queue inboundQueue = (Queue) INBOUND_QUEUE.get(netMan);
            Queue vanillaInboundQueue = new ConcurrentLinkedQueue();
            INBOUND_QUEUE.set(netMan, vanillaInboundQueue);
            // XXX: May reorder packets.
            while (!inboundQueue.isEmpty()) {
                vanillaInboundQueue.add(inboundQueue.poll());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PacketHooks.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean handlePacket(final Player player, final Packet packet) {
        if (installed.get() && packet instanceof Packet250CustomPayload) {
            Packet250CustomPayload customPacket = (Packet250CustomPayload) packet;
            if ("MC|AdvCdm".equals(customPacket.tag)) {
                try {
                    DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(customPacket.data));
                    final int i = datainputstream.readInt();
                    final int j = datainputstream.readInt();
                    final int k = datainputstream.readInt();
                    final String s = Packet.a(datainputstream, 256);
                    // Switch to main server thread.
                    if (!((CraftServer) plugin.getServer()).getServer().getEnableCommandBlock()) {
                        player.sendMessage("Command blocks are not enabled, set enable-command-block=true in server.properties.");
                        return false;
                    }
                    if (editInCreative && player.getGameMode() != GameMode.CREATIVE) {
                        player.sendMessage("You need to be in creative to edit command blocks.");
                        return false;
                    }
                    Block b = player.getWorld().getBlockAt(i, j, k);
                    if (b.getType() != Material.COMMAND) {
                        return false;
                    }
                    String[] parts = s.split(" ");
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
                    CompoundTag e = nbtTools.readTileEntity(b);
                    e.setString("Command", s);
                    nbtTools.writeTileEntity(b, e);
                    player.sendMessage("Command set: " + s);
                    return false;
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, null, e);
                    return false;
                }
            }

        }


        return true;
    }

    private static final class HackedInboundQueue extends ConcurrentLinkedQueue {

        private final Player player;
        private final PacketHooks hooks;
        // This packet is a NOOP when received from the client.
        // It is normally only sent server -> client.
        private static final Packet NOOP = new Packet9Respawn();

        public HackedInboundQueue(Player player, PacketHooks hooks) {
            this.player = player;
            this.hooks = hooks;
        }

        @Override
        public Object poll() {
            // Hook into poll even though it's more of a pain, because if we
            // eat the packet, we cannot return null.
            // This is processed on the main server thread, .add() is not.
            Object ret = super.poll();
            if (hooks.handlePacket(player, (Packet) ret)) {
                return ret;
            } else {
                return NOOP;
            }
        }
    }
}
