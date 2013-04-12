/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instance;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Party;

/**
 *
 * @author antony
 */
public class PartyJoin extends AbstractCommand {

    public PartyJoin() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 1) {
            return null;
        }
        Party party = instances.getParty(player);
        if (party != null) {
            throw new InvocationException("You are already in a party.");
        }
        party = instances.getParty(args[0]);
        if (party == null || !party.getInvites().contains(player)) {
            // Don't leak information about the existance or non-existance of the party.
            throw new InvocationException("You have not been invited to join " + args[0] + ".");
        }
        for (Instance instance : party.getInstances()) {
            if (instance.getPortal().getMaxPlayers() > 0 && instance.getPortal().getMaxPlayers() <= party.getMembers().size()) {
                party.emote(instances, player, "cannot join because the party is full.");
                throw new InvocationException("That party is full.");
            }
        }
        instances.partyAdd(party, player);
        party.emote(instances, player, "has joined the party.");
        return msg();
    }
}