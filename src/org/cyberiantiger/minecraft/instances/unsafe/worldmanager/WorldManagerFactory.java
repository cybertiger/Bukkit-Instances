/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.worldmanager;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class WorldManagerFactory {

    public static WorldManager createWorldManager(Instances instances) {
        Logger log = instances.getLogger();
        PluginManager pm = instances.getServer().getPluginManager();
        if (pm.isPluginEnabled(MultiverseCoreWorldManager.PLUGIN_NAME)) {
            log.info("Creating world manager interface for Multiverse-Core");
            try {
                return new MultiverseCoreWorldManager(log, pm);
            } catch (Exception e) {
                log.log(Level.WARNING, "Error creating Multiverse-Core world manager interface", e);
            } catch (Error e) {
                log.log(Level.WARNING, "Error creating Multiverse-Core world manager interface", e);
            }
        }
        log.info("Disabling worldmanager support.");
        return new FakeWorldManager();
    }
}
