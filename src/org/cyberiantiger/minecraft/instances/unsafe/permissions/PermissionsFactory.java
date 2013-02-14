/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.permissions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class PermissionsFactory {
    public static Permissions createPermissions(Instances instances) {
        try {
            Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");
            return new PEXPermissions();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PermissionsFactory.class.getName()).log(Level.SEVERE, "Could not find PermissionsEx", ex);
        }

        return new FakePermissions();
    }
}
