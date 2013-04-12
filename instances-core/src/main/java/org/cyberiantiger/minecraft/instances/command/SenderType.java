/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author antony
 */
public enum SenderType {
    PLAYER("Player", "Players"),
    BLOCK("Block", "Blocks"),
    CONSOLE("Console", "Console"),
    RCONSOLE("Remote Console", "Remote Consoles"),
    UNKNOWN("Unknown", "Unknowns");
    private final String displayName;
    private final String pluralDisplayName;

    private SenderType(String displayName, String pluralDisplayName) {
        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPluralDisplayName() {
        return pluralDisplayName;
    }

    public static SenderType getSenderType(CommandSender sender) {
        if (sender instanceof Player) {
            return PLAYER;
        } else if (sender instanceof BlockCommandSender) {
            return BLOCK;
        } else if (sender instanceof ConsoleCommandSender) {
            return CONSOLE;
        } else if (sender instanceof RemoteConsoleCommandSender) {
            return RCONSOLE;
        } else {
            return UNKNOWN;
        }
    }
}
