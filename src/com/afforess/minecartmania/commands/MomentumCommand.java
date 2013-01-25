package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class MomentumCommand extends MinecartManiaCommand {

	public boolean isPlayerOnly() {
		return true;
	}

	public CommandType getCommand() {
		return CommandType.Momentum;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (player.getVehicle() instanceof Minecart) {
			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)player.getVehicle());
			player.sendMessage(LocaleParser.getTextKey("AdminControlsMomentum", (float)minecart.getMotionX(), (float)minecart.getMotionY(), (float)minecart.getMotionZ()));
		}
		else {
			player.sendMessage(LocaleParser.getTextKey("AdminControlsMomentumInvalid"));
		}
		return true;
	}

}
