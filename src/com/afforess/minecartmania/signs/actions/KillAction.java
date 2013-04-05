package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class KillAction extends SignAction {

	@Override
	public boolean execute(MMMinecart minecart) {
		minecart.killOptionalReturn();
		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[kill")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "killsign";
	}

	@Override
	public String getFriendlyName() {
		return "Kill";
	}

}
