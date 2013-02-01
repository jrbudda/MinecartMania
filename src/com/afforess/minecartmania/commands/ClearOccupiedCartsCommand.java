package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MMMinecart;

public class ClearOccupiedCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearOccupiedCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isStandardMinecart() && minecart.getPassenger() != null;
	}

}