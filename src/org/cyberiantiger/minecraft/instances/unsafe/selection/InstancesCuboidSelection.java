/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.selection;

import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Cuboid;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Selection;
import org.cyberiantiger.minecraft.instances.command.InvocationException;

/**
 *
 * @author antony
 */
public class InstancesCuboidSelection implements CuboidSelection {
    private final Instances instances;

    public InstancesCuboidSelection(Instances instances) {
        this.instances = instances;
    }

    public Cuboid getCurrentSelection(Player player) {
        Selection sel = instances.getSelection(player);
        if (sel.isValid()) {
            return sel.getCuboid();
        } else {
            throw new InvocationException("You do not currently have a valid selection.");
        }
    }

    public boolean isNative() {
        return true;
    }
}
