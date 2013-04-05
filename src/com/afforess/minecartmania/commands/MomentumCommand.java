package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;

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
			MMMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)player.getVehicle());
			
			player.sendMessage(Settings.getLocal("AdminControlsMomentum", (minecart.getMotionX() / .4)* 100, (minecart.getMotionY()/.4)*100, (minecart.getMotionZ()/.4)*100));
			
		}
		else {
			player.sendMessage(Settings.getLocal("AdminControlsMomentumInvalid"));
		}
		return true;
	}

}
