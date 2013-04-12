/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.instances.Instances;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author antony
 */
public class PEXWorldInheritanceFactory extends DependencyFactory<Instances, WorldInheritance> {
    public static final String PLUGIN_NAME = "PermissionsEx";

    public PEXWorldInheritanceFactory(Instances thisPlugin) {
        super(thisPlugin, PLUGIN_NAME);
    }

    @Override
    public Class<WorldInheritance> getInterfaceClass() {
        return WorldInheritance.class;
    }

    @Override
    protected WorldInheritance createInterface(Plugin plugin) throws Exception {
        return new PEXWorldInheritance(plugin);
    }

    public static class PEXWorldInheritance implements WorldInheritance {
        private final PermissionsEx plugin;
        private final PermissionManager permissionManager;

        public PEXWorldInheritance(Plugin plugin) {
            this.plugin = (PermissionsEx) plugin;
            permissionManager = PermissionsEx.getPermissionManager();
        }

        public void preAddInheritance(String parent, String child) {
            PermissionManager pm = PermissionsEx.getPermissionManager();
            pm.setWorldInheritance(child, new String[]{parent});
        }

        public void postAddInheritance(String parent, String child) {
        }

        public void preRemoveInheritance(String parent, String child) {
        }

        public void postRemoveInheritance(String parent, String child) {
            PermissionManager pm = PermissionsEx.getPermissionManager();
            pm.setWorldInheritance(child, new String[0]);
        }

        public Plugin getPlugin() {
            return plugin;
        }
    }
}
