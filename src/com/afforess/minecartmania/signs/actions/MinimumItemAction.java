package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.AbstractItem;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.utils.ItemUtils;

public class MinimumItemAction implements SignAction{
	protected AbstractItem items[] = null;
	public MinimumItemAction(Sign sign) {
		this.items = ItemUtils.getItemStringListToMaterial(sign.getLines());
	}

	
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

	
	public boolean valid(Sign sign) {
		if (sign.getLine(0).toLowerCase().contains("min item")) {
			sign.addBrackets();
			return true;
		}
		return false;
	}

	
	public String getName() {
		return "minimumitemsign";
	}

	
	public String getFriendlyName() {
		return "Minimum Item Sign";
	}

}
