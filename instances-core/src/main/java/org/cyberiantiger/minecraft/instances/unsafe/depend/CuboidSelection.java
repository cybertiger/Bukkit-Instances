/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.Cuboid;
import org.cyberiantiger.minecraft.instances.command.InvocationException;
import org.cyberiantiger.minecraft.instances.util.Dependency;

/**
 *
 * @author antony
 */
public interface CuboidSelection extends Dependency {

    public Cuboid getCurrentSelection(Player player) throws InvocationException;

    public boolean isNative();

}
