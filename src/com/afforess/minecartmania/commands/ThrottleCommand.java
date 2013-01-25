package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class ThrottleCommand extends MinecartManiaCommand{

	public boolean isPlayerOnly() {
		return true;
	}

	public CommandType getCommand() {
		return CommandType.Throttle;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(LocaleParser.getTextKey("AdminControlsThrottleUsage"));
			return true;
		}
		Player player = (Player)sender;
		if (player.getVehicle() != null && player.getVehicle() instanceof Minecart) {
			try {
				String num = StringUtils.getNumber(args[0]);
				double throttle = Double.valueOf(num);
				if (throttle >= 0.0D) {
					MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)player.getVehicle());
					minecart.setDataValue("throttle", throttle);
					if (throttle <= 100D) 
						sender.sendMessage(LocaleParser.getTextKey("AdminControlsThrottleSet"));
					else
						sender.sendMessage(LocaleParser.getTextKey("AdminControlsThrottleSetOverdrive"));
				}
				else {
					sender.sendMessage(LocaleParser.getTextKey("AdminControlsThrottleUsage"));
				}
			}
			catch (Exception e) {
				sender.sendMessage(LocaleParser.getTextKey("AdminControlsThrottleUsage"));
			}
			return true;
		}
		sender.sendMessage(LocaleParser.getTextKey("AdminControlsThrottleUsage"));
		return true;
	}

}
