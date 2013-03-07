/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.inventories;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class InventoriesFactory {

    public static Inventories createInventories(Instances instances) {
        PluginManager pm = instances.getServer().getPluginManager();
        Logger log = instances.getLogger();
        if (pm.isPluginEnabled(MultiverseInventories.PLUGIN_NAME)) {
            log.info("Enabling support for Multiverse-Inventories");
            try {
                return new MultiverseInventories(log, pm);
            } catch (Exception e) {
                log.log(Level.WARNING, "Error creating Multiverse-Inventories inventory interface", e);
            } catch (Error e) {
                log.log(Level.WARNING, "Error creating Multiverse-Inventories inventory interface", e);
            }
        }
        log.info("Disabling Inventory sharing support.");
        return new FakeInventories();
    }
}
