/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Selection;

/**
 *
 * @author antony
 */
public class SetEntrance extends AbstractCommand {
    public SetEntrance() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0)
            return null;
        Selection selection = instances.getSelection(player);
        if (!selection.isValid()) {
            throw new InvocationException("You do not currently have a valid selection.");
        }
        if (instances.isInstance(selection.getWorld())) {
            throw new InvocationException("You cannot create instance portals inside an instance.");
        }

        instances.getSession(player).setEntrance();

        return msg("Entrance portal location set.");
    }


}
