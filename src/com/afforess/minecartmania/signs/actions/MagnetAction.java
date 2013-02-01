package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class MagnetAction extends SignAction {

	private boolean off = false;

	@Override
	public boolean execute(MMMinecart minecart) {
		minecart.setMagnetic(!off);
		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[magnet")) {
				String[] split = line.split(":");
				if (split.length != 2) {
					return true;
				}
				off = split[1].toLowerCase().contains("off");
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "magnetsign";
	}

	@Override
	public String getFriendlyName() {
		return "Magnet";
	}

}
