package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;

public class ClearEmptyStorageCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearEmptyStorageCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isStorageMinecart() && ((MMStorageCart)minecart).isEmpty();
	}

}