package com.afforess.minecartmania.utils;

import java.util.Set;

import org.bukkit.util.Vector;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.events.MinecartIntersectionEvent;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.actions.StationAction;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class StationUtil {

	public static boolean isPromptUserAtAnyIntersection() {
		return Settings.IntersectionPromptsMode == 0;
	}

	public static boolean isStationIntersectionPrompt() {
		return  Settings.IntersectionPromptsMode== 1;
	}

	public static boolean isNeverIntersectionPrompt() {
		return 	 Settings.IntersectionPromptsMode== 2;
	}


	public static boolean shouldPromptUser(MMMinecart minecart, final MinecartIntersectionEvent event) {
		if (isNeverIntersectionPrompt() && minecart.getDataValue("Prompt Override") == null) {
			return false;
		}
		else {
			minecart.setDataValue("Prompt Override", null);
		}
		if (!minecart.hasPlayerPassenger()) {
			return false;
		}
		if (isStationIntersectionPrompt()) {
			if (!com.afforess.minecartmania.config.NewControlBlockList.hasSignAction(minecart.getBlockBeneath(), com.afforess.minecartmania.signs.actions.StationAction.class)) {
				return false;
			}			   
			// Player is riding in the minecart, but the intersection is handled by signs, so do not prompt
			if (event.isActionTaken())
				return false;
		}

		return true;
	}

	public static boolean isInQueue(MMMinecart minecart) {
		return minecart.getDataValue("queued velocity") != null;
	}

	public static void updateQueue(MMMinecart minecart) {
		//Test all 4 compass directions
		MMMinecart minecartBehind = minecart.getAdjacentMinecartFromDirection(DirectionUtils.CompassDirection.NORTH);
		if (minecartBehind == null) {
			minecartBehind = minecart.getAdjacentMinecartFromDirection(DirectionUtils.CompassDirection.EAST);
		}
		if (minecartBehind == null) {
			minecartBehind = minecart.getAdjacentMinecartFromDirection(DirectionUtils.CompassDirection.SOUTH);
		}
		if (minecartBehind == null) {
			minecartBehind = minecart.getAdjacentMinecartFromDirection(DirectionUtils.CompassDirection.WEST);
		}
		//restart the waiting queue behind us
		while (minecartBehind != null) {
			Vector velocity = (Vector)minecartBehind.getDataValue("queued velocity");
			if (velocity == null) {
				break;
			}
			minecartBehind.setMotion(velocity);
			minecartBehind.setDataValue("queued velocity", null);

			minecartBehind = minecartBehind.getMinecartBehind();
		}
	}

	public static Vector alterMotionFromDirection(DirectionUtils.CompassDirection direction, Vector oldVelocity) {
		double speed = Math.abs(oldVelocity.getX()) > Math.abs(oldVelocity.getZ()) ? Math.abs(oldVelocity.getX()) : Math.abs(oldVelocity.getZ());


		if (direction.equals(DirectionUtils.CompassDirection.WEST))
			return new Vector(-speed, 0, 0);
		if (direction.equals(DirectionUtils.CompassDirection.EAST))
			return new Vector(speed, 0, 0);
		if (direction.equals(DirectionUtils.CompassDirection.NORTH))
			return new Vector(0, 0, -speed);
		if (direction.equals(DirectionUtils.CompassDirection.SOUTH))
			return new Vector(0, 0, speed);

		return null;
	}

	public static String buildValidDirectionString(Set<CompassDirection> restricted) {
		String valid = "";
		boolean first = true;
		if (!restricted.contains(CompassDirection.NORTH)){
			if (!first) {
				valid += " or ";
			}
			valid += CompassDirection.NORTH.toString();
			first = false;
		}
		if (!restricted.contains(CompassDirection.EAST)){
			if (!first) {
				valid += " or ";
			}
			valid += CompassDirection.EAST.toString();
			first = false;
		}
		if (!restricted.contains(CompassDirection.SOUTH)){
			if (!first) {
				valid += " or ";
			}
			valid += CompassDirection.SOUTH.toString();
			first = false;
		}
		if (!restricted.contains(CompassDirection.WEST)){
			if (!first) {
				valid += " or ";
			}
			valid += CompassDirection.WEST.toString();
			first = false;
		}
		return valid;
	}

	public static boolean isValidDirection(CompassDirection facingDir, MMMinecart minecart) {
		Set<CompassDirection> restricted = StationAction.getRestrictedDirections(minecart);
		//Check if the direction is valid
		if (!MinecartUtils.validMinecartTrack(minecart.getLocation(), CompassDirection.NORTH)) {
				restricted.add(CompassDirection.NORTH);
		}
		if (!MinecartUtils.validMinecartTrack(minecart.getLocation(), CompassDirection.SOUTH)) {
					restricted.add(CompassDirection.SOUTH);	
		}
		if (!MinecartUtils.validMinecartTrack(minecart.getLocation(), CompassDirection.EAST)) {
				restricted.add(CompassDirection.EAST);	
		}
		if (!MinecartUtils.validMinecartTrack(minecart.getLocation(), CompassDirection.WEST)) {
				restricted.add(CompassDirection.WEST);	
		}
		
		if (restricted.contains(facingDir)){
			if (minecart.hasPlayerPassenger()) {
				minecart.getPlayerPassenger().sendMessage(Settings.getLocal("StationsInvalidDirection"));
				minecart.getPlayerPassenger().sendMessage(Settings.getLocal("StationsValidDirections", StationUtil.buildValidDirectionString(restricted)));
				return false;
			}
		}
		return true;
	}
}
