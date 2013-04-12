/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class Cmd extends AbstractCommand {

    public Cmd() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0)
            return null;

        player.setItemInHand(new ItemStack(Material.COMMAND));
        return msg();
    }
}
