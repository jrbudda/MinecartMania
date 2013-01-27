package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class StationAction extends SignAction {

	@Override
	public boolean execute(MinecartManiaMinecart minecart) {
		//this is just a placeholder action for setting a block as a station block.
		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[station") && !line.toLowerCase().contains("set")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "stationsign";
	}

	@Override
	public String getFriendlyName() {
		return "Station Sign";
	}

}
