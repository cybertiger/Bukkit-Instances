/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.selection;

import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Cuboid;

/**
 *
 * @author antony
 */
public interface CuboidSelection {

    public Cuboid getCurrentSelection(Player player);

    public boolean isNative();

}
