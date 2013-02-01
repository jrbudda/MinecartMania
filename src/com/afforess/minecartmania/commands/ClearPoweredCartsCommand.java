package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MMMinecart;

public class ClearPoweredCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearPoweredCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isPoweredMinecart();
	}

}