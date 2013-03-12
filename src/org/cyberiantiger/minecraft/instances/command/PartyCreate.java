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
public class PartyCreate extends AbstractCommand {

    public PartyCreate() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if(args.length != 1)
            return null; // Usage;

        Party party = instances.getParty(player);
        if (party != null) {
            throw new InvocationException("You are already in a party.");
        }

        party = instances.getParty(args[0]);
        if (party != null) {
            throw new InvocationException("That party already exists.");
        }

        party = instances.partyCreate(args[0], player);

        return msg("Party " + args[0] + " created.");
    }
}
