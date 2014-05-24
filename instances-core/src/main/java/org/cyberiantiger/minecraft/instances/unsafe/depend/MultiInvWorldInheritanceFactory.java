/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import uk.co.tggl.pluckerpluck.multiinv.MultiInv;

/**
 *
 * @author antony
 */
public class MultiInvWorldInheritanceFactory extends DependencyFactory<Instances, WorldInheritance> {
    public static final String PLUGIN_NAME = "MultiInv";

    public MultiInvWorldInheritanceFactory(Instances instances) {
        super(instances, PLUGIN_NAME);
    }

    @Override
    public Class<WorldInheritance> getInterfaceClass() {
        return WorldInheritance.class;
    }

    @Override
    protected WorldInheritance createInterface(Plugin plugin) throws Exception {
        return new MultiInvWorldInheritance(plugin);
    }

    private static class MultiInvWorldInheritance implements WorldInheritance {
        private final MultiInv plugin;

        public MultiInvWorldInheritance(Plugin plugin) {
            this.plugin = (MultiInv) plugin;
        }

        @Override
        public Plugin getPlugin() {
            return plugin;
        }

        @Override
        public void preAddInheritance(String parent, String child) {
            Map<String, String> groups = plugin.getAPI().getGroups();
            if (groups.containsKey(parent)) {
                groups.put(child, groups.get(parent));
            }
        }

        @Override
        public void postAddInheritance(String parent, String child) {
        }

        @Override
        public void preRemoveInheritance(String parent, String child) {
        }

        @Override
        public void postRemoveInheritance(String parent, String child) {
            Map<String,String> groups = plugin.getAPI().getGroups();
            groups.remove(child);
        }
    }
}
