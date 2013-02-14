/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.inventories;

import java.util.logging.Level;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class InventoriesFactory {

    public static Inventories createInventories(Instances instances) {
        try {
            // Stupid class name conflicts.
            com.onarandombox.multiverseinventories.api.Inventories inventories =
                    (com.onarandombox.multiverseinventories.api.Inventories) instances.getServer().getPluginManager().getPlugin("Multiverse-Inventories");
            return new MultiverseInventories(inventories);
        } catch (Exception e) {
            instances.getLogger().log(Level.WARNING, "Could not find Multiverse-Core", e);
        } catch (Error e) {
            instances.getLogger().log(Level.WARNING, "Could not find Multiverse-Core", e);
        }
        return new FakeInventories();
    }
}
