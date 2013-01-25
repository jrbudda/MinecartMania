package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public class MinecartDirectionChangeEvent extends MinecartManiaEvent{
	private MinecartManiaMinecart minecart;
	private CompassDirection previous;
	private CompassDirection current;
	public MinecartDirectionChangeEvent(MinecartManiaMinecart minecart, CompassDirection previous, CompassDirection current) {
		super("MinecartDirectionChangeEvent");
		this.minecart = minecart;
		this.previous = previous;
		this.current = current;
	}
	
	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
	
	public CompassDirection getPreviousDirection() {
		return previous;
	}
	
	public CompassDirection getCurrentDirection() {
		return current;
	}
}
