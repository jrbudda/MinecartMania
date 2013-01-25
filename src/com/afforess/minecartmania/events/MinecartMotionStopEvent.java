package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartMotionStopEvent extends MinecartManiaEvent{
	private MinecartManiaMinecart minecart;
	
	public MinecartMotionStopEvent(MinecartManiaMinecart cart) {
		super("MinecartMotionStopEvent");
		minecart = cart;
	}

	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
}
