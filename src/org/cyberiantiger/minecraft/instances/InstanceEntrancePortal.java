/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.cyberiantiger.minecraft.instances.unsafe.InstanceTools;

/**
 *
 * @author antony
 */
public class InstanceEntrancePortal extends Portal {

    private PortalPair pair;

    public InstanceEntrancePortal(Cuboid cuboid) {
        super(cuboid);
    }

    public void setPortalPair(PortalPair pair) {
        this.pair = pair;
    }

    public PortalPair getPortalPair() {
        return pair;
    }

    @Override
    protected void onEnter(Instances instances, PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Party party = instances.getParty(player);
        if (party == null) {
            player.sendMessage("You must be in a party to enter the dungeon.");
            e.setCancelled(true);
            return;
        }

        if (pair == null) {
            player.sendMessage("Portal does not connect anywhere.");
            e.setCancelled(true);
            return;
        }

        InstanceDestinationPortal destination = getPortalPair().getDestination();
        if (destination == null) {
            player.sendMessage("Portal does not connect anywhere.");
            e.setCancelled(true);
            return;
        }

        Instance instance = party.getInstanceFromSourceWorld(destination.getCuboid().getWorld());

        World world;
        if (instance == null) {
            World sourceWorld = instances.getServer().getWorld(destination.getCuboid().getWorld());
            if (sourceWorld == null) {
                player.sendMessage("Portal does not connect anywhere.");
                e.setCancelled(true);
                return;
            }
            world = InstanceTools.createInstance(instances, sourceWorld);
            if (world == null) {
                player.sendMessage("Could not create instance world.");
                e.setCancelled(true);
                return;
            }

            instance = new Instance(sourceWorld.getName(), world.getName());

            party.addInstance(instance);

            instances.getLogger().info("Created instance: " + instance);
        } else {
            world = instances.getServer().getWorld(instance.getInstance());
        }

        instances.setLastEnterPortal(player, this);

        destination.teleport(e.getPlayer(), world);
    }

    @Override
    protected void onLeave(Instances instances, PlayerMoveEvent e) {
        // NOOP
    }

    // Teleports a player TO this portal.
    protected void teleport(Player player) {
        super.teleport(player, player.getServer().getWorld(getCuboid().getWorld()));
    }
}
