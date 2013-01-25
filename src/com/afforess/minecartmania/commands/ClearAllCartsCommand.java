package com.afforess.minecartmania.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class ClearAllCartsCommand extends MinecartManiaCommand implements ClearMinecartCommand {

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.ClearAllCarts;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int distance = -1;
		boolean delete = false;
		if (args.length > 0) {
			for (String arg : args) {
				try {
					distance = Integer.parseInt(StringUtils.getNumber(args[0]));
				}
				catch (Exception e) {
					delete = delete || arg.contains("-d");
				}
			}
		}
		
		Vector location = null;
		if (sender instanceof Player) {
			location = ((Player)sender).getLocation().toVector();
		}
		else {
			distance = -1;
		}
		MinecartManiaWorld.pruneMinecarts();
		
		int count = 0;
		ArrayList<MinecartManiaMinecart> minecartList = MinecartManiaWorld.getMinecartManiaMinecartList();
		for (MinecartManiaMinecart minecart : minecartList) {
			if (!minecart.isDead() && !minecart.isDead()) {
				if (distance < 0 || (minecart.getLocation().toVector().distance(location) < distance)) {
					if (shouldRemoveMinecart(minecart)) {
						count++;
						minecart.kill(!delete);
					}
				}
			}
		}
		sender.sendMessage(LocaleParser.getTextKey("AdminControlsMinecartsRemoved", count));
		return true;
	}

	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return true;
	}

}
