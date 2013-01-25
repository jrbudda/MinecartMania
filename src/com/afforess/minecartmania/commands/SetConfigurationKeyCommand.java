package com.afforess.minecartmania.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class SetConfigurationKeyCommand extends MinecartManiaCommand {

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.SetConfigKey;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2) {
			sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKeyUsage"));
			return true;
		}
		String key = args[0];
		String value = args[1];
		if (value.equalsIgnoreCase("null")) {
			MinecartManiaWorld.setConfigurationValue(key, null);
			sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKey", key, "null"));
		}
		else {
			if (value.equalsIgnoreCase("true")) {
				sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKey", key, value));
				MinecartManiaWorld.setConfigurationValue(key, Boolean.TRUE);
			}
			else if (value.equalsIgnoreCase("false")) {
				sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKey", key, value));
				MinecartManiaWorld.setConfigurationValue(key, Boolean.FALSE);
			}
			else if (StringUtils.containsLetters(value)) {
				//save it as a string
				sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKey", key, value));
				MinecartManiaWorld.setConfigurationValue(key, value);
			}
			else {
				try {
					value = StringUtils.getNumber(value);
					Double d = Double.valueOf(value);
					if (d.intValue() == d) {
						//try to save it as an int
						sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKey", key, String.valueOf(d.intValue())));
						MinecartManiaWorld.setConfigurationValue(key, new Integer(d.intValue()));
					}
					else {
						//save it as a double
						sender.sendMessage(LocaleParser.getTextKey("AdminControlsSetConfigKey", key, String.valueOf(d.doubleValue())));
						MinecartManiaWorld.setConfigurationValue(key, d);
					}
				}
				catch (Exception e) {
					sender.sendMessage(LocaleParser.getTextKey("AdminControlsInvalidValue"));
				}
			}
		}
		return true;
	}

}
