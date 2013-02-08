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
import org.cyberiantiger.minecraft.instances.Party;

/**
 *
 * @author antony
 */
public class PartyLeader extends AbstractCommand {

    public PartyLeader() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }
        Player player = (Player) sender;
        Party party = instances.getParty(player);
        if (party == null) {
            return Collections.singletonList("You are not in a party.");
        }
        if (!player.equals(party.getLeader())) {
            return Collections.singletonList("You are not the party leader.");
        }
        Player newLeader = instances.getServer().getPlayer(args[0]);
        if (newLeader == null) {
            return Collections.singletonList(args[0] + " not found.");
        }
        if (newLeader == player) {
            return Collections.singletonList("You are already the leader numbskull.");
        }
        if (!party.getMembers().contains(newLeader)) {
            return Collections.singletonList(args[0] + " is not in the party.");
        }
        party.setLeader(newLeader);
        StringBuilder line = new StringBuilder();
        line.append(instances.getPartyNamePrefix());
        line.append(party.getName());
        line.append(instances.getPartyNameSuffix());
        line.append(' ');
        line.append(args[0]);
        line.append(" has been appointed leader by ");
        line.append(player.getName());
        line.append('.');
        party.sendAll(line.toString());
        return Collections.emptyList();
    }
}
