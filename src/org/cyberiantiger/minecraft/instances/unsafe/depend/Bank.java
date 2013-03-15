/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.util.Dependency;

/**
 *
 * @author antony
 */
public interface Bank extends Dependency {

    public boolean deduct(Player player, double amount);
    
}
