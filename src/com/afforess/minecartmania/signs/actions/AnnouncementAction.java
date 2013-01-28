package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public class AnnouncementAction extends SignAction{

	protected String[] announcement;
	protected CompassDirection direction = CompassDirection.NO_DIRECTION;

	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if(direction == CompassDirection.NO_DIRECTION || minecart.getDirectionOfMotion() == direction){
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


	public boolean async() {
		return false;
	}


	public boolean process(String[] lines) {
		if (lines.length < 2) return false;
		if (lines[0].toLowerCase().contains("[announce")) {

			final String title = org.bukkit.ChatColor.translateAlternateColorCodes('&', Settings.AnnouncementPrefix);

			if(lines[0].contains(":")){
				String[] linesplit = lines[0].split(":");
				if (linesplit.length == 2){
					String dir = linesplit[1].toLowerCase();
					if (dir.contains("nor)")) direction = CompassDirection.NORTH;
					if (dir.contains("eas)")) direction = CompassDirection.EAST;
					if (dir.contains("wes)")) direction = CompassDirection.WEST;
					if (dir.contains("sou)")) direction = CompassDirection.SOUTH;
				}
			}

			announcement = new String[3];
			int line = 0;
			announcement[line] = title + lines[1];
			//! signifies a new line, otherwise continue message on same line

			if(lines.length <3) return true;
			if (lines[2].startsWith("!")) {
				line++;
				announcement[line] = '\n' + title + lines[2].substring(1);
			}
			else {
				announcement[line] += lines[2];
			}
			if(lines.length <4) return true;
			if (lines[3].startsWith("!")) {
				line++;
				announcement[line] = '\n' + title + lines[3].substring(1);
			}
			else {
				announcement[line] += lines[3];
			}
			return true;
		}
		return false;


	}


	public String getPermissionName() {
		return "announcementsign";
	}


	public String getFriendlyName() {
		return "Announcement Sign";
	}

}
