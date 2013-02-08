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
public class PartyChat extends AbstractCommand {

    public PartyChat() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0)
            return null; // Usage
        Party party = instances.getParty(player);
        if (party == null)
            return Collections.singletonList("You are not in a party.");

        StringBuilder line = new StringBuilder();

        line.append(instances.getPartyNamePrefix());
        line.append(party.getName());
        line.append(' ');
        line.append(player.getName());
        line.append(instances.getPartyNameSuffix());
        for (String s : args) {
            line.append(' ');
            line.append(s);
        }
        party.sendAll(line.toString());
        return Collections.emptyList();
    }

}
