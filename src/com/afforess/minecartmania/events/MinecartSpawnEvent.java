package com.afforess.minecartmania.events;

import org.bukkit.event.Cancellable;

import com.afforess.minecartmania.MMMinecart;

public class MinecartSpawnEvent extends MinecartManiaEvent implements Cancellable{

	private boolean cancelled = false;
	private MMMinecart minecart;
	protected MinecartSpawnEvent(MMMinecart minecart) {
		super("MinecartSpawnEvent");
		this.minecart = minecart;
	}
	
	public MMMinecart getMinecart() {
		return this.minecart;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
