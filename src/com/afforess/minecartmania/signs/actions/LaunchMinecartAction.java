package com.afforess.minecartmania.signs.actions;

import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;

public class LaunchMinecartAction extends SignAction {
	private volatile Vector launchSpeed = null;
	private volatile boolean previous = false;


	public boolean execute(MinecartManiaMinecart minecart) {
		if(minecart == null){
			minecart = MinecartUtils.getNearestMinecartInRange(loc, 2);
		}

		if(minecart == null) return false;

		if (minecart.isMoving()) {
			return false;
		}

		final MinecartManiaMinecart mc = minecart;

		if (previous) {
			if (minecart.isFrozen()){
				minecart.setFrozen(false);
			}
			else if (minecart.getPreviousDirectionOfMotion() != null && minecart.getPreviousDirectionOfMotion() != CompassDirection.NO_DIRECTION) {
				mc.setMotion(mc.getPreviousDirectionOfMotion(), 0.6D);
				return true;
			}
		}
		else if (launchSpeed != null) {
			mc.setMotion(launchSpeed);		
		}
		else		{
			Vector spd = null;
			if(	MinecartUtils.validMinecartTrack(minecart.getLocation(),1,CompassDirection.NORTH)){
				com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" launch north");
				spd = new Vector(0, 0, -0.6D);
			}
			else if(	MinecartUtils.validMinecartTrack(minecart.getLocation(),1,CompassDirection.SOUTH)){
				spd = new Vector(0, 0, 0.6D);
			}
			else if(	MinecartUtils.validMinecartTrack(minecart.getLocation(),1,CompassDirection.EAST)){
				spd = new Vector(0.6D, 0, 0);
			}
			else if(	MinecartUtils.validMinecartTrack(minecart.getLocation(),1,CompassDirection.WEST)){
				spd = new Vector(-0.6D, 0, 0);
			}

			if (spd !=null){
				mc.setMotion(spd);		
			}
		}

		return false;

	}


	public boolean async() {
		return true;
	}

	public boolean process(String[] lines) {
		this.executeAcceptsNull = true;
		if (launchSpeed == null ) {
			previous = false;
			launchSpeed = null;
			for (String line : lines){
				if (line.toLowerCase().contains("[previous dir")) {
					previous = true;
					break;
				}
				else if (line.toLowerCase().contains("[launch north")) {
					launchSpeed = new Vector(0, 0, -0.6D);
					break;
				}
				else if (line.toLowerCase().contains("[launch east")) {
					launchSpeed = new Vector(0.6D, 0, 0);
					break;
				}
				else if (line.toLowerCase().contains("[launch south")) {
					launchSpeed = new Vector(0, 0, 0.6D);
					break;
				}
				else if (line.toLowerCase().contains("[launch west")) {
					launchSpeed = new Vector(-0.6D, 0, 0);
					break;
				}
				else if (line.toLowerCase().contains("[launch")) {
					return true;
				}

			}

		}
		return launchSpeed != null || previous;
	}


	public String getPermissionName() {
		return "launchersign";
	}


	public String getFriendlyName() {
		return "Launcher Sign";
	}

}
