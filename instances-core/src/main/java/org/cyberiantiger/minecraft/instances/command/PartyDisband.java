/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Party;

/**
 *
 * @author antony
 */
public class PartyDisband extends AbstractCommand {

    public PartyDisband() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0) {
            return null; // Usage
        }
        Party party = instances.getParty(player);
        if (party == null) {
            throw new InvocationException("You are not in a party.");
        }

        if (!party.hasLeader()) {
            throw new InvocationException("You cannot disband leaderless parties.");
        }

        if (instances.isLeaderDisband() && !party.getLeader().equals(player))  {
            throw new InvocationException("You are not the party leader.");
        }
        party.emote(instances, player, "has disbanded the party.");
        instances.partyDisband(party);
        return msg();
    }
}
