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
public class PartyKick extends AbstractCommand {

    public PartyKick() {
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
        if (instances.isLeaderKick() && !player.equals(party.getLeader())) {
            return error("You are not the party leader.");
        }
        Player toKick = instances.getServer().getPlayer(args[0]);
        if (toKick == null) {
            return error(args[0] + " not found.");
        }
        if (!party.getMembers().contains(toKick)) {
            return error(args[0] + " is not in the party.");
        }
        party.emote(instances, toKick, " has been removed from the party by " + player.getName());
        instances.partyRemove(party, toKick);
        return msg();
    }
}
