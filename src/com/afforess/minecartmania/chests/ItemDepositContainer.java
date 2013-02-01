package com.afforess.minecartmania.chests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class ItemDepositContainer extends GenericItemContainer implements ItemContainer{
	private MinecartManiaInventory inventory;
	
	public ItemDepositContainer(MinecartManiaInventory inventory, String line, CompassDirection direction) {
		super(line, direction);
		this.inventory = inventory;
	}

	public void doCollection(MinecartManiaInventory deposit) {
		Logger.debug("Processing Deposit Sign. Text: "  + this.line);
		List<AbstractItem> rawList =new ArrayList<AbstractItem>();
		java.util.Collections.addAll(rawList, getRawItemList());
		for (CompassDirection direction : directions) {
			AbstractItem[] list = getItemList(direction);
			for (AbstractItem item : list) {
				if (item != null && rawList.contains(item)) {
					int amount = item.getAmount();
					while (inventory.contains(item.type()) && (item.isInfinite() || amount > 0) ) {
						ItemStack itemStack = inventory.getItem(inventory.first(item.type()));
						int toAdd = item.isInfinite() ? itemStack.getAmount() : (itemStack.getAmount() > amount ? amount : itemStack.getAmount());
						if (!inventory.canRemoveItem(itemStack.getTypeId(), toAdd, itemStack.getDurability())) {
							break; //if we are not allowed to remove the items, give up
						}
						else if (!deposit.addItem(new ItemStack(itemStack.getTypeId(), toAdd, itemStack.getDurability()))) {
							break;
						}
						inventory.removeItem(itemStack.getTypeId(), toAdd, itemStack.getDurability());
						amount -= toAdd;
					}
					rawList.remove(item);
				}
			}
		}
	}
}
