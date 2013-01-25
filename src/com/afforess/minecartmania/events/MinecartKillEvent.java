package com.afforess.minecartmania.events;

import org.bukkit.event.Cancellable;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class MinecartKillEvent extends MinecartManiaEvent implements Cancellable{
	private boolean cancelled = false;
	private MinecartManiaMinecart minecart;
	protected MinecartKillEvent(MinecartManiaMinecart minecart) {
		super("MinecartKillEvent");
		this.minecart = minecart;
	}
	
	public MinecartManiaMinecart getMinecart() {
		return this.minecart;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
