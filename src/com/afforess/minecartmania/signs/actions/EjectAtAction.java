package com.afforess.minecartmania.signs.actions;


import org.bukkit.Location;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class EjectAtAction extends SignAction  {
	Location teleport = null;
	boolean here = false;

	public boolean execute(MMMinecart minecart) {
		if (minecart.getPassenger() == null) {
			return false;
		}

		if(here && loc !=null){
			teleport = loc.clone();
			teleport.setYaw(0);
			teleport.setPitch(0);
		}


		teleport.setWorld(minecart.getWorld());
		minecart.setDataValue("Eject At", teleport);

		return true;
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
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
		else if (lines[0].toLowerCase().contains("[eject here")){
			here = true;	
			return true;
		}

		return false;
	}


	public String getPermissionName() {
		return "ejectatsign";
	}


	public String getFriendlyName() {
		String loc = ((teleport == null) ? "Location" : (teleport.getBlockX() + "," + teleport.getBlockY() + "," + teleport.getBlockZ()));
		return "Eject At " + loc  ;
	}



}
