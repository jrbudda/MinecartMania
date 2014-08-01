package com.afforess.minecartmania.chests;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TrashItemContainer extends GenericItemContainer implements ItemContainer {

    public TrashItemContainer(String line, CompassDirection direction) {
        super(line, direction);
    }


    public void doCollection(MinecartManiaInventory other) {
        List<AbstractItem> rawList = new ArrayList<AbstractItem>();
        java.util.Collections.addAll(rawList, getRawItemList());
        for (CompassDirection direction : directions) {
            AbstractItem[] list = getItemList(direction);
            for (AbstractItem item : list) {
                if (item != null && rawList.contains(item)) {
                    int amount = item.getAmount();
                    while (other.contains(item.type()) && (item.isInfinite() || amount > 0)) {
                        ItemStack itemStack = other.getItem(other.first(item.type()));
                        int toAdd = item.isInfinite() ? itemStack.getAmount() : (itemStack.getAmount() > amount ? amount : itemStack.getAmount());
                        if (!other.canRemoveItem(itemStack.getTypeId(), toAdd, itemStack.getDurability())) {
                            break; //if we are not allowed to remove the items, give up
                        }
                        other.removeItem(itemStack.getTypeId(), toAdd, itemStack.getDurability());
                        amount -= toAdd;
                    }
                    rawList.remove(item);
                }
            }
        }
    }

}
