package com.afforess.minecartmania.farming;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;

public class CocoaFarming extends FarmingBase {

	private static Random rand = new Random();

	public static void doAutoFarm(MinecartManiaStorageCart minecart)
	{
		if(isFarmingActive(minecart))
		{
			if (minecart.getFarmingRange() < 1) {
				return;
			}
			Location loc = minecart.getLocation().clone();
			int range = minecart.getFarmingRange();
			int rangeY = minecart.getFarmingRangeY();
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(rangeY); dy <= rangeY; dy++){
					for (int dz = -(range); dz <= range; dz++){
						//Setup data
						int x = loc.getBlockX() + dx;
						int y = loc.getBlockY() + dy;
						int z = loc.getBlockZ() + dz;
						Block b =MinecartManiaWorld.getBlockAt(minecart.getWorld(), x, y, z);
						int id = b.getTypeId();
						boolean dirty = false; //set when the data gets changed
						//Harvest fully grown crops first

						int data = MinecartManiaWorld.getBlockData(minecart.getWorld(), x, y, z);

						if (id == Material.COCOA.getId()) {
							//fully grown

							if ((data & 12) == 8){
								int c = rand.nextInt(2) + 2; //2-3

								org.bukkit.inventory.ItemStack is = com.afforess.minecartmania.entity.Item.COCOA_BEANS.toItemStack();
								is.setAmount(c);
								minecart.addItem(is);

								b.setTypeId(0);
							}

						}
						//Plant
						else if (id == com.afforess.minecartmania.entity.Item.JUNGLE_LOG.getId()) {
							if (b.getRelative(org.bukkit.block.BlockFace.NORTH).getType() == Material.AIR){			
								if (minecart.removeItem(351,1,(short) 3)) 
									b.getRelative(org.bukkit.block.BlockFace.NORTH).setTypeIdAndData(com.afforess.minecartmania.entity.Item.COCOA_PLANT.getId(), (byte) 0, true);			
							}
							if (b.getRelative(org.bukkit.block.BlockFace.WEST).getType() == Material.AIR){			
								if (minecart.removeItem(351,1,(short) 3)) 
									b.getRelative(org.bukkit.block.BlockFace.WEST).setTypeIdAndData(com.afforess.minecartmania.entity.Item.COCOA_PLANT.getId(), (byte) 3, true);			
							}
							if (b.getRelative(org.bukkit.block.BlockFace.EAST).getType() == Material.AIR){		
								if (minecart.removeItem(351,1,(short) 3)) 
									b.getRelative(org.bukkit.block.BlockFace.EAST).setTypeIdAndData(com.afforess.minecartmania.entity.Item.COCOA_PLANT.getId(), (byte) 1, true);			
							}
							if (b.getRelative(org.bukkit.block.BlockFace.SOUTH).getType() == Material.AIR){			
								if (minecart.removeItem(351,1,(short) 3)) 
									b.getRelative(org.bukkit.block.BlockFace.SOUTH).setTypeIdAndData(com.afforess.minecartmania.entity.Item.COCOA_PLANT.getId(), (byte) 2, true);			
							}				
						}
					}
				}
			}
		}
	}




	private static boolean isFarmingActive(MinecartManiaStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Cocoa);
	}
}
