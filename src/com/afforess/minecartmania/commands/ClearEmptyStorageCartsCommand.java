package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;

public class ClearEmptyStorageCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearEmptyStorageCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MMMinecart minecart) {
		return minecart.isStorageMinecart() && ((MinecartManiaStorageCart)minecart).isEmpty();
	}

}