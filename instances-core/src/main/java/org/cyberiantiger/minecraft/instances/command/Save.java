/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class Save extends AbstractCommand {

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0)
            return null;
        instances.save();
        return msg("Instance settings saved.");
    }

}
