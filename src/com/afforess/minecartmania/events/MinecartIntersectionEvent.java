package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartIntersectionEvent extends MinecartManiaEvent implements MinecartEvent {
	private boolean action = false;
	private MinecartManiaMinecart minecart;
	
	public MinecartIntersectionEvent(MinecartManiaMinecart cart) {
		super("MinecartIntersectionEvent");
		minecart = cart;
	}
	
	
	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
	
	public boolean isActionTaken() {
		return action;
	}
	
	public void setActionTaken(boolean b) {
		action = b;
	}

}
