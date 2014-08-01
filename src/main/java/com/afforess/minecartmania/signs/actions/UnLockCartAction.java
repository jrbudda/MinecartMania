package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class UnLockCartAction extends SignAction {
	@Override
	public boolean execute(MMMinecart minecart) {
		
		if (minecart.isLocked() && minecart.hasPlayerPassenger()) {
			minecart.getPlayerPassenger().sendMessage(Settings.getLocal("SignCommandsMinecartUnlocked"));
		}
		
		minecart.setLocked(false);
		return true;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[unlock cart")){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public String getPermissionName() {
		return "unlockcart";
	}

	@Override
	public String getFriendlyName() {
		return "Unlock Cart";
	}

}
