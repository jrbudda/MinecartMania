package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MMMinecart;

public interface MinecartEvent{
	
	public boolean isActionTaken();
	
	public void setActionTaken(boolean Action);
	
	public MMMinecart getMinecart();

}
