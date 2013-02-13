/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.InstanceDestinationPortal;
import org.cyberiantiger.minecraft.instances.InstanceEntrancePortal;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.PortalPair;
import org.cyberiantiger.minecraft.instances.Session;

/**
 *
 * @author antony
 */
public class CreatePortal extends AbstractCommand {

    public CreatePortal() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 1) {
            return null;
        }
        Session session = instances.getSession(player);

        if (session.getEntrance() == null || !session.getEntrance().isValid()) {
            throw new InvocationException("You have not set the entrance portal location.");
        }
        if (session.getDestination() == null || !session.getDestination().isValid()) {
            throw new InvocationException("You have not set the destination portal location.");
        }
        if (instances.getPortalPair(args[0]) != null) {
            throw new InvocationException("That portal pair already exists.");
        }
        InstanceEntrancePortal entrance = new InstanceEntrancePortal(session.getEntrance().getCuboid());
        InstanceDestinationPortal destination = new InstanceDestinationPortal(session.getDestination().getCuboid());
        PortalPair pair = new PortalPair(args[0], entrance, destination);
        instances.addPortalPair(pair);
        session.clear();
        return msg("Portal " + args[0] + " created.");
    }

}
