/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Party;

/**
 *
 * @author antony
 */
public class PartyList extends AbstractCommand {

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0) {
            return null;
        }

        StringBuilder ret = new StringBuilder();

        List<Party> parties = new ArrayList(instances.getParties());
        Collections.sort(parties);

        if (parties.isEmpty()) {
            throw new InvocationException("No parties");
        }
        StringBuilder tmp = new StringBuilder("Current parties: ");
        for (Party p : parties) {
             tmp.append(' ');
             tmp.append(p.getName());
        }
        return msg(tmp.toString());
    }

}
