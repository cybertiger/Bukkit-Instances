/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
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

    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        switch (SenderType.getSenderType(sender)) {
            case BLOCK:
                return execute(instances, (BlockCommandSender) sender, args);
            case CONSOLE:
                return execute(instances, (ConsoleCommandSender) sender, args);
            case PLAYER:
                return execute(instances, (Player) sender, args);
            case RCONSOLE:
                return execute(instances, (RemoteConsoleCommandSender) sender, args);
        }
        return null;
    }

    public List<String> execute(Instances instances, BlockCommandSender sender, String[] args) {
        return null;
    }

    public List<String> execute(Instances instances, ConsoleCommandSender sender, String[] args) {
        return null;
    }

    public List<String> execute(Instances instances, Player sender, String[] args) {
        return null;
    }

    public List<String> execute(Instances instances, RemoteConsoleCommandSender sender, String[] args) {
        return null;
    }

    public List<String> error(String msg) {
        return Collections.singletonList("§4" + msg);
    }

    public List<String> msg(String msg) {
        return Collections.singletonList("§2" + msg);
    }

    public List<String> msg(String[] msg) {
        for (int i = 0; i < msg.length; i++) {
            msg[i] = "§2" + msg;
        }
        return Arrays.asList(msg);
    }

    public List<String> msg(List<String> msg) {
        ListIterator<String> i = msg.listIterator();
        while (i.hasNext()) {
            i.set("§2" + i.next());
        }
        return msg;
    }

    public List<String> msg() {
        return Collections.emptyList();
    }

    public String concatonate(String[] args) {
        return concatonate(args, 0, args.length);
    }

    public String concatonate(String[] args, int offset, int length) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            ret.append(args[offset+i]);
            if (i != length - 1) {
                ret.append(' ');
            }
        }
        return ret.toString();
    }
}
