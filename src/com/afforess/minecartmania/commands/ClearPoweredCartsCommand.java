package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class ClearPoweredCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearPoweredCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return minecart.isPoweredMinecart();
	}

}