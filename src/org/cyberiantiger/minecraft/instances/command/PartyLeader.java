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
            return error("You are not in a party.");
        }
        if (!player.equals(party.getLeader())) {
            return error("You are not the party leader.");
        }
        Player newLeader = instances.getServer().getPlayer(args[0]);
        if (newLeader == null) {
            return error(args[0] + " not found.");
        }
        if (newLeader == player) {
            return error("You are already the leader numbskull.");
        }
        if (!party.getMembers().contains(newLeader)) {
            return error(args[0] + " is not in the party.");
        }
        party.setLeader(newLeader);
        party.emote(instances, newLeader, " has been appointed leader by " + player.getName());
        return msg();
    }
}
