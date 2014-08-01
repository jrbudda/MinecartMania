package com.afforess.minecartmania.chests;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaFurnace;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class FurnaceSmeltContainer extends GenericItemContainer implements ItemContainer {
    private static final int SLOT = 0;
    MinecartManiaFurnace furnace;

    public FurnaceSmeltContainer(MinecartManiaFurnace furnace, String smelt, CompassDirection direction) {
        super(smelt, direction);
        this.furnace = furnace;
        if (smelt.toLowerCase().contains("smelt")) {
            String[] split = smelt.split(":");
            smelt = "";
            for (String s : split) {
                if (!s.toLowerCase().contains("smelt")) {
                    smelt += s + ":";
                }
            }
        }
        this.line = smelt;
    }


    public void doCollection(MinecartManiaInventory withdraw) {
        for (CompassDirection direction : directions) {
            AbstractItem[] list = getItemList(direction);
            for (AbstractItem item : list) {
                if (item != null) {
                    if (item.isInfinite()) {
                        item.setAmount(64);
                    }
                    short data = (short) (item.hasData() ? item.getData() : -1);
                    //does not match the item already in the slot, continue
                    if (furnace.getItem(SLOT) != null && !item.equals(Item.getItem(furnace.getItem(SLOT)))) {
                        continue;
                    }
                    int toAdd = Math.min(item.getAmount(), withdraw.amount(item.type()));
                    item.setAmount(toAdd);
                    if (furnace.getItem(SLOT) != null) {
                        toAdd = Math.min(64 - furnace.getItem(SLOT).getAmount(), toAdd);
                        item.setAmount(furnace.getItem(SLOT).getAmount() + toAdd);
                    }
                    if (withdraw.contains(item.type()) && withdraw.canRemoveItem(item.getId(), toAdd, data)) {
                        if (furnace.canAddItem(item.toItemStack())) {
                            withdraw.removeItem(item.getId(), toAdd, data);
                            furnace.setItem(SLOT, item.toItemStack());
                            return;
                        }
                    }

                }
            }
        }
    }
}
