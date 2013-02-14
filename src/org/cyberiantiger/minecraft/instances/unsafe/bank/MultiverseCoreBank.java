/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.bank;

import com.fernferret.allpay.multiverse.GenericBank;
import org.bukkit.entity.Player;

/**
 *
 * @author antony
 */
public class MultiverseCoreBank implements Bank {
    private final GenericBank bank;
    
    public MultiverseCoreBank(GenericBank bank) {
        this.bank = bank;
    }

    public boolean deduct(Player player, double amount) {
        if (bank.getBalance(player, -1) < amount)
            return false;
        bank.take(player, amount, -1);
        return true;
    }

}
