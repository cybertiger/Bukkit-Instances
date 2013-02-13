/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.PortalPair;

/**
 *
 * @author antony
 */
public class PortalList extends AbstractCommand {

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0)
            return null;
        List<String> ret = new ArrayList<String>();
        List<PortalPair> portals = new ArrayList<PortalPair>(instances.getPortalPairs());
        if (portals.isEmpty()) {
            ret.add("No portals defined.");
        } else {
            for (PortalPair pair : portals) {
                ret.add(pair.toString());
            }
        }
        return msg(ret);
    }

}
