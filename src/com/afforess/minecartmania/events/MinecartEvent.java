package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;

public interface MinecartEvent{
	
	public boolean isActionTaken();
	
	public void setActionTaken(boolean Action);
	
	public MinecartManiaMinecart getMinecart();

}
