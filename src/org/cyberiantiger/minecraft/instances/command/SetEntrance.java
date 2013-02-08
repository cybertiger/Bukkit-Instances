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
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0)
            return null;
        Player player = (Player) sender;
        Selection selection = instances.getSelection(player);
        if (!selection.isValid()) {
            return Collections.singletonList("You do not currently have a valid selection.");
        }
        if (instances.isInstance(selection.getWorld())) {
            return Collections.singletonList("You cannot create instance portals inside an instance.");
        }

        instances.getSession(player).setEntrance();

        return Collections.singletonList("Entrance portal location set.");
    }


}
