package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class ClearStandardCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearStandardCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isStandardMinecart();
	}

}
