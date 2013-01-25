package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartManiaMinecartCreatedEvent extends MinecartManiaEvent{
	private MinecartManiaMinecart minecart;
	
	public MinecartManiaMinecartCreatedEvent(MinecartManiaMinecart cart) {
		super("MinecartManiaMinecartCreatedEvent");
		minecart = cart;
	}

	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
}
