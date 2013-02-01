package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.utils.StringUtils;

public class ThrottleCommand extends MinecartManiaCommand{

	public boolean isPlayerOnly() {
		return true;
	}

	public CommandType getCommand() {
		return CommandType.Throttle;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(Settings.getLocal("AdminControlsThrottleUsage"));
			return true;
		}
		Player player = (Player)sender;
		if (player.getVehicle() != null && player.getVehicle() instanceof Minecart) {
			try {
				String num = StringUtils.getNumber(args[0]);
				double throttle = Double.valueOf(num);
				if (throttle >= 0.0D) {
					MMMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)player.getVehicle());

					minecart.setMotion(minecart.getDirection(), throttle);
					sender.sendMessage(Settings.getLocal("AdminControlsThrottleSet"));
				}
				else {
					sender.sendMessage(Settings.getLocal("AdminControlsThrottleUsage"));
				}
			}
			catch (Exception e) {
				sender.sendMessage(Settings.getLocal("AdminControlsThrottleUsage"));
			}
			return true;
		}
		sender.sendMessage(Settings.getLocal("AdminControlsThrottleUsage"));
		return true;
	}

}
