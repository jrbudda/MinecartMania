package com.afforess.minecartmania.signs.actions;

import org.bukkit.Location;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public class AnnouncementAction implements SignAction{

	protected String[] announcement;
	protected Location sign;
	public AnnouncementAction(Sign sign) {
		this.sign = sign.getLocation();

		final String title = MinecartManiaWorld.getConfigurationValue("AnnouncementSignPrefixColor","&c").toString()
				+ MinecartManiaWorld.getConfigurationValue("AnnouncementSignPrefix","Announcement:").toString() + " "
				+ MinecartManiaWorld.getConfigurationValue("AnnouncementColor","yellow");
		announcement = new String[3];
		int line = 0;
		announcement[line] = title + sign.getLine(1);
		//! signifies a new line, otherwise continue message on same line
		if (sign.getLine(2).startsWith("!")) {
			line++;
			announcement[line] = '\n' + title + sign.getLine(2).substring(1);
		}
		else {
			announcement[line] += sign.getLine(2);
		}

		if (sign.getLine(3).startsWith("!")) {
			line++;
			announcement[line] = '\n' + title + sign.getLine(3).substring(1);
		}
		else {
			announcement[line] += sign.getLine(3);
		}
	}

	protected Sign getSign() {
		return SignManager.getSignAt(this.sign);
	}


	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if (isParallel(minecart.getLocation(), minecart.getDirection()) || isUnder(minecart.getLocation())) {
				for (int i = 0; i < 3; i++) {
					if (announcement[i] != null && !announcement[i].trim().isEmpty()) {
						minecart.getPlayerPassenger().sendMessage(announcement[i]);
					}
				}
				return true;
			}
		}
		return false;
	}

	protected boolean isParallel(Location location, CompassDirection exempt) {
		if (Math.abs(sign.getBlockY() - location.getBlockY()) > 2) {
			return false;
		}


		if ((exempt != CompassDirection.EAST) && (exempt != CompassDirection.WEST)) {
			if ((sign.getBlockX() != location.getBlockX()) && (sign.getBlockZ() == location.getBlockZ()))
				return ((sign.getBlockX() - 1) == location.getBlockX()) || ((sign.getBlockX() + 1) == location.getBlockX());
		}
		if ((exempt != CompassDirection.NORTH) && (exempt != CompassDirection.SOUTH)) {
			if ((sign.getBlockX() == location.getBlockX()) && (sign.getBlockZ() != location.getBlockZ()))
				return ((sign.getBlockZ() - 1) == location.getBlockZ()) || ((sign.getBlockZ() + 1) == location.getBlockZ());
		}

		return false;
	}

	protected boolean isUnder(Location location) {
		if (sign.getBlockX() != location.getBlockX()) {
			return false;
		}
		if (sign.getBlockZ() != location.getBlockZ()) {
			return false;
		}
		return true;
	}


	public boolean async() {
		return false;
	}


	public boolean valid(Sign sign) {
		if (sign.getLine(0).toLowerCase().contains("announce")) {
			sign.setLine(0, "[Announce]");
			return true;
		}
		return false;
	}


	public String getName() {
		return "announcementsign";
	}


	public String getFriendlyName() {
		return "Announcement Sign";
	}

}
