package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class ClearOccupiedCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearOccupiedCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return minecart.isStandardMinecart() && minecart.getPassenger() != null;
	}

}