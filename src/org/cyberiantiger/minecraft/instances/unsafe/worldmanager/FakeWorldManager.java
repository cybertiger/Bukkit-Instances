/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.worldmanager;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author antony
 */
public class FakeWorldManager implements WorldManager {

    public boolean setGameModeOnTp(Player player, String destination) {
        return false;
    }

    public GameMode getGameMode(String destination, GameMode fallback) {
        return fallback;
    }

}
