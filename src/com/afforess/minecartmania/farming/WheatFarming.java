package com.afforess.minecartmania.farming;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;

import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;

public class WheatFarming
{
	public static void doAutoFarm(MinecartManiaStorageCart minecart) {
		if (!isAutoHarvestActive(minecart) && !isAutoHarvestActive(minecart) && !isAutoSeedActive(minecart)) {
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
					boolean dirty = false; //set when the data gets changed
					//Harvest fully grown crops first
					if (isAutoHarvestActive(minecart)) {
						int data = MinecartManiaWorld.getBlockData(minecart.getWorld(), x, y, z);
						if (id == Material.CROPS.getId()) {
							//fully grown
							if (data == 0x7) {
								minecart.addItem(Material.WHEAT.getId());
								minecart.addItem(Material.SEEDS.getId());
								if ((new Random()).nextBoolean()) { //Randomly add second seed.
									minecart.addItem(Material.SEEDS.getId());
								}
								MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
								dirty = true;
							}
						}
					}
					//update data
					if (dirty) {
						id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
						aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
						dirty = false;
					}
					//till soil
					if (isAutoTillActive(minecart)) {
						if (id == Material.GRASS.getId() ||  id == Material.DIRT.getId()) {
							if (aboveId == Material.AIR.getId()) {
								MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.SOIL.getId(), x, y, z);
								dirty = true;
							}
						}
					}

					//update data
					if (dirty) {
						id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
						aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
						dirty = false;
					}
					//Seed tilled land 
					if (isAutoSeedActive(minecart)) {
						if (id == Material.SOIL.getId()) {
							if (aboveId == Material.AIR.getId()) {
								if (minecart.removeItem(Material.SEEDS.getId())) {
									MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.CROPS.getId(), x, y+1, z);
									dirty = true;
								}
							}
						}
					}

				}
			}
		}
	}
	
	private static boolean isAutoTillActive(MinecartManiaStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Wheat) || minecart.getDataValue("AutoTill") != null;
	}
	
	private static boolean isAutoSeedActive(MinecartManiaStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Wheat) || minecart.getDataValue("AutoSeed") != null;
	}
	
	private static boolean isAutoHarvestActive(MinecartManiaStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Wheat) || minecart.getDataValue("AutoHarvest") != null;
	}
}
