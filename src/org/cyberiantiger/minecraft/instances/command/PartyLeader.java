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
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 1) {
            return null;
        }
        Party party = instances.getParty(player);
        if (party == null) {
            throw new InvocationException("You are not in a party.");
        }
        if (!player.equals(party.getLeader())) {
            throw new InvocationException("You are not the party leader.");
        }
        Player newLeader = instances.getServer().getPlayer(args[0]);
        if (newLeader == null) {
            throw new InvocationException(args[0] + " not found.");
        }
        if (newLeader == player) {
            throw new InvocationException("You are already the leader numbskull.");
        }
        if (!party.getMembers().contains(newLeader)) {
            throw new InvocationException(args[0] + " is not in the party.");
        }
        party.setLeader(newLeader);
        party.emote(instances, newLeader, "has been appointed leader by " + player.getName());
        return msg();
    }
}
