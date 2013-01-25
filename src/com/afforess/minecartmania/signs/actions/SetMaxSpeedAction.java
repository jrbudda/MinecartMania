package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class SetMaxSpeedAction implements SignAction {
	
	protected int percent = -1;
	public SetMaxSpeedAction(Sign sign) {
		for (String line : sign.getLines()) {
			if (line.toLowerCase().contains("max speed")) {
				String[] split = line.split(":");
				if (split.length != 2) continue;
				double percent = Double.parseDouble(StringUtils.getNumber(split[1]));
				percent = Math.min(percent, Settings.getMaximumMinecartSpeedPercent());
				this.percent = (int)percent;
				sign.addBrackets();
				break;
			}
		}
	}

	
	
	public boolean execute(MinecartManiaMinecart minecart) {
		minecart.setMaxSpeed(0.4D * percent / 100);
		return true;
	}

	
	public boolean async() {
		return true;
	}

	
	public boolean valid(Sign sign) {
		return this.percent > 0;
	}

	
	public String getName() {
		return "maxspeedsign";
	}

	
	public String getFriendlyName() {
		return "Max Speed Sign";
	}

}
