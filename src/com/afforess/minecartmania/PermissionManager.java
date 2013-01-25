package com.afforess.minecartmania;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;

public class PermissionManager {
	
	//private PermissionHandler handler = null;
	public PermissionManager(Server server) {		
		MinecartManiaLogger.getInstance().log("Using Bukkit permissions.");
	}
	
	public boolean isHasPermissions() {
		return true;
	}
	
	public boolean canCreateSign(Player player, String sign) {
		if (player.isOp()) {
			return true;
		}
		if (isHasPermissions()) {
			return player.hasPermission("minecartmania.signs.create." + sign);
		}
		return true;
	}
	
	public boolean canBreakSign(Player player, String sign) {
		if (player.isOp()) {
			return true;
		}
		if (isHasPermissions()) {
			return player.hasPermission("minecartmania.signs.break." + sign);
		}
		return true;
	}
	
	public boolean canUseAdminCommand(Player player, String command) {
		if (player.isOp()) {
			return true;
		}
		if (isHasPermissions()) {
			return player.hasPermission("minecartmania.commands.create." + command.toLowerCase());
		}
		return false;
	}
	
	public boolean canUseCommand(Player player, String command) {
		if (player.isOp()) {
			return true;
		}
		if (isHasPermissions()) {
			return player.hasPermission("minecartmania.commands." + command.toLowerCase());
		}
		return true;
	}

}
