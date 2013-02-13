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
import org.cyberiantiger.minecraft.instances.util.StringUtil;

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

    public List<String> execute(Instances instances, SenderType type, CommandSender sender, String[] args) {
        if (!availableTo(type)) throw new NotAvailableException();
        return execute(instances, sender, args);
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
    
    public static List<String> msg(String msg) {
        return Collections.singletonList(msg);
    }

    public static List<String> msg(String[] msg) {
        return Arrays.asList(msg);
    }

    public static List<String> msg() {
        return Collections.emptyList();
    }

    public static String concatonate(String[] args) {
        return concatonate(args, 0, args.length);
    }

    public static String concatonate(String[] args, int offset, int length) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            ret.append(args[offset+i]);
            if (i != length - 1) {
                ret.append(' ');
            }
        }
        return ret.toString();
    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String[] shift(String[] args, int amount) {
        if (amount > args.length) {
            throw new IllegalArgumentException();
        }
        if (amount == args.length) {
            return EMPTY_STRING_ARRAY;
        }
        String[] ret = new String[args.length - amount];
        System.arraycopy(args, amount, ret, 0, ret.length);
        return ret;
    }
}
