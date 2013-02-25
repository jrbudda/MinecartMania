package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class LaunchPlayerAction extends SignAction{
	private boolean reverse = false;
	public boolean execute(MMMinecart minecart) {
	if(minecart.hasPlayerPassenger()){
		minecart.launchCart(reverse);
		minecart.setDataValue("hold sign data", null);
		return true;
	}

	return false;
	}


	public boolean async() {
		return false;
	}


	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[launch pla")) {
				return true;
			}
			else if (line.toLowerCase().contains("[launch2 pla")) {
				reverse = true;
				return true;
			}
		}
		return false;
	}


	public String getPermissionName() {
		return "launchplayersign";
	}


	public String getFriendlyName() {
		return "Launch Player";
	}
	
	
}
