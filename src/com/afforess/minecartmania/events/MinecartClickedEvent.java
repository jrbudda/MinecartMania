package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartClickedEvent extends MinecartManiaEvent implements MinecartEvent{
	boolean action = false;
	MinecartManiaMinecart minecart;
	
	public MinecartClickedEvent(MinecartManiaMinecart minecart) {
		super("MinecartClickedEvent");
		this.minecart = minecart;
	}

	public boolean isActionTaken() {
		return action;
	}

	public void setActionTaken(boolean Action) {
		this.action = Action;
	}

	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}

}
