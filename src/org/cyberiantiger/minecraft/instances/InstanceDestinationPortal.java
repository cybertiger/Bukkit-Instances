/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author antony
 */
public class InstanceDestinationPortal extends Portal {
    private PortalPair pair;

    public InstanceDestinationPortal(Cuboid cuboid) {
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

        if (pair == null) {
            e.setCancelled(true);
            player.sendMessage("Portal is not connected");
            return;
        }

        InstanceEntrancePortal entrance = pair.getEnter();
        if (entrance == null) {
            e.setCancelled(true);
            player.sendMessage("Portal is not connected");
            return;
        }

        entrance.teleport(player);
    }

    @Override
    protected void onLeave(Instances instances, PlayerMoveEvent e) {
        // NOOP
    }

}
