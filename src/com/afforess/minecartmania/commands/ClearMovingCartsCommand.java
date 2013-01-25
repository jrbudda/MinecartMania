package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class ClearMovingCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearMovingCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return minecart.isMoving();
	}

}