package com.afforess.minecartmania.signs.actions;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;

public class ElevatorAction implements SignAction{
	
	protected Location sign;
	public ElevatorAction(Sign sign) {
		this.sign = sign.getLocation();
	}
	
	protected Sign getSign() {
		return SignManager.getSignAt(this.sign); 
	}
	
	protected Location calculateElevatorStop(MinecartManiaMinecart minecart) {
		//get the offset of the track just after the sign in the current facing direction
		int facingX = 0;
		int facingZ = 0;
		if (minecart.getDirection() == CompassDirection.NORTH) {
			facingX = -1;
		}
		else if (minecart.getDirection() == CompassDirection.EAST) {
			facingZ = -1;
		}
		else if (minecart.getDirection() == CompassDirection.SOUTH) {
			facingX = 1;
		}
		else if (minecart.getDirection() == CompassDirection.WEST) {
			facingZ = 1;
		}

		Location search = this.sign.clone();
		Location nextFloor = null;
		for (int i = 0; i < 128; i++) {
			if (i != this.sign.getY()) {
				search.setY(i);
				Sign temp = SignManager.getSignAt(search);
				if (temp != null) {
					if (temp.hasSignAction(ElevatorAction.class)) {
						nextFloor = search.clone();
						nextFloor.setX(nextFloor.getX() + facingX);
						nextFloor.setZ(nextFloor.getZ() + facingZ);
						//give priority to the minecart current facing direction
						if (MinecartUtils.isTrack(nextFloor)) {
							return nextFloor;
						}
						nextFloor.setX(nextFloor.getX() - facingX - 1);
						nextFloor.setZ(nextFloor.getZ() - facingZ);
						double speed = minecart.getMotion().length();
						if (MinecartUtils.isTrack(nextFloor)) {
							minecart.setMotion(CompassDirection.NORTH, speed);
							return nextFloor;
						}
						nextFloor.setX(nextFloor.getX() + 1);
						nextFloor.setZ(nextFloor.getZ() - 1);
						if (MinecartUtils.isTrack(nextFloor)) {
							minecart.setMotion(CompassDirection.EAST, speed);
							return nextFloor;
						}
						nextFloor.setX(nextFloor.getX() + 1);
						nextFloor.setZ(nextFloor.getZ() + 1);
						if (MinecartUtils.isTrack(nextFloor)) {
							minecart.setMotion(CompassDirection.SOUTH, speed);
							return nextFloor;
						}
						nextFloor.setX(nextFloor.getX() - 1);
						nextFloor.setZ(nextFloor.getZ() + 1);
						if (MinecartUtils.isTrack(nextFloor)) {
							minecart.setMotion(CompassDirection.WEST, speed);
							return nextFloor;
						}
					}
				}
			}
		}
		return null;
	}

	
	public boolean execute(MinecartManiaMinecart minecart) {
		Block ahead = minecart.getBlockTypeAhead();
		if (ahead != null && ahead.getState() instanceof org.bukkit.block.Sign) {
			Location teleport = calculateElevatorStop(minecart);
			if (teleport != null) {
				minecart.teleport(teleport);
				return true;
			}
		}
		return false;
	}

	
	public boolean async() {
		return false;
	}

	
	public boolean valid(Sign sign) {
		for (String line : sign.getLines()) {
			if (line.toLowerCase().contains("elevator") ||
				line.toLowerCase().contains("lift up") ||
				line.toLowerCase().contains("lift down")) {
				return true;
			}
		}
		return false;
	}

	
	public String getName() {
		return "elevatorsign";
	}

	
	public String getFriendlyName() {
		return "Elevator Sign";
	}

}
