package com.afforess.minecartmania.farming;

import org.bukkit.Location;
import org.bukkit.Material;

import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMStorageCart;

public class SugarFarming
{
	public static void doAutoFarm(MMStorageCart minecart) {
		if(!isAutoPlantActive(minecart) || !isAutoSugarActive(minecart)) {
			return;
		}

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

					int id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
					int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z); 
					int belowId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y-1, z);
					
					//Harvest Sugar
					if (isAutoSugarActive(minecart)) {
					
						// Check for sugar blocks and ensure they're the top one in the stack. 
						// Breaking sugar below the top will result in cane on the track which can stop the cart
						// until autocollection is turned back on.

						if (id == Material.SUGAR_CANE_BLOCK.getId() && aboveId != Material.SUGAR_CANE_BLOCK.getId()) {
							if (belowId == Material.GRASS.getId() ||  belowId == Material.DIRT.getId()) {
								if(!isAutoPlantActive(minecart)) {
									minecart.addItem(Material.SUGAR_CANE.getId());
									MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
								}
							} else {
								minecart.addItem(Material.SUGAR_CANE.getId());
								MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
							}
						}
					}

					//update data
					id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
					aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
					belowId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y-1, z);

					//Replant cane
					if (isAutoPlantActive(minecart)) {
						if (id == Material.GRASS.getId() ||  id == Material.DIRT.getId()) {
							if (aboveId == Material.AIR.getId()) {

								// Need to check for water or the cane will not plant.
								int water1 = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x+1, y, z);
								int water2 = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x-1, y, z);
								int water3 = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z+1);
								int water4 = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z-1);

								boolean foundwater = false;

								if(water1 == Material.WATER.getId() || water1 == Material.STATIONARY_WATER.getId()) { foundwater = true; }
								if(water2 == Material.WATER.getId() || water2 == Material.STATIONARY_WATER.getId()) { foundwater = true; }
								if(water3 == Material.WATER.getId() || water3 == Material.STATIONARY_WATER.getId()) { foundwater = true; }
								if(water4 == Material.WATER.getId() || water4 == Material.STATIONARY_WATER.getId()) { foundwater = true; }

								if(foundwater == true) {

									if (minecart.removeItem(Material.SUGAR_CANE.getId())) {
										MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.SUGAR_CANE_BLOCK.getId(), x, y+1, z);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean isAutoSugarActive(MMStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Sugar) || minecart.getDataValue("AutoSugar") != null;
	}
	
	private static boolean isAutoPlantActive(MMStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Sugar) || minecart.getDataValue("AutoPlant") != null;
	}
}
