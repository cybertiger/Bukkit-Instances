/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.bank;

import org.bukkit.entity.Player;

/**
 *
 * @author antony
 */
public interface Bank {

    public boolean deduct(Player player, double amount);
    
}
