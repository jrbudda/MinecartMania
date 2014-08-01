package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartMania;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class MinecartManiaCommand implements Command {

    public boolean canExecuteCommand(CommandSender sender) {
        if (!(sender instanceof Player) && isPlayerOnly()) {
            return false;
        }
        if (sender.isOp()) {
            //console is op
            return true;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        String command = this.getCommand().name();
        if (this.getCommand().isAdminCommand()) {
            return MinecartMania.permissions.canUseAdminCommand((Player) sender, command);
        }
        return MinecartMania.permissions.canUseCommand((Player) sender, command);
    }

    public boolean isValidCommand(String command, String[] args) {
        return command.equalsIgnoreCase(getCommand().name());
    }

}
