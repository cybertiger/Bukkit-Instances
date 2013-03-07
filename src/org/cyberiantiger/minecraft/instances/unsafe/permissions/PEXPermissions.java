/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.permissions;

import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author antony
 */
public class PEXPermissions implements Permissions {

    public static final String PLUGIN_NAME = "PermissionsEx";
    private final Logger log;
    private final PluginManager manager;

    public PEXPermissions(Logger log, PluginManager manager) {
        this.log = log;
        this.manager = manager;
        // Not sure what we can test here.
        PermissionsEx ex = (PermissionsEx) manager.getPlugin(PLUGIN_NAME);
        PermissionManager pm = PermissionsEx.getPermissionManager();
    }

    public void addInheritance(String parent, String child) {
        if (manager.isPluginEnabled(PLUGIN_NAME)) {
            PermissionManager pm = PermissionsEx.getPermissionManager();
            pm.setWorldInheritance(child, new String[]{parent});
        }
    }

    public void removeInheritance(String parent, String child) {
        if (manager.isPluginEnabled(PLUGIN_NAME)) {
            PermissionManager pm = PermissionsEx.getPermissionManager();
            pm.setWorldInheritance(child, new String[0]);
        }
    }
}
