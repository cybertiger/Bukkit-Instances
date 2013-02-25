/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.unsafe.NBTTools;
import org.cyberiantiger.nbt.CompoundTag;

/**
 *
 * @author antony
 */
public class Cmd extends AbstractCommand {

    public Cmd() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length == 0)
            return null;

        String method = args[0];

        Block target = player.getTargetBlock(null, 200);
        if (target == null) {
            throw new InvocationException("You must be looking at a block.");
        }
        if ("show".equals(method)) {
            if (args.length > 1)
                return null;
            if (target.getType() != Material.COMMAND) {
                throw new InvocationException("You must be looking at a command block.");
            }
            if (!player.hasPermission("instances.general.cmd.show")) {
                throw new InvocationException("Insufficient privileges.");
            }
            CompoundTag tag = NBTTools.readTileEntity(target);
            return msg("Command is: " + tag.getString("Command"));
        } else if ("set".equals(method)) {
            if (target.getType() != Material.COMMAND) {
                throw new InvocationException("You must be looking at a command block.");
            }
            args = shift(args,1);
            if (args.length > 0) {
                if (!player.hasPermission("instances.general.cmd.set." + args[0])) {
                    throw new InvocationException("Insufficient privileges to set command to: " + args[0]);
                }
            } else {
                if (!player.hasPermission("instances.general.cmd.reset")) {
                    throw new InvocationException("Insufficient privileges to reset command.");
                }
            }
            String command = concatonate(args);
            CompoundTag tag = NBTTools.readTileEntity(target);
            tag.setString("Command", command);
            NBTTools.writeTileEntity(target, tag);
            return msg("Command set to: " + command);
        } else if ("create".equals(method)) {
            if (args.length > 1)
                return null;
            if (!player.hasPermission("instances.general.cmd.create")) {
                throw new InvocationException("Insufficient privileges");
            }
            target.setTypeIdAndData(Material.COMMAND.getId(), (byte)0, true);
            return msg();
        } else {
            return null;
        }
    }
}
