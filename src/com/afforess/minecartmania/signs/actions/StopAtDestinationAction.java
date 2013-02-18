package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class StopAtDestinationAction extends SignAction{
	protected String station = null;

	public boolean execute(MMMinecart minecart) {
		if (minecart.getDestination().equalsIgnoreCase(station)) {
			minecart.stopCart();
			minecart.setDestination("");
			if (minecart.hasPlayerPassenger() ) minecart.getPlayerPassenger().sendMessage(Settings.getLocal("SignCommandsDestination"));
			return true;
		}

		return false;
	}

	public boolean async() {
		return false;
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
		return "Stop At Destination";
	}

}
