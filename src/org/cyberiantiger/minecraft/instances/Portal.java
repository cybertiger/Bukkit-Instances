/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import org.bukkit.GameMode;
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
        player.teleport(floor);
        if (instances.getWorldManager().setGameModeOnTp(player, cuboid.getWorld())) {
            GameMode gm = instances.getWorldManager().getGameMode(cuboid.getWorld(), player.getGameMode());
            if (gm != null) {
                player.setGameMode(gm);
            }
        }
    }
}
