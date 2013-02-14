/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.permissions;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author antony
 */
public class PEXPermissions implements Permissions {

    public void addInheritance(String parent, String child) {
        PermissionManager permissionManager = PermissionsEx.getPermissionManager();
        permissionManager.setWorldInheritance(child, new String[] {parent});
    }

    public void removeInheritance(String parent, String child) {
        PermissionManager permissionManager = PermissionsEx.getPermissionManager();
        permissionManager.setWorldInheritance(child, new String[0]);
    }

}
