package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.AbstractItem;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.utils.ItemUtils;

public class MinimumItemAction extends SignAction{
	protected AbstractItem items[] = null;
	
	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.isStorageMinecart()) {
			for (AbstractItem item : items) {
				((MinecartManiaStorageCart)minecart).setMinimumItem(item.type(), item.getAmount());
			}
			return true;
		}
		return false;
	}

	
	public boolean async() {
		return true;
	}

	
	public boolean process(String[] lines) {
		if (lines[0].toLowerCase().contains("[min item")) {
			this.items = ItemUtils.getItemStringListToMaterial(lines);
			return true;
		}
		return false;
	}

	
	public String getPermissionName() {
		return "minimumitemsign";
	}

	
	public String getFriendlyName() {
		return "Minimum Item Sign";
	}

}
