package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.chests.CollectionUtils;
import com.afforess.minecartmania.chests.ItemContainer;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.signs.SignAction;

public class CollectItemsAction extends SignAction {

	@Override
	public boolean execute(MMMinecart incminecart) {
		if(!(incminecart instanceof MinecartManiaStorageCart)) return false;

		MinecartManiaStorageCart	 minecart = (MinecartManiaStorageCart) (incminecart);
		ArrayList<ItemContainer> derp = CollectionUtils.getItemContainers(this.loc.getBlock().getLocation(), minecart.getDirection(), true);

		for(ItemContainer cont : derp){
			cont.addDirection(minecart.getDirection());
			cont.doCollection(minecart);
		}

		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		return lines[0].toLowerCase().contains("[collect item");
	}

	@Override
	public String getPermissionName() {
		return "collectitemssign";
	}

	@Override
	public String getFriendlyName() {
		return "Collect Items";
	}

}