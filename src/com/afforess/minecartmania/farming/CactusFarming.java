package com.afforess.minecartmania.farming;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.utils.StorageMinecartUtils;

public class CactusFarming
{
	public static void doAutoFarm(MinecartManiaStorageCart minecart) {
		if(!isAutoCactusActive(minecart) && !isAutoReCactusActive(minecart)) {
			return;
		}
		if (minecart.getRange() < 1) {
			return;
		}
		
		Location loc = minecart.getLocation().clone();
		World w = loc.getWorld();
		int range = minecart.getRange();
		int rangeY = minecart.getRangeY();
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(rangeY); dy <= rangeY; dy++){
				for (int dz = -(range); dz <= range; dz++){
					//Setup data
					int x = loc.getBlockX() + dx;
					int y = loc.getBlockY() + dy;
					int z = loc.getBlockZ() + dz;

					int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
					int aboveId = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z); 
					int belowId = MinecartManiaWorld.getBlockIdAt(w, x, y-1, z);

					//Harvest Sugar
					if (isAutoCactusActive(minecart)) {

						// Like sugar, we need to break this from the top first. 

						if (id == Material.CACTUS.getId() && aboveId != Material.CACTUS.getId()) {
							if (belowId == Material.SAND.getId()) {
								if(!isAutoReCactusActive(minecart)) {
									// Only harvest the bottom if we're not replanting. 
									minecart.addItem(Material.CACTUS.getId());
									MinecartManiaWorld.setBlockAt(w, Material.AIR.getId(), x, y, z);
								}
							} else {
								minecart.addItem(Material.CACTUS.getId());
								MinecartManiaWorld.setBlockAt(w, Material.AIR.getId(), x, y, z);
							}
						}
					}

					//update data
					id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
					aboveId = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z);

					//Replant Cactus
					if (isAutoCactusActive(minecart) || isAutoReCactusActive(minecart)) {
						if (id == Material.SAND.getId()) {
							if (aboveId == Material.AIR.getId()) {

								// Need to check for blocks to the sides of the cactus position 
								// as this would normally block planting.

								int sidemx = MinecartManiaWorld.getBlockIdAt(w, x-1, y+1, z);
								int sidepx = MinecartManiaWorld.getBlockIdAt(w, x+1, y+1, z);
								int sidemz = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z-1);
								int sidepz = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z+1);

								boolean blockcactus = false;

								if(sidemx != Material.AIR.getId()) { blockcactus = true; }
								if(sidepx != Material.AIR.getId()) { blockcactus = true; }
								if(sidemz != Material.AIR.getId()) { blockcactus = true; }
								if(sidepz != Material.AIR.getId()) { blockcactus = true; }


								if (blockcactus == false && minecart.removeItem(Material.CACTUS.getId())) {
									MinecartManiaWorld.setBlockAt(w, Material.CACTUS.getId(), x, y+1, z);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean isAutoCactusActive(MinecartManiaStorageCart minecart)
	{
		return StorageMinecartUtils.isFarmingActive(minecart, FarmType.Cactus) || minecart.getDataValue("AutoCactus") != null;
	}
	
	private static boolean isAutoReCactusActive(MinecartManiaStorageCart minecart)
	{
		return StorageMinecartUtils.isFarmingActive(minecart, FarmType.Cactus) || minecart.getDataValue("AutoReCactus") != null;
	}
}
