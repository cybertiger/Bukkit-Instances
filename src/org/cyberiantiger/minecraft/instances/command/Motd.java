/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class Motd extends AbstractCommand {

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0)
            return null;
        String[] motd = instances.getMotd();
        if (motd == null) {
            return Collections.singletonList("No MOTD set, create one in plugins/Instances/motd.txt");
        }
        return Arrays.asList(motd);
    }

}
