package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MMMinecart;

public class ClearEmptyCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearEmptyCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isStandardMinecart() && minecart.getPassenger() == null;
	}

}
