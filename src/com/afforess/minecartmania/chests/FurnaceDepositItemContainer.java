package com.afforess.minecartmania.chests;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaFurnace;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class FurnaceDepositItemContainer extends GenericItemContainer implements ItemContainer{
	private MinecartManiaFurnace furnace;
	private static final int SLOT = 2;
	public FurnaceDepositItemContainer(MinecartManiaFurnace furnace, String line, CompassDirection direction) {
		super(line, direction);
		this.furnace = furnace;
	}

	public void doCollection(MinecartManiaInventory deposit) {
		for (CompassDirection direction : directions) {
			AbstractItem[] list = getItemList(direction);
			for (AbstractItem item : list) {
				if (item != null) {
					short data = (short) (item.hasData() ? item.getData() : -1);
					//does not match the item already in the slot, continue
					if (furnace.getItem(SLOT) == null || !item.equals(Item.getItem(furnace.getItem(SLOT)))) {
						continue;
					}
					int toRemove = furnace.getItem(SLOT).getAmount();
					if (!item.isInfinite() && item.getAmount() < toRemove) {
						toRemove = item.getAmount();
					}
					item.setAmount(toRemove);
					if (furnace.canRemoveItem(item.getId(), toRemove, data)) {
						if (deposit.canAddItem(item.toItemStack())) {
							if (deposit.addItem(item.toItemStack())) {
								furnace.setItem(SLOT, null);
							}
							else {
								return;
							}
						}
					}
					
				}
			}
		}
	}

}
