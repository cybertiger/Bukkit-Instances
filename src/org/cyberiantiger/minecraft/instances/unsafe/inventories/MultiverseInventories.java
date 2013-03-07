/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.inventories;

import com.onarandombox.multiverseinventories.api.Inventories;
import com.onarandombox.multiverseinventories.api.profile.WorldGroupProfile;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author antony
 */
public class MultiverseInventories implements org.cyberiantiger.minecraft.instances.unsafe.inventories.Inventories {

    public static final String PLUGIN_NAME = "Multiverse-Inventories";
    private final Logger log;
    private final PluginManager manager;

    public MultiverseInventories(Logger log, PluginManager manager) {
        this.log = log;
        this.manager = manager;
        com.onarandombox.multiverseinventories.api.Inventories plugin =
                (Inventories) manager.getPlugin(PLUGIN_NAME);
        plugin.getGroupManager().getGroups();
    }

    protected boolean isPluginEnabled() {
        return manager.isPluginEnabled(PLUGIN_NAME);
    }

    protected Inventories getPlugin() {
        return (Inventories) manager.getPlugin(PLUGIN_NAME);
    }

    public void addShare(String source, String destination) {
        if (isPluginEnabled()) {
            Inventories inventories = getPlugin();
            for (WorldGroupProfile profile : inventories.getGroupManager().getGroupsForWorld(source)) {
                profile.addWorld(destination);
            }
        }
    }

    public void removeShare(String source, String destination) {
        if (isPluginEnabled()) {
            Inventories inventories = getPlugin();
            for (WorldGroupProfile profile : inventories.getGroupManager().getGroupsForWorld(source)) {
                profile.removeWorld(destination);
            }
        }
    }
}
