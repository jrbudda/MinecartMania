package com.afforess.minecartmaniacore.utils;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.events.MinecartIntersectionEvent;
import com.afforess.minecartmania.stations.SignCommands;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public class StationUtil {

	public static boolean isPromptUserAtAnyIntersection() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("IntersectionPrompts") == 0;
	}

	public static boolean isStationIntersectionPrompt() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("IntersectionPrompts") == 1;
	}

	public static boolean isNeverIntersectionPrompt() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("IntersectionPrompts") == 2;
	}

	public static boolean isStationCommandNeverResets() {
		return (Boolean)MinecartManiaWorld.getConfigurationValue("StationCommandSavesAfterUse");
	}

	public static boolean shouldPromptUser(MinecartManiaMinecart minecart, final MinecartIntersectionEvent event) {
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
			if (!ControlBlockList.isValidStationBlock(minecart)) {
				return false;
			}			   
			// Player is riding in the minecart, but the intersection is handled by signs, so do not prompt
			if (event.isActionTaken())
				return false;
		}

		return true;
	}

	public static boolean isInQueue(MinecartManiaMinecart minecart) {
		return minecart.getDataValue("queued velocity") != null;
	}

	public static void updateQueue(MinecartManiaMinecart minecart) {
		//Test all 4 compass directions
		MinecartManiaMinecart minecartBehind = minecart.getAdjacentMinecartFromDirection(DirectionUtils.CompassDirection.NORTH);
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

	public static String buildValidDirectionString(ArrayList<CompassDirection> restricted) {
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

	public static boolean isValidDirection(CompassDirection facingDir, MinecartManiaMinecart minecart) {
		ArrayList<CompassDirection> restricted = SignCommands.getRestrictedDirections(minecart);
		//Check if the direction is valid
		if (!MinecartUtils.validMinecartTrack(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.NORTH)) {
			if (!restricted.contains(CompassDirection.NORTH)) {
				restricted.add(CompassDirection.NORTH);
			}
		}
		if (!MinecartUtils.validMinecartTrack(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.SOUTH)) {
			if (!restricted.contains(CompassDirection.SOUTH)) {
				restricted.add(CompassDirection.SOUTH);
			}
		}
		if (!MinecartUtils.validMinecartTrack(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.EAST)) {
			if (!restricted.contains(CompassDirection.EAST)) {
				restricted.add(CompassDirection.EAST);
			}
		}
		if (!MinecartUtils.validMinecartTrack(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), 2, CompassDirection.WEST)) {
			if (!restricted.contains(CompassDirection.WEST)) {
				restricted.add(CompassDirection.WEST);
			}
		}
		if (restricted.contains(facingDir)){
			if (minecart.hasPlayerPassenger()) {
				minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("StationsInvalidDirection"));
				minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("StationsValidDirections", StationUtil.buildValidDirectionString(restricted)));
				return false;
			}
		}
		return true;
	}
}
