/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author antony
 */
public abstract class Portal {

    private final Cuboid cuboid;
    private Facing facing;

    public Portal(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    protected abstract void onEnter(Instances instances, PlayerMoveEvent e);

    protected abstract void onLeave(Instances instances, PlayerMoveEvent e);

    // Teleports a player TO this portal.
    protected void teleport(Instances instances, Player player, World world) {
        Location floor = cuboid.getCenterFloor(world);
        if (facing != null) {
            floor.setYaw(facing.getYaw());
            floor.setPitch(facing.getPitch());
        } else {
            Location playerLocation = player.getLocation();
            floor.setYaw(playerLocation.getYaw());
            floor.setPitch(playerLocation.getPitch());
        }
        player.teleport(floor);
    }

    public Facing getFacing() {
        return facing;
    }

    public void setFacing(Facing facing) {
        this.facing = facing;
    }

    public abstract boolean isDestination();
}
