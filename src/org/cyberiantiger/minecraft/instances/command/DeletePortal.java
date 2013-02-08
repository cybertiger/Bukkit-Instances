/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.PortalPair;

/**
 *
 * @author antony
 */
public class DeletePortal extends AbstractCommand {

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }
        PortalPair pair = instances.getPortalPair(args[0]);
        if (pair == null) {
            return Collections.singletonList("Portal " + args[0] + " not found.");
        }
        instances.removePortalPair(pair);
        return Collections.singletonList("Portal " + args[0] + " deleted.");
    }

}
