package com.afforess.minecartmania.chests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class ItemCollectionContainer extends GenericItemContainer implements ItemContainer{
	private MinecartManiaInventory inventory;

	public ItemCollectionContainer(MinecartManiaInventory inventory, String line, CompassDirection direction) {
		super(line, direction);
		this.inventory = inventory;
	}
	

	public void doCollection(MinecartManiaInventory withdraw) {
		Logger.debug("Processing Collection Sign. Text: "  + this.line);
		List<AbstractItem> rawList =new ArrayList<AbstractItem>();
		java.util.Collections.addAll(rawList, getRawItemList());
		Player owner = null;
		String temp = null;
		if (inventory instanceof MinecartManiaChest) {
			temp = ((MinecartManiaChest)inventory).getOwner();
		}
		if (temp != null) {
			owner = Bukkit.getServer().getPlayer(temp);
		}
		for (CompassDirection direction : directions) {
			AbstractItem[] list = getItemList(direction);
			for (AbstractItem item : list) {
				if (item != null && rawList.contains(item)) {
					int amount = item.getAmount();
					while (withdraw.contains(item.type()) && (item.isInfinite() || amount > 0) ) {
						ItemStack itemStack = withdraw.getItem(withdraw.first(item.type()));
						int toAdd = item.isInfinite() ? itemStack.getAmount() : (itemStack.getAmount() > amount ? amount : itemStack.getAmount());
						if (!withdraw.canRemoveItem(itemStack.getTypeId(), toAdd, itemStack.getDurability())) {
							break; //if we are not allowed to remove the items, give up
						}
						else if (!inventory.addItem(new ItemStack(itemStack.getTypeId(), toAdd, itemStack.getDurability()), owner)) {
							break;
						}
						withdraw.removeItem(itemStack.getTypeId(), toAdd, itemStack.getDurability());
						amount -= toAdd;
					}
					rawList.remove(item);
				}
			}
		}
	}
}
