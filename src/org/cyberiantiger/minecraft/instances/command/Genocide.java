/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class Genocide extends AbstractCommand {
    public Genocide() {
        super (SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 1 && args.length != 2)
            return null;

        EntityType type = EntityType.fromName(args[0]);
        boolean remove = false;
        if (args.length == 2) {
            if ("remove".equals(args[1])) {
                remove = true;
            } else {
                return null;
            }
        }

        if (type == null || !type.isAlive()) {
            throw new InvocationException("Not a valid entity: " + args[0]);
        }

        for (Entity e : player.getWorld().getEntities()) {
            if (e.getType() == type) {
                LivingEntity living = (LivingEntity) e;
                if (remove) {
                    living.remove();
                } else {
                    living.damage(living.getHealth());
                }
            }
        }
        return msg(args[0] + " has been purged from the world.");
    }

}
