package com.afforess.minecartmania.signs.actions;

import org.bukkit.Location;

import com.afforess.minecartmaniacore.entity.Item;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.NewControlBlock;
import com.afforess.minecartmania.config.NewControlBlockList;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;

public class ElevatorAction extends SignAction{

	protected Location calculateElevatorStop(MinecartManiaMinecart minecart) {
		//get the offset of the track just after the sign in the current facing direction
		int facingX = 0;
		int facingZ = 0;
		
		if (minecart.getDirection() == CompassDirection.NORTH) {
			facingZ = -1;
		}
		else if (minecart.getDirection() == CompassDirection.EAST) {
			facingX = -1;
		}
		else if (minecart.getDirection() == CompassDirection.SOUTH) {
			facingZ = 1;
		}
		else if (minecart.getDirection() == CompassDirection.WEST) {
			facingX = 1;
		}

		Location search = loc.clone();
		Location nextFloor = null;
		for (int i = 0; i < 256; i++) {

			if (i == loc.getBlockY()) continue;

			search.setY(i);
			MMSign temp = SignManager.getOrCreateMMSign(search);	
			NewControlBlock ncb = NewControlBlockList.getControlBlock(Item.getItem(search.getBlock()));

			if (temp == null && ncb == null) continue;

			if ( (temp !=null && temp.hasSignAction(ElevatorAction.class)) || (ncb!=null &&  ncb.hasSignAction(ElevatorAction.class)) )  {

				nextFloor = search.clone();
				
				if(ncb !=null) search = search.add(0, 1, 0); //control block.
				
				nextFloor.setX(nextFloor.getX() + facingX);
				nextFloor.setZ(nextFloor.getZ() + facingZ);
				
				//give priority to the minecart current facing direction
				if (MinecartUtils.isTrack(nextFloor)) {
					return nextFloor;
				}
				
				nextFloor.setX(nextFloor.getX() - facingX -1);
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
		return null;
	}


	public boolean execute(MinecartManiaMinecart minecart) {
		Location teleport = calculateElevatorStop(minecart);
		if (teleport != null) {
			minecart.teleport(teleport);
			return true;
		}
		com.afforess.minecartmania.MinecartMania.log("could not find exit for elevator");
		return false;
	}


	public boolean async() {
		return false;
	}


	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[elevator")) {
				return true;
			}
		}
		return false;
	}


	public String getPermissionName() {
		return "elevatorsign";
	}


	public String getFriendlyName() {
		return "Elevator Sign";
	}

}
