package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class SetStationAction extends SignAction{
	protected String station = null;


	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).setLastStation(station);
			return true;
		}
		return false;
	}


	public boolean async() {
		return true;
	}


	public boolean process(String[] lines) {
		if (lines[0].contains("[set station:")) {
			String val[] = lines[0].split(":");

			station = "";

			if (val.length >= 2)	station += StringUtils.removeBrackets(val[1].trim());	
			//check following lines
			for (int i = 1 ; i < lines.length  ; i++){
				station += StringUtils.removeBrackets(lines[i].substring(1));
			}
		}

		return station != null;
	}




	public String getPermissionName() {
		return "setstationsign";
	}


	public String getFriendlyName() {
		return "Set Station Sign";
	}

}
