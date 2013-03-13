/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.bank;

import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class VaultBank implements Bank {
    public static final String PLUGIN_NAME = "Vault";
    private final Logger log;
    private final Instances instances;

    public VaultBank(Logger log, Instances instances) {
        this.log = log;
        this.instances = instances;
        RegisteredServiceProvider<Economy> provider = 
                instances.getServer().getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            throw new IllegalStateException("Vault provider not found, do you have vault installed and no economy plugins?");
        }
        Economy econ = provider.getProvider();
        log.info("Vault banking interface loaded, using: " + econ.getName());
    }

    public boolean deduct(Player player, double amount) {
        RegisteredServiceProvider<Economy> provider = 
                instances.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = provider.getProvider();
        EconomyResponse status = econ.bankWithdraw(player.getName(), amount);
        return status.transactionSuccess();
    }
}
