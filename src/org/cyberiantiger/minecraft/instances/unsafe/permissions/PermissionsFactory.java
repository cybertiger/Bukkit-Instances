/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.permissions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class PermissionsFactory {

    public static Permissions createPermissions(Instances instances) {
        PluginManager pm = instances.getServer().getPluginManager();
        Logger log = instances.getLogger();
        if (pm.isPluginEnabled(PEXPermissions.PLUGIN_NAME)) {
            log.info("Found PermissionsEx, attempting to create permission interface.");
            try {
                return new PEXPermissions(instances.getLogger(), instances.getServer().getPluginManager());
            } catch (Exception e) {
                log.log(Level.WARNING, "Error creating PermissionsEx permissions interface", e);
            } catch (Error e) {
                log.log(Level.WARNING, "Error creating PermissionsEx permissions interface", e);
            }
        }
        log.info("Disabling Instances permissions support");
        return new FakePermissions();
    }
}
