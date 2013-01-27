package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;

public class ForceUnlockChestAction extends SignAction{
	protected boolean valid = false;

	public boolean execute(MinecartManiaMinecart minecart) {
		return valid;
	}

	public boolean async() {
		return false;
	}

	public boolean process(String[] lines) {

		for (String line : lines) {
			if (line.contains("[unlock chest")) {
				return true;
			}
		}
		return false;
	}

	public String getPermissionName() {
		return "forceunlockchestsign";
	}

	public String getFriendlyName() {
		return "Force Unlock Chest Sign";
	}
}
