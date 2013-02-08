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
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        Player player = (Player)sender;
        if(args.length != 1)
            return null; // Usage;

        Party party = instances.getParty(player);
        if (party != null) {
            return Collections.singletonList("You are already in a party.");
        }

        party = instances.getParty(args[0]);
        if (party != null) {
            return Collections.singletonList("That party already exists.");
        }

        party = instances.createParty(args[0], player);

        return Collections.singletonList("Party " + args[0] + " created.");
    }
}
