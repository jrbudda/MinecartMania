package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartClickedEvent extends MinecartManiaEvent implements MinecartEvent{
	boolean action = false;
	MMMinecart minecart;
	
	public MinecartClickedEvent(MMMinecart minecart) {
		super("MinecartClickedEvent");
		this.minecart = minecart;
	}

	public boolean isActionTaken() {
		return action;
	}

	public void setActionTaken(boolean Action) {
		this.action = Action;
	}

	public MMMinecart getMinecart() {
		return minecart;
	}

}
