package com.afforess.minecartmania.signs.actions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.events.MinecartPassengerEjectEvent;
import com.afforess.minecartmania.signs.FailureReason;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class EjectAtAction extends SignAction  {
	Location teleport = null;


	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.getPassenger() == null) {
			return false;
		}
		if (!ControlBlockList.isValidEjectorBlock(minecart)) {
			return false;
		}
		Entity passenger = minecart.getPassenger();
		minecart.setDataValue("Eject At Sign", true);
		MinecartPassengerEjectEvent mpee = new MinecartPassengerEjectEvent(minecart, passenger);
		Bukkit.getServer().getPluginManager().callEvent(mpee);
		minecart.setDataValue("Eject At Sign", null);
		if (!mpee.isCancelled()) {
			minecart.eject();
			teleport.setWorld(minecart.getWorld());
			return passenger.teleport(teleport);
		}
		return false;
	}


	public boolean async() {
		return false;
	}


	public boolean process(String[] lines) {
		if (lines[0].toLowerCase().contains("[eject at")) {
			if (lines.length < 2) return false;

			try {
				String coords[] = StringUtils.removeBrackets(lines[1]).split(":");
				if (coords.length != 3) return false;

				double x = Double.parseDouble(coords[0].trim());
				double y = Double.parseDouble(coords[1].trim());
				double z = Double.parseDouble(coords[2].trim());
				teleport = new Location(null, x, y, z);

				if(lines.length <3) return true;

				String rotation[] = StringUtils.removeBrackets(lines[2]).split(":");
				if (coords.length != 2) return true;
				float pitch = 0;
				float yaw = 0;
				if (rotation.length > 1) {
					pitch = Float.parseFloat(rotation[1].trim());
					yaw = Float.parseFloat(rotation[0].trim());
				}
				else if (rotation.length > 0) {
					yaw = Float.parseFloat(rotation[0].trim());
				}
				teleport.setPitch(pitch);
				teleport.setYaw(yaw);

			}
			catch (Exception e) {
				return false;
			}
		}

		return (teleport != null );
	}


	public String getPermissionName() {
		return "ejectatsign";
	}


	public String getFriendlyName() {
		return "Eject At Sign";
	}



}
