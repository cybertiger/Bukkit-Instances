/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.worldmanager;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class MultiverseCoreWorldManager implements WorldManager {

    public static final String PLUGIN_NAME = "Multiverse-Core";
    private final Logger log;
    private final PluginManager manager;

    public MultiverseCoreWorldManager(Logger log, PluginManager manager) {
        this.log = log;
        this.manager = manager;
        getPlugin();
    }

    protected boolean isPluginEnabled() {
        return manager.isPluginEnabled(PLUGIN_NAME);
    }

    protected MultiverseCore getPlugin() {
        return (MultiverseCore) manager.getPlugin(PLUGIN_NAME);
    }

    public boolean setGameModeOnTp(Player player, String destination) {
        return !player.hasPermission("multiverse.bypass.gamemode." + destination);
    }

    public GameMode getGameMode(String destination, GameMode fallback) {
        if (isPluginEnabled()) {
            MVWorldManager mVWorldManager = getPlugin().getMVWorldManager();
            MultiverseWorld mVWorld = mVWorldManager.getMVWorld(destination);
            if (mVWorld == null) {
                return null;
            }
            return mVWorld.getGameMode();
        } else {
            return fallback;
        }
    }
}
