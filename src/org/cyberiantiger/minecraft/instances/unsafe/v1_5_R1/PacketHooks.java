/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.v1_5_R1;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.NetworkManager;
import net.minecraft.server.v1_5_R1.Packet;
import net.minecraft.server.v1_5_R1.Packet250CustomPayload;
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
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.unsafe.NBTTools;

/**
 *
 * @author antony
 */
public class PacketHooks implements org.cyberiantiger.minecraft.instances.unsafe.PacketHooks, Listener {

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

    public PacketHooks(Plugin plugin, boolean editInCreative) {
        this.plugin = plugin;
        this.editInCreative = editInCreative;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (installed.get()) {
            if (! installPacketHooks(e.getPlayer())) {
                plugin.getLogger().warning("Disabling packet hooks, server seems not to support them, consider using ProtocolLib");
                uninstall();
            }
        }
    }

    public void install() {
        if (this.installed.getAndSet(true))
            return;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (!installPacketHooks(p)) {
                plugin.getLogger().warning("Disabling packet hooks, server seems not to support them, consider using ProtocolLib");
                uninstall();
                break;
            }
        }
    }

    public void uninstall() {
        if (!this.installed.getAndSet(false))
            return;
        PlayerJoinEvent.getHandlerList().unregister(this);
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            uninstallPacketHooks(p);
        }
    }

    private boolean installPacketHooks(Player player) {
        try {
            EntityPlayer handle = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = handle.playerConnection;
            NetworkManager netMan = (NetworkManager) connection.networkManager;
            Queue inboundQueue = (Queue) INBOUND_QUEUE.get(netMan);
            Queue hackedInboundQueue = new HackedInboundQueue(player, this, inboundQueue);
            INBOUND_QUEUE.set(netMan, hackedInboundQueue);
            return true;
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean uninstallPacketHooks(Player player) {
        try {
            EntityPlayer handle = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = handle.playerConnection;
            NetworkManager netMan = (NetworkManager) connection.networkManager;
            Queue inboundQueue = (Queue) INBOUND_QUEUE.get(netMan);
            if (inboundQueue instanceof HackedInboundQueue) {
                INBOUND_QUEUE.set(netMan, ((HackedInboundQueue)inboundQueue).delegate);
                return true;
            }
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
        return false;
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
                    NBTTools nbtTools = ((Instances)plugin).getNBTTools();
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

    public Plugin getPlugin() {
        return plugin;
    }

    private static final class HackedInboundQueue implements Queue {

        private final Player player;
        private final PacketHooks hooks;
        private final Queue delegate;

        public HackedInboundQueue(Player player, PacketHooks hooks, Queue delegate) {
            this.player = player;
            this.hooks = hooks;
            this.delegate = delegate;
        }

        @Override
        public Object poll() {
            while (true) {
                Object ret = delegate.poll();
                if (!hooks.handlePacket(player, (Packet) ret)) {
                    return ret;
                }
            }
        }

        @Override
        public boolean add(Object e) {
            return delegate.add(e);
        }

        @Override
        public boolean offer(Object e) {
            return delegate.offer(e);
        }

        @Override
        public Object remove() {
            return delegate.remove();
        }

        @Override
        public Object element() {
            return delegate.element();
        }

        @Override
        public Object peek() {
            return delegate.peek();
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        @Override
        public Iterator iterator() {
            return delegate.iterator();
        }

        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public Object[] toArray(Object[] a) {
            return delegate.toArray(a);
        }

        @Override
        public boolean remove(Object o) {
            return delegate.remove(o);
        }

        @Override
        public boolean containsAll(Collection c) {
            return delegate.containsAll(c);
        }

        @Override
        public boolean addAll(Collection c) {
            return delegate.addAll(c);
        }

        @Override
        public boolean removeAll(Collection c) {
            return delegate.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection c) {
            return delegate.retainAll(c);
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }
}
