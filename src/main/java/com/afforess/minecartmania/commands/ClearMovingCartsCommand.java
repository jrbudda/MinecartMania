package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class ClearMovingCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearMovingCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isMoving();
	}

}