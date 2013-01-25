package com.afforess.minecartmaniachestcontrol.itemcontainer;

import com.afforess.minecartmaniacore.entity.AbstractItem;
import com.afforess.minecartmaniacore.entity.MinecartManiaInventory;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public interface ItemContainer {
	
	public boolean hasDirectionCondition();
	
	public boolean hasAmountCondition();
	
	public AbstractItem[] getRawItemList();
	
	public AbstractItem[] getItemList(CompassDirection direction);
	
	public void addDirection(CompassDirection direction);
	
	public void doCollection(MinecartManiaInventory other);

}
