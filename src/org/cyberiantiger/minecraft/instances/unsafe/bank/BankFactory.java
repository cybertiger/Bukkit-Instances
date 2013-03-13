/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.bank;

import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class BankFactory {

    public static Bank createBank(Instances instances) {
        PluginManager pm = instances.getServer().getPluginManager();
        Logger log = instances.getLogger();
        if (pm.isPluginEnabled(VaultBank.PLUGIN_NAME)) {
            log.info("Found Vault, attempting to create banking interface.");
            try {
                return new VaultBank(instances.getLogger(), instances);
            } catch (Exception e) {
                log.log(Level.WARNING, "Error creating Vault banking interface", e);
            } catch (Error e) {
                log.log(Level.WARNING, "Error creating Vault banking interface", e);
            }
        }
        if (pm.isPluginEnabled(MultiverseCoreBank.PLUGIN_NAME)) {
            log.info("Found Multiverse-Core, attempting to create banking interface.");
            try {
                return new MultiverseCoreBank(instances.getLogger(), instances.getServer().getPluginManager());
            } catch (Exception e) {
                log.log(Level.WARNING, "Error creating Multiverse-Core banking interface", e);
            } catch (Error e) {
                log.log(Level.WARNING, "Error creating Multiverse-Core banking interface", e);
            }
        }
        log.info("Disabling bank support.");
        return new FakeBank();
    }
}
