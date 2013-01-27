package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class StopAtDestinationAction extends SignAction{
	protected String station = null;


	
	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if (MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getLastStation().equals(station)) {
				minecart.stopCart();
				minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("SignCommandsDestination"));
				return true;
			}
		}
		return false;
	}

	
	public boolean async() {
		return true;
	}

	
	public boolean process(String[]  lines) {
		boolean found = false;
		for (String line : lines) {
			if (found) {
				station = StringUtils.removeBrackets(line);
				break;
			}
			if (line.toLowerCase().contains("[station stop")) {
				found = true;
			}
		}
		
		return station != null;
	}

	
	public String getPermissionName() {
		return "stopatdestinationsign";
	}

	
	public String getFriendlyName() {
		return "Stop At Destination Sign";
	}

}
