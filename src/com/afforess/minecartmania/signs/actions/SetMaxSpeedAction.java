package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class SetMaxSpeedAction extends SignAction {
	
	protected int percent = -1;
	
	
	public boolean execute(MMMinecart minecart) {
		minecart.setMaxSpeed(0.4D * percent / 100);
		return true;
	}

	
	public boolean async() {
		return false;
	}

	
	public boolean process(String[] lines) {
		
		for (String line : lines) {
			if (line.toLowerCase().contains("max speed")) {
				String[] split = line.split(":");
				if (split.length != 2) return false;
				double percent = Double.parseDouble(StringUtils.getNumber(split[1]));
				percent = Math.min(percent, Settings.MaxAllowedSpeedPercent);
				this.percent = (int)percent;
				return true;
			}
		}
		
		return false;
	}

	
	public String getPermissionName() {
		return "maxspeedsign";
	}

	
	public String getFriendlyName() {
		return "Max Speed";
	}

}
