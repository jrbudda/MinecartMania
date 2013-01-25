package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaPlayer;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class StationCommand extends MinecartManiaCommand {

	public boolean isPlayerOnly() {
		return true;
	}

	public CommandType getCommand() {
		return CommandType.St;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length < 1) {
			return false;
		}
		MinecartManiaPlayer mmp = MinecartManiaWorld.getMinecartManiaPlayer(player);
		String station = args[0];
		mmp.setLastStation(station);
		if (args.length > 1) {
			if (args[1].contains("s")) {
				mmp.setDataValue("Reset Station Data", Boolean.TRUE);
			}
		}
		else {
			mmp.setDataValue("Reset Station Data", null);
		}
		mmp.sendMessage(LocaleParser.getTextKey("AdminControlsStation", station));
		return true;
	}

    
    public static boolean getAlwaysRememberStation() {
        return (Boolean)MinecartManiaWorld.getConfigurationValue("AlwaysRememberStation");
    }

}
