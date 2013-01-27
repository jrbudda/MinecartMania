package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;

public class ReloadCommand extends MinecartManiaCommand  {

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.Reload;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		com.afforess.minecartmania.MinecartMania.getInstance().reloadMyConfig();
		
		sender.sendMessage(LocaleParser.getTextKey("AdminReload"));
		return true;
	}

	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return true;
	}

}
