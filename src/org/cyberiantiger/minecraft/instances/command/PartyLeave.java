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
public class PartyLeave extends AbstractCommand {

    public PartyLeave() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0) {
            return null;
        }
        Party party = instances.getParty(player);
        if (party == null) {
            throw new InvocationException("You are not in a party.");
        }
        if (player.equals(party.getLeader())) {
            throw new InvocationException("You must assign someone else as party leader or disband the party.");
        }
        party.emote(instances, player, "has left the party.");
        instances.partyRemove(party, player);
        return msg();
    }
}
