package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class ClearStalledCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearStalledCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return !minecart.isMoving();
	}

}