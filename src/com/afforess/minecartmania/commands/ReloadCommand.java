package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.minecarts.MMMinecart;

public class ReloadCommand extends MinecartManiaCommand  {

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.Reload;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		com.afforess.minecartmania.MinecartMania.getInstance().reloadMyConfig();
		
		sender.sendMessage(Settings.getLocal("AdminReload"));
		return true;
	}

	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return true;
	}

}
