/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class SetHome extends AbstractCommand {

    public SetHome() {
        super (SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0)
            return null;
        Location home = player.getLocation();
        if (instances.isInstance(home.getWorld())) {
            throw new InvocationException("You cannot set your home in an instance.");
        }
        instances.setHome(player, home);
        return msg("Home set.");
    }

}
