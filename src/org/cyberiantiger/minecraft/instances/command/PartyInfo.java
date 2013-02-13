/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instance;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Party;

/**
 *
 * @author antony
 */
public class PartyInfo extends AbstractCommand {

    public PartyInfo() {
        super();
    }

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0 && args.length != 1) {
            return null;
        }
        if (!(sender instanceof Player) && args.length == 0) {
            return Collections.singletonList("Only players can see information about their current party.");
        }
        Party party;
        if (args.length == 0) {
            party = instances.getParty((Player)sender);
            if (party == null)
                return error("You are not in a party.");
        } else {
            party = instances.getParty(args[0]);
            if (party == null)
                return error(args[0] + " does not exist.");
        }
        List<String> ret = new ArrayList<String>();
        ret.add("Party name: " + party.getName());
        ret.add("Party leader: " + party.getLeader().getName());
        StringBuilder tmp = new StringBuilder();
        tmp.append("Members:");
        for (Player p : party.getMembers()) {
            if (!p.equals(party.getLeader())) {
                tmp.append(' ');
                tmp.append(p.getName());
            }
        }
        ret.add(tmp.toString());
        if (sender.hasPermission("instances.party.info")) {
            tmp.setLength(0);
            tmp.append("Instances:");
            for (Instance i : party.getInstances()) {
                tmp.append(' ');
                tmp.append(i);
            }
            ret.add(tmp.toString());
        }
        return msg(ret);
    }
}
