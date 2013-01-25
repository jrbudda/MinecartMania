package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartManiaMinecartDestroyedEvent extends MinecartManiaEvent{
	private MinecartManiaMinecart minecart;
	
	public MinecartManiaMinecartDestroyedEvent(MinecartManiaMinecart cart) {
		super("MinecartManiaMinecartDestroyedEvent");
		minecart = cart;
	}

	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
}
