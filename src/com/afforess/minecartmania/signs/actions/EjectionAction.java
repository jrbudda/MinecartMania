package com.afforess.minecartmania.signs.actions;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.EntityUtils;

public class EjectionAction implements SignAction{
	
	private Location sign;
	public EjectionAction(Sign sign) {
		this.sign = sign.getLocation();
	}

	
	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.getPassenger() == null) {
			return false;
		}
		if (!ControlBlockList.isValidEjectorBlock(minecart)) {
			return false;
		}
		Location location = EntityUtils.getValidLocation(this.sign.getBlock());
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

	
	public boolean valid(Sign sign) {
		for (String line : sign.getLines()) {
			if (line.toLowerCase().contains("eject here")) {
				sign.addBrackets();
				return true;
			}
		}
		return false;
	}

	
	public String getName() {
		return "ejectionsign";
	}

	
	public String getFriendlyName() {
		return "Ejection Sign";
	}

}
