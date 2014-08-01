package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;

import com.afforess.minecartmania.chests.CollectionUtils;
import com.afforess.minecartmania.chests.ItemContainer;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import com.afforess.minecartmania.signs.SignAction;

public class SmeltItemsAction extends SignAction {

	@Override
	public boolean execute(MMMinecart incminecart) {
		if(!(incminecart instanceof MMStorageCart)) return false;

		MMStorageCart	 minecart = (MMStorageCart) (incminecart);
		ArrayList<ItemContainer> derp = CollectionUtils.getFurnaceContainers(this.loc.getBlock().getLocation(), minecart.getDirection());
		
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
		for(String line : lines){
			if(line.toLowerCase().contains("[smelt:") || line.contains("[fuel:")){
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "smeltitemssign";
	}

	@Override
	public String getFriendlyName() {
		return "Smelt Items";
	}

}