package com.afforess.minecartmania.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.utils.MinecartUtils;


public class EjectCommand extends MinecartManiaCommand{

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.Eject;
	}
	
	public boolean isPermenantEject() {
		return false;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			List<Player> matchingPlayers = com.afforess.minecartmania.MinecartMania.getInstance().getServer().matchPlayer(args[0]);
			if (!matchingPlayers.isEmpty()) {
				for (Player p : matchingPlayers) {
					if (p.isInsideVehicle())
						p.leaveVehicle();
					if (isPermenantEject()) {
						MinecartUtils.toggleBlockFromEntering(p);
					}
				}
			}
			else {
				sender.sendMessage(Settings.getLocal("AdminControlsNoPlayerFound"));
			}
		}
		else {
			sender.sendMessage(Settings.getLocal("AdminControlsEjectUsage"));
		}
		return true;
	}
}
