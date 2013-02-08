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
public class Home extends AbstractCommand {

    public Home() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length != 0) {
            return null;
        }
        Player player = (Player) sender;
        Location home = instances.getHome((Player)sender);
        if (home == null) {
            return Collections.singletonList("You are homeless.");
        }
        player.teleport(home);
        return Collections.emptyList();
    }

}
