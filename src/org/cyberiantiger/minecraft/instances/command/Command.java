/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public interface Command {

    public List<String> execute(Instances instances, SenderType type, CommandSender sender, String[] args);

}
