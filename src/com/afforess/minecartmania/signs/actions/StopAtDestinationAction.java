package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class StopAtDestinationAction implements SignAction{
	protected String station = null;
	public StopAtDestinationAction(Sign sign) {
		
		boolean found = false;
		for (String line : sign.getLines()) {
			if (found) {
				station = StringUtils.removeBrackets(line);
				sign.addBrackets();
				break;
			}
			if (line.toLowerCase().contains("station stop")) {
				found = true;
			}
		}
		
	}

	
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

	
	public boolean valid(Sign sign) {
		return station != null;
	}

	
	public String getName() {
		return "stopatdestinationsign";
	}

	
	public String getFriendlyName() {
		return "Stop At Destination Sign";
	}

}
