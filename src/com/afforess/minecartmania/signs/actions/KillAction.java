package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class KillAction extends SignAction {

	@Override
	public boolean execute(MinecartManiaMinecart minecart) {
		minecart.kill(com.afforess.minecartmania.config.Settings.ReturnCartsToOwner);
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
		return "Kill Sign";
	}

}
