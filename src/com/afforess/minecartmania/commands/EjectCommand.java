package com.afforess.minecartmania.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.listeners.VehicleListener;

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
						VehicleListener.toggleBlockFromEntering(p);
					}
				}
			}
			else {
				sender.sendMessage(LocaleParser.getTextKey("AdminControlsNoPlayerFound"));
			}
		}
		else {
			sender.sendMessage(LocaleParser.getTextKey("AdminControlsEjectUsage"));
		}
		return true;
	}
}
