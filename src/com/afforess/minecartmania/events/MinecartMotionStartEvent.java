package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MMMinecart;

public class MinecartMotionStartEvent extends MinecartManiaEvent{
	private MMMinecart minecart;
	
	public MinecartMotionStartEvent(MMMinecart cart) {
		super("MinecartMotionStartEvent");
		minecart = cart;
	}

	public MMMinecart getMinecart() {
		return minecart;
	}
}
