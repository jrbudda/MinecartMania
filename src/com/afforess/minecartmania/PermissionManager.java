package com.afforess.minecartmania;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.debug.Logger;

public class PermissionManager {

	//private PermissionHandler handler = null;
	public PermissionManager(Server server) {		
		Logger.info("Using Bukkit permissions.");
	}


	public boolean canCreateSign(Player player, String sign) {
		if (player == null || player.isOp()) {
			return true;
		}
		//TODO do this properly in the plugin.yml
		return player.hasPermission("minecartmania.signs.create." + sign) || player.hasPermission("minecartmania.signs.create.*") ;

	}

	public boolean canBreakSign(Player player, String sign) {
		if (player == null || player.isOp()) {
			return true;
		}

		return player.hasPermission("minecartmania.signs.create." + sign) || player.hasPermission("minecartmania.signs.create.*");

	}

	public boolean canUseAdminCommand(Player player, String command) {
		if (player == null || player.isOp()) {
			return true;
		}

		return player.hasPermission("minecartmania.commands.create." + command.toLowerCase()) || player.hasPermission("minecartmania.commands.create.*");

	}

	public boolean canUseCommand(Player player, String command) {
		if (player == null || player.isOp()) {
			return true;
		}

		return player.hasPermission("minecartmania.commands." + command.toLowerCase()) || player.hasPermission("minecartmania.commands.create.*");

	}

}
