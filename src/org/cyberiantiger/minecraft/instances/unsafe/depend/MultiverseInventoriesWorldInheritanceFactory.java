/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import com.onarandombox.multiverseinventories.api.profile.WorldGroupProfile;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public class MultiverseInventoriesWorldInheritanceFactory extends DependencyFactory<WorldInheritance> {
    public static final String PLUGIN_NAME = "Multiverse-Inventories";

    public MultiverseInventoriesWorldInheritanceFactory(Plugin thisPlugin) {
        super(thisPlugin, PLUGIN_NAME);
    }

    @Override
    public Class<WorldInheritance> getInterfaceClass() {
        return WorldInheritance.class;
    }

    @Override
    protected WorldInheritance createInterface(Plugin plugin) throws Exception {
        return new MultiverseInventoriesWorldInheritance(plugin);
    }

    private static class MultiverseInventoriesWorldInheritance implements WorldInheritance {
        private MultiverseInventories plugin;

        public MultiverseInventoriesWorldInheritance(Plugin plugin) {
            this.plugin = (MultiverseInventories) plugin;
        }

        public void addInheritance(String parent, String child) {
            for (WorldGroupProfile profile : plugin.getGroupManager().getGroupsForWorld(parent)) {
                profile.addWorld(child);
            }
        }

        public void removeInheritance(String parent, String child) {
            for (WorldGroupProfile profile : plugin.getGroupManager().getGroupsForWorld(parent)) {
                profile.removeWorld(child);
            }
        }

        public Plugin getPlugin() {
            return plugin;
        }
    }
}
