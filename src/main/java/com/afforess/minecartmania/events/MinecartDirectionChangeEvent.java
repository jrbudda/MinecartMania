package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class MinecartDirectionChangeEvent extends MinecartManiaEvent{
	private MMMinecart minecart;
	private CompassDirection previous;
	private CompassDirection current;
	public MinecartDirectionChangeEvent(MMMinecart minecart, CompassDirection previous, CompassDirection current) {
		super("MinecartDirectionChangeEvent");
		this.minecart = minecart;
		this.previous = previous;
		this.current = current;
	}
	
	public MMMinecart getMinecart() {
		return minecart;
	}
	
	public CompassDirection getPreviousDirection() {
		return previous;
	}
	
	public CompassDirection getCurrentDirection() {
		return current;
	}
}
