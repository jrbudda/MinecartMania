package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;

public class ClearEmptyStorageCartsCommand extends ClearAllCartsCommand{
	
	@Override
	public CommandType getCommand() {
		return CommandType.ClearEmptyStorageCarts;
	}
	
	@Override
	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart) {
		return minecart.isStorageMinecart() && ((MinecartManiaStorageCart)minecart).isEmpty();
	}

}