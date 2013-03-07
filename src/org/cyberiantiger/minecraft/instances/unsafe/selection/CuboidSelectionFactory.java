/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.selection;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class CuboidSelectionFactory {

    public static CuboidSelection createCuboidSelection(Instances instances) {
        Logger log = instances.getLogger();
        PluginManager pm = instances.getServer().getPluginManager();
        if (pm.isPluginEnabled(WorldEditCuboidSelection.PLUGIN_NAME)) {
            log.info("Creating CuboidSelection interface for WorldEdit.");
            try {
                return new WorldEditCuboidSelection(log, pm);
            } catch (Exception e) {
                log.log(Level.WARNING, "Error creating CuboidSelection interface for WorldEdit", e);
            } catch (Error e) {
                log.log(Level.WARNING, "Error creating CuboidSelection interface for WorldEdit", e);
            }
        }
        log.info("Using in built CuboidSelection tool.");
        return new InstancesCuboidSelection(instances);
    }
}
