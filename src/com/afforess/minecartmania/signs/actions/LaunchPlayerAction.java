package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;

public class LaunchPlayerAction extends SignAction{

	public boolean execute(MinecartManiaMinecart minecart) {
		minecart.launchCart();
		minecart.setDataValue("hold sign data", null);
		return true;
	}


	public boolean async() {
		return true;
	}


	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[launch player")) {
				return true;
			}
		}
		return false;
	}


	public String getPermissionName() {
		return "launchplayersign";
	}


	public String getFriendlyName() {
		return "Launch Player Sign";
	}
	
	
}
