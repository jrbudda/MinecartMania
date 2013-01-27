package com.afforess.minecartmania.signs.actions;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.EntityUtils;

public class EjectionAction extends SignAction{

	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.getPassenger() == null || loc == null) {
			return false;
		}
		Location location = EntityUtils.getValidLocation(loc.getBlock());
		if (location != null) {
			Entity passenger = minecart.getPassenger();
			location.setPitch(passenger.getLocation().getPitch());
			location.setYaw(passenger.getLocation().getYaw());
			minecart.eject();
			return passenger.teleport(location);
		}
		return false;
	}


	public boolean async() {
		return false;
	}


	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[eject") && !line.toLowerCase().contains("at")) {
				return true;
			}
		}
		return false;
	}


	public String getPermissionName() {
		return "ejectionsign";
	}


	public String getFriendlyName() {
		return "Ejection Sign";
	}

}
