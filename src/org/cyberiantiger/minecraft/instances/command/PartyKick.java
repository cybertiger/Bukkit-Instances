/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Party;

/**
 *
 * @author antony
 */
public class PartyKick extends AbstractCommand {

    public PartyKick() {
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
        if (instances.isLeaderKick() && !player.equals(party.getLeader())) {
            return Collections.singletonList("You are not the party leader.");
        }
        Player toKick = instances.getServer().getPlayer(args[0]);
        if (toKick == null) {
            return Collections.singletonList(args[0] + " not found.");
        }
        if (!party.getMembers().contains(toKick)) {
            return Collections.singletonList(args[0] + " is not in the party.");
        }
        StringBuilder line = new StringBuilder();
        line.append(instances.getPartyNamePrefix());
        line.append(party.getName());
        line.append(instances.getPartyNameSuffix());
        line.append(' ');
        line.append(args[0]);
        line.append(" has been removed from the party by ");
        line.append(player.getName());
        line.append('.');
        party.sendAll(line.toString());
        instances.partyRemove(party, toKick);
        return Collections.emptyList();
    }
}
