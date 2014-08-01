package com.afforess.minecartmania.chests;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public interface ItemContainer {

    public boolean hasDirectionCondition();

    public boolean hasAmountCondition();

    public AbstractItem[] getRawItemList();

    public AbstractItem[] getItemList(CompassDirection direction);

    public void addDirection(CompassDirection direction);

    public void doCollection(MinecartManiaInventory other);

}
