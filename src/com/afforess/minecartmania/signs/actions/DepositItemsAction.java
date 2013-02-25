package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.chests.CollectionUtils;
import com.afforess.minecartmania.chests.ItemContainer;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.signs.SignAction;

public class DepositItemsAction extends SignAction {

	@Override
	public boolean execute(MMMinecart incminecart) {
		if(!(incminecart instanceof MinecartManiaStorageCart)) return false;

		MinecartManiaStorageCart	 minecart = (MinecartManiaStorageCart) (incminecart);
		ArrayList<ItemContainer> derp = CollectionUtils.getItemContainers(this.loc.getBlock().getLocation(), minecart.getDirection(), false);

		for(ItemContainer cont : derp){
			cont.addDirection(minecart.getDirection());
			cont.doCollection(minecart);
		}

		return true;
	}

	@Override
	public boolean async() {
		return true;
	}

	@Override
	public boolean process(String[] lines) {
		return lines[0].toLowerCase().contains("[deposit item");
	}

	@Override
	public String getPermissionName() {
		return "deposititemssign";
	}

	@Override
	public String getFriendlyName() {
		return "Deposit Items";
	}

}