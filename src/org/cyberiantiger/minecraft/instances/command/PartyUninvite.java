/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
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
            throw new InvocationException("You are not in a party.");
        }
        if (instances.isLeaderInvite() && !player.equals(party.getLeader())) {
            throw new InvocationException("You are not the party leader.");
        }
        Player invitee = instances.getServer().getPlayer(args[0]);
        if (invitee == null) {
            throw new InvocationException("No such player: " + args[0] + ".");
        }
        if (!invitee.isOnline()) {
            throw new InvocationException(args[0] + " is not online.");
        }
        boolean invited = party.getInvites().contains(invitee);
        boolean member = party.getMembers().contains(invitee);
        if (member) {
            throw new InvocationException(args[0] + " is already a party member.");
        }
        if (!invited) {
            throw new InvocationException(args[0] + " is not invited.");
        }
        party.getInvites().remove(invitee);
        party.emote(instances, invitee, "has been uninvited by " + player.getDisplayName() + '.');
        invitee.sendMessage("You are no longer invited to join party " + party.getName() + '.');
        return Collections.emptyList();
    }
}
