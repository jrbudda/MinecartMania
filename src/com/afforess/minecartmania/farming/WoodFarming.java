package com.afforess.minecartmania.farming;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;

public class WoodFarming extends FarmingBase {
	public static void doAutoFarm(MinecartManiaStorageCart minecart) {
		if (!isAutoTimberActive(minecart)) {
			return;
		}
		if (minecart.getFarmingRange() < 1) {
			return;
		}

		//get roots
		ArrayList<Block> roots = findRoots(minecart, Item.LOG.getId());
		
		//remove root log blocks and replace with saplings
		for (Block root : roots)
		{
			Block logBlock = root.getRelative(BlockFace.UP);
			if(logBlock.getTypeId() == Item.LOG.getId())
			{

				//check log block and add to inventory
				minecart.addItem(Item.getItem(logBlock.getTypeId(), logBlock.getData()).toItemStack());
				
				//plant sapling if AutoForest, alse replace with air
				if(isAutoForestActive(minecart))
				{
					Item sapling = Item.SAPLING;
					if(logBlock.getData() == 0x1) sapling = Item.SPRUCE_SAPLING;
					else if(logBlock.getData() == 0x2) sapling = Item.BIRCH_SAPLING;
					else if(logBlock.getData() == 0x3) sapling = Item.JUNGLE_SAPLING;
					if(minecart.contains(sapling))
					{
						minecart.removeItem(sapling.getId(), sapling.getData());
						logBlock.setTypeIdAndData(sapling.getId(), (byte)sapling.getData(), true);
					}
					else
					{
						logBlock.setTypeIdAndData(Item.AIR.getId(), (byte)Item.AIR.getData(), true);
					}
				}
				else
				{
					logBlock.setTypeIdAndData(Item.AIR.getId(), (byte)Item.AIR.getData(), true);
				}
			}
		}
		
		//remove all logs
		for (Block root : roots)
		{
			Block tree = root.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
			removeLogs(tree, minecart);
		}
	}
	
	private static void removeLogs(Block tree, MinecartManiaStorageCart minecart)
	{
		if(tree != null && tree.getTypeId() == Item.LOG.getId() && minecart != null)
		{
		    Item item = Item.getItem(tree.getTypeId(), tree.getData());
		    if(item != null)
		    {
    			minecart.addItem(item.toItemStack());
    			tree.setTypeIdAndData(Item.AIR.getId(), (byte)Item.AIR.getData(), true);
    
    			removeLogs(tree.getRelative(BlockFace.UP), minecart);
    			removeLogs(tree.getRelative(BlockFace.NORTH), minecart);
    			removeLogs(tree.getRelative(BlockFace.SOUTH), minecart);
    			removeLogs(tree.getRelative(BlockFace.WEST), minecart);
    			removeLogs(tree.getRelative(BlockFace.EAST), minecart);
    			removeLogs(tree.getRelative(BlockFace.NORTH_EAST), minecart);
    			removeLogs(tree.getRelative(BlockFace.NORTH_WEST), minecart);
    			removeLogs(tree.getRelative(BlockFace.SOUTH_EAST), minecart);
    			removeLogs(tree.getRelative(BlockFace.SOUTH_WEST), minecart);
		    }
		}		
	}
	
	private static boolean isAutoTimberActive(MinecartManiaStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Wood) || minecart.getDataValue("AutoTimber") != null;
	}
	
	private static boolean isAutoForestActive(MinecartManiaStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Wood) || minecart.getDataValue("AutoForest") != null;
	}
}
