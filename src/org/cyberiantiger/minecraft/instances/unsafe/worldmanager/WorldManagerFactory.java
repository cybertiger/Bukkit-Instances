/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.worldmanager;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class WorldManagerFactory {

    public static WorldManager createWorldManager(Instances instances) {
        try {
            MultiverseCore core = (MultiverseCore) instances.getServer().getPluginManager().getPlugin("Multiverse-Core");
            return new MultiverseCoreWorldManager(instances, core);
        } catch (Error e) {
        } catch (Exception e) {
        }
        return new FakeWorldManager();
    }
}
