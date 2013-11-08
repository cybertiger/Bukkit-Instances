/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.Cuboid;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class SetEntrance extends AbstractCommand {
    public SetEntrance() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 0)
            return null;
        Cuboid cuboid = instances.getCuboidSelection().getCurrentSelection(player);
        if (instances.isInstance(instances.getServer().getWorld(cuboid.getWorld()))) {
            throw new InvocationException("You cannot create instance portals inside an instance.");
        }

        instances.getSession(player).setEntrance(cuboid);

        return msg("Entrance portal location set.");
    }


}
