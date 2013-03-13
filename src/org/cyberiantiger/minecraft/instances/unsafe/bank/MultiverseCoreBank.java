/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.bank;

import com.fernferret.allpay.multiverse.GenericBank;
import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author antony
 */
@Deprecated
public class MultiverseCoreBank implements Bank {
    public static final String PLUGIN_NAME = "Multiverse-Core";
    private final Logger log;
    private final PluginManager manager;
    
    public MultiverseCoreBank(Logger log, PluginManager manager) {
        this.log = log;
        this.manager = manager;
        // Do some stuff just to check it's working, and hopefully
        // Cause an exception if it is not.
        MultiverseCore core = (MultiverseCore) manager.getPlugin(PLUGIN_NAME);
        if (core == null) {
            throw new NullPointerException();
        }
        GenericBank bank = core.getBank();
        log.info("Muliverse-Core banking interface loaded, using: " + bank.getEconUsed());
    }

    public boolean deduct(Player player, double amount) {
        GenericBank bank = ((MultiverseCore) manager.getPlugin(PLUGIN_NAME)).getBank();
        if (bank.getBalance(player, -1) < amount)
            return false;
        bank.take(player, amount, -1);
        return true;
    }

}
