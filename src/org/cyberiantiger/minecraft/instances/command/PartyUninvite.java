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
public class PartyUninvite extends AbstractCommand {

    public PartyUninvite() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            return null; // Usage
        }
        Party party = instances.getParty(player);
        if (party == null) {
            return Collections.singletonList("You are not in a party.");
        }
        if (instances.isLeaderInvite() && !player.equals(party.getLeader())) {
            return Collections.singletonList("You are not the party leader.");
        }
        Player invitee = instances.getServer().getPlayer(args[0]);
        if (invitee == null) {
            return Collections.singletonList("No such player: " + args[0] + ".");
        }
        if (!invitee.isOnline()) {
            return Collections.singletonList(args[0] + " is not online.");
        }
        boolean invited = party.getInvites().contains(invitee);
        boolean member = party.getMembers().contains(invitee);
        if (member) {
            return Collections.singletonList(args[0] + " is already a party member.");
        }
        if (!invited) {
            return Collections.singletonList(args[0] + " is not invited.");
        }
        party.getInvites().remove(invitee);
        StringBuilder line = new StringBuilder();
        line.append(instances.getPartyNamePrefix());
        line.append(party.getName());
        line.append(instances.getPartyNameSuffix());
        line.append(' ');
        line.append(args[0]);
        line.append(" has been uninvited by ");
        line.append(player.getName());
        line.append(".");
        party.sendAll(line.toString());
        invitee.sendMessage("You are no longer invited to join party " + party.getName());
        return Collections.emptyList();
    }
}
