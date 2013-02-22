/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.worldmanager;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class MultiverseCoreWorldManager implements WorldManager {
    private final Instances instances;
    private final MultiverseCore core;

    public MultiverseCoreWorldManager(Instances instances, MultiverseCore core) {
        this.instances = instances;
        this.core = core;
    }

    public boolean setGameModeOnTp(Player player, String destination) {
        return !player.hasPermission("multiverse.bypass.gamemode." + destination);
    }

    public GameMode getGameMode(String destination) {
        MVWorldManager mVWorldManager = core.getMVWorldManager();
        MultiverseWorld mVWorld = mVWorldManager.getMVWorld(destination);
        if (mVWorld == null)
            return null;
        return mVWorld.getGameMode();
    }

}
