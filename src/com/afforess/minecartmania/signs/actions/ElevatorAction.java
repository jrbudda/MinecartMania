package com.afforess.minecartmania.signs.actions;

import java.util.Set;

import org.bukkit.Location;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.MMSign;
import com.afforess.minecartmania.config.NewControlBlock;
import com.afforess.minecartmania.config.NewControlBlockList;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.MinecartUtils;

public class ElevatorAction extends SignAction{

	private int min = 0;
	private int max = 255;

	protected Location calculateElevatorStop(MMMinecart minecart) {
		//get the offset of the track just after the sign in the current facing direction

		Location search = loc.clone();

		for (int i = min; i <= max; i++) {

			if (i == loc.getBlockY()) continue;

			search.setY(i);
			MMSign temp = SignManager.getOrCreateMMSign(search);	
			NewControlBlock ncb = NewControlBlockList.getControlBlock(Item.getItem(search.getBlock()));

			if (temp == null && ncb == null) continue;

			if ( (temp !=null && temp.hasSignAction(ElevatorAction.class)) || (ncb!=null &&  ncb.hasSignAction(ElevatorAction.class)) )  {

				if(ncb !=null) search = search.add(0, 1, 0); //control block.

				Set<CompassDirection> dirs = MinecartUtils.getValidDirections(search.getBlock());
		
				Logger.debug("elevator: looking for exit at y= " + i + "valid dirs " + dirs.size());		

				if(dirs.contains(minecart.getDirection())) return search;

				else{
					if (!dirs.isEmpty()){
						minecart.setMotion(dirs.iterator().next(), minecart.getMotion().length());
						return search;
					}
				}
			}
		}
		return null;
	}





	public boolean execute(MMMinecart minecart) {
		Location teleport = calculateElevatorStop(minecart);
		if (teleport != null) {
			minecart.teleport(teleport);
			return true;
		}
		Logger.debug("could not find exit for elevator");
		return false;
	}


	public boolean async() {
		return false;
	}

	public boolean process(String[] lines) {
		for (String line : lines) {
			String s = line.toLowerCase();
			if (s.toLowerCase().contains("[elevator")) {
				return true;
			}
			else if( s.toLowerCase().contains("[lift down")){
				min = 0;
				max = loc.getBlockX()-1;
				return true;
			}
			else if( s.toLowerCase().contains("[lift up")) {
				min = loc.getBlockX() +1;
				max = 255;
				return true;
			}
			return false;
		}
		return false;
	}

	public String getPermissionName() {
		return "elevatorsign";
	}


	public String getFriendlyName() {
		return "Elevator";
	}

}
