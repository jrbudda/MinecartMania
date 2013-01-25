package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class ClearStalledCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearStalledCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return !minecart.isMoving();
	}

}