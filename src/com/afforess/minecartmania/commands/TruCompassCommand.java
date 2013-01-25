package com.afforess.minecartmania.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.afforess.minecartmaniacore.utils.DirectionUtils;

public class TruCompassCommand extends MinecartManiaCommand{

	public boolean isPlayerOnly() {
		return true;
	}

	public CommandType getCommand() {
		return CommandType.TruCompass;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		final int bearing = (int)((Player)sender).getLocation().getYaw();
		final int corrBearing = (bearing + 180 + 360) % 360;

		final DirectionUtils.CompassDirection facingDir = DirectionUtils.getDirectionFromRotation(bearing);
		sender.sendMessage(ChatColor.YELLOW + "Bearing: " + facingDir.toString() + " (" + corrBearing + " degrees)");
		return true;

	
	}

}
