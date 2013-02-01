package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MMMinecart;

public class MinecartManiaMinecartDestroyedEvent extends MinecartManiaEvent{
	private MMMinecart minecart;
	
	public MinecartManiaMinecartDestroyedEvent(MMMinecart cart) {
		super("MinecartManiaMinecartDestroyedEvent");
		minecart = cart;
	}

	public MMMinecart getMinecart() {
		return minecart;
	}
}
