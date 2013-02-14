/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.bank;

import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.logging.Level;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class BankFactory {

    public static Bank createBank(Instances instances) {
        try {
            MultiverseCore core = (MultiverseCore) instances.getServer().getPluginManager().getPlugin("Multiverse-Core");
            return new MultiverseCoreBank(core.getBank());
        } catch (Error e) {
            instances.getLogger().log(Level.WARNING, "Could not find Multiverse-Core", e);
        } catch (Exception e) {
            instances.getLogger().log(Level.WARNING, "Could not find Multiverse-Core", e);
        }
        return new FakeBank();
    }
}
