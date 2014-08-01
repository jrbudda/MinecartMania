package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;

import com.afforess.minecartmania.chests.CollectionUtils;
import com.afforess.minecartmania.chests.ItemContainer;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import com.afforess.minecartmania.signs.SignAction;

public class TrashItemsAction extends SignAction {

	@Override
	public boolean execute(MMMinecart incminecart) {
		if(!(incminecart instanceof MMStorageCart)) return false;

		MMStorageCart	 minecart = (MMStorageCart) (incminecart);
		ArrayList<ItemContainer> derp = CollectionUtils.getTrashItemContainers(this.loc.getBlock().getLocation(), minecart.getDirection());

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
		return lines[0].toLowerCase().contains("[trash item");
	}

	@Override
	public String getPermissionName() {
		return "trashitemssign";
	}

	@Override
	public String getFriendlyName() {
		return "Trash Items";
	}

}