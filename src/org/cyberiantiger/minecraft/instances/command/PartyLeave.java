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
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0) {
            return null;
        }
        Player player = (Player) sender;
        Party party = instances.getParty(player);
        if (party == null) {
            return Collections.singletonList("You are not in a party.");
        }
        if (player.equals(party.getLeader())) {
            return Collections.singletonList("You must assign someone else as party leader or disband the party.");
        }
        StringBuilder line = new StringBuilder();
        line.append(instances.getPartyNamePrefix());
        line.append(party.getName());
        line.append(instances.getPartyNameSuffix());
        line.append(' ');
        line.append(player.getName());
        line.append(" has left the party.");
        party.sendAll(line.toString());
        instances.partyRemove(party, player);
        return Collections.emptyList();
    }
}
