package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartMotionStartEvent extends MinecartManiaEvent{
	private MinecartManiaMinecart minecart;
	
	public MinecartMotionStartEvent(MinecartManiaMinecart cart) {
		super("MinecartMotionStartEvent");
		minecart = cart;
	}

	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
}
