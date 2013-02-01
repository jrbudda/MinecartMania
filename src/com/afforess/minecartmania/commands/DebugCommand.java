package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.DebugMode;
import com.afforess.minecartmania.debug.Logger;

public class DebugCommand extends MinecartManiaCommand{

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.Debug;
	}

	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		if (args.length == 1) {
			DebugMode mode = null;
			for (DebugMode m : DebugMode.values()) {
				if (args[0].equalsIgnoreCase(m.name())) {
					mode = m;
					break;
				}
			}
			if (mode != null) {
				Logger.switchDebugMode(mode);
				sender.sendMessage(Settings.getLocal("AdminControlsDebugMode", mode.name()));
				
			}
		}
		else {
			String modes = "";
			for (DebugMode m : DebugMode.values()) {
				modes += m.name().toLowerCase() + ", ";
			}
			modes.substring(0, modes.length() - 3);
			sender.sendMessage(Settings.getLocal("AdminControlsValidDebugModes", modes));
		}
		return true;
	}


}
