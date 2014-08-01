package com.afforess.minecartmania.farming;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.minecarts.MMStorageCart;

public class PumpkinFarming extends FarmingBase {    
	public static void doAutoFarm(MMStorageCart minecart)
	{
		if(isPumpkinFarmingActive(minecart))
		{
			ArrayList<Block> roots = findRoots(minecart, Item.PUMPKIN.getId());
			
			for (Block root : roots)
			{
			    Block melonBlock = root.getRelative(BlockFace.UP);
	            if(melonBlock.getTypeId() == Item.PUMPKIN.getId())
	            {	                 
	                //add pumpkin to inventory
	                minecart.addItem(Item.PUMPKIN.getId(), 1);
	                
	                //remove melon block
                    melonBlock.setTypeIdAndData(Item.AIR.getId(), (byte)Item.AIR.getData(), true);
	                
                    //set root to soil/farmland
                    root.setTypeIdAndData(Item.SOIL.getId(), (byte)Item.SOIL.getData(), true);
	            }
			}
		}
	}


	private static boolean isPumpkinFarmingActive(MMStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Pumpkin);
	}
}
