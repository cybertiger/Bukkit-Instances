/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class Spawn extends AbstractCommand {
    public Spawn() {
        super (SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0)
            return null;
        instances.teleportToSpawn(player);
        return msg();
    }

}
