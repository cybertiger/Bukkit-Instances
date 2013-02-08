/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.EnumSet;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public abstract class AbstractCommand implements Command {
    private final EnumSet<SenderType> target;
  
    public AbstractCommand(SenderType type) {
        this(EnumSet.of(type));
    }

    public AbstractCommand() {
        this(EnumSet.allOf(SenderType.class));
    }

    public AbstractCommand(EnumSet<SenderType> target) {
        this.target = target;
    }

    public boolean availableTo(SenderType type) {
        return target.contains(type);
    }

    public abstract List<String> execute(Instances instances, CommandSender sender, String[] args);

}
