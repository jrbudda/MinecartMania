package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.config.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class HelpCommand extends MinecartManiaCommand {

    public boolean isPlayerOnly() {
        return false;
    }

    public CommandType getCommand() {
        return CommandType.Help;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        StringBuilder sb = new StringBuilder();

        for (CommandType c : CommandType.values()) {
            sb.append(", " + c.toString());
        }

        sender.sendMessage(Settings.getLocal("AdminControlsHelp") + sb.toString());

        return true;

    }

}
