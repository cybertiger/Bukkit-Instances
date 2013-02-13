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
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 1) {
            return null; // Usage
        }
        Party party = instances.getParty(player);
        if (party == null) {
            return error("You are not in a party.");
        }
        if (instances.isLeaderInvite() && !player.equals(party.getLeader())) {
            return error("You are not the party leader.");
        }
        Player invitee = instances.getServer().getPlayer(args[0]);
        if (invitee == null) {
            return error("No such player: " + args[0] + ".");
        }
        if (!invitee.isOnline()) {
            return error(args[0] + " is not online.");
        }
        boolean invited = party.getInvites().contains(invitee);
        boolean member = party.getMembers().contains(invitee);
        if (member) {
            return error(args[0] + " is already a party member.");
        }
        if (!invited) {
            return error(args[0] + " is not invited.");
        }
        party.getInvites().remove(invitee);
        party.emote(instances, invitee, " has been uninvited by " + player.getDisplayName() + '.');
        invitee.sendMessage("You are no longer invited to join party " + party.getName() + '.');
        return Collections.emptyList();
    }
}
