package com.afforess.minecartmaniacore.utils;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.afforess.minecartmania.farming.FarmType;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class StorageMinecartUtils {

	public static void doAutoFarm(MinecartManiaStorageCart minecart) {
		if (minecart.getDataValue("AutoHarvest") == null && minecart.getDataValue("AutoTill") == null && minecart.getDataValue("AutoSeed") == null && !isFarmingActive(minecart, FarmType.Wheat)) {
			return;
		}
		if (minecart.getRange() < 1) {
			return;
		}
		Location loc = minecart.getLocation().clone();
		int range = minecart.getRange();
		int rangeY = minecart.getRangeY();
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
					if (minecart.getDataValue("AutoHarvest") != null || isFarmingActive(minecart, FarmType.Wheat)) {
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
					if (minecart.getDataValue("AutoTill") != null || isFarmingActive(minecart, FarmType.Wheat)) {
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
					if (minecart.getDataValue("AutoSeed") != null || isFarmingActive(minecart, FarmType.Wheat)) {
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

	public static void doAutoCactusFarm(MinecartManiaStorageCart minecart) {
		if((minecart.getDataValue("AutoCactus") == null) && (minecart.getDataValue("AutoReCactus") == null) && !isFarmingActive(minecart, FarmType.Cactus)) {
			return;
		}
		if (minecart.getRange() < 1) {
			return;
		}
		Location loc = minecart.getLocation().clone();
		int range = minecart.getRange();
		int rangeY = minecart.getRangeY();
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
					if (minecart.getDataValue("AutoCactus") != null) {

						// Like sugar, we need to break this from the top first. 

						if (id == Material.CACTUS.getId() && aboveId != Material.CACTUS.getId()) {
							if (belowId == Material.SAND.getId()) {
								if(!isFarmingActive(minecart, FarmType.Cactus) &&  minecart.getDataValue("AutoReCactus") == null) {
									// Only harvest the bottom if we're not replanting. 
									minecart.addItem(Material.CACTUS.getId());
									MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
								}
							} else {
								minecart.addItem(Material.CACTUS.getId());
								MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
							}
						}
					}

					//update data
					id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
					aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);

					//Replant Cactus
					if (minecart.getDataValue("AutoReCactus") != null || isFarmingActive(minecart, FarmType.Cactus)) {
						if (id == Material.SAND.getId()) {
							if (aboveId == Material.AIR.getId()) {

								// Need to check for blocks to the sides of the cactus position 
								// as this would normally block planting.

								int sidemx = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x-1, y+1, z);
								int sidepx = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x+1, y+1, z);
								int sidemz = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z-1);
								int sidepz = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z+1);

								boolean blockcactus = false;

								if(sidemx != Material.AIR.getId()) { blockcactus = true; }
								if(sidepx != Material.AIR.getId()) { blockcactus = true; }
								if(sidemz != Material.AIR.getId()) { blockcactus = true; }
								if(sidepz != Material.AIR.getId()) { blockcactus = true; }


								if (blockcactus == false && minecart.removeItem(Material.CACTUS.getId())) {
									MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.CACTUS.getId(), x, y+1, z);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void doAutoFertilize(MinecartManiaStorageCart minecart) {
		return; // Todo: Make this work right without flashing trees/crops.
		/* 
		if (minecart.getDataValue("AutoFertilize") == null) {
		 	return;
		}

		if (minecart.getEntityDetectionRange() < 1) {
			return;
		}

		Location loc = minecart.getLocation().clone();
		int range = minecart.getEntityDetectionRange();

		int allowcycle = (range * 2) + 1;
		
		// In theory if we only run a refertilize cycle at range * 2 we should not overlap
		// refertilizing which should block the flashing tree/crops effect. Assuming we only increase the
		// count by 1 per each block. 
		 
		if(blockcycle == allowcycle) {
			blockcycle = 0;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						//Setup data
						int x = loc.getBlockX() + dx;
						int y = loc.getBlockY() + dy;
						int z = loc.getBlockZ() + dz;
						World w = minecart.getWorld();

						int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);

						if (minecart.getDataValue("AutoFertilize") != null) {
							int data = MinecartManiaWorld.getBlockData(w, x, y, z);

							if(id == Material.SAPLING.getId()) {
								if (minecart.removeItem(Material.INK_SACK.getId(), 1, (short)15))  {
									// Remove 1 unit of bonemeal and try to dump a tree

									int rand = ((new Random()).nextInt(5));
									TreeType t = null;
									switch (rand) {
									case 0: t = TreeType.BIG_TREE; break;
									case 1: t = TreeType.BIRCH; break;
									case 2: t = TreeType.REDWOOD; break;
									case 3: t = TreeType.TALL_REDWOOD; break;
									case 4: t = TreeType.TREE; break;
									//default: t = TreeType.TREE; 
									}
									MinecartManiaWorld.setBlockAt(w, 0, x, y, z);
									w.generateTree(new Location(w, x, y, z), t);
								}
							} else if (id == Material.CROPS.getId()) {
								if (data != 0x7) {
									if (minecart.removeItem(Material.INK_SACK.getId(), 1, (short) 15)) {
										MinecartManiaWorld.setBlockData(w, 0x7, x, y, z);
									}
								}
							}
						}
					}
				} 
			}

		} else {
			blockcycle++;
			log.info("Blockcycle: " + blockcycle);
		}
	*/
	}

	/**
	 * Returns true of the farm type is set (via [Farm] sign)
	 * @param minecart
	 * @param farmType
	 * @return
	 */
	public static boolean isFarmingActive(MinecartManiaStorageCart minecart, String farmType)
	{
		Object value = minecart.getDataValue("Farm");
		if(value != null && value instanceof String) {
			String strValue = ((String)value).toLowerCase();
			return strValue.equals(farmType.toLowerCase());
		}
		return false;
	}
	
	/**
	 * Returns all blocks in farming range of the minecart
	 * @param minecart
	 * @return
	 */
	public static ArrayList<Block> getBlocksInRange(MinecartManiaStorageCart minecart)
	{
		Location loc = minecart.getLocation().clone();
		int range = minecart.getRange();
		int rangeY = minecart.getRangeY();
		World w = minecart.getWorld();
		
		ArrayList<Block> blocks = new ArrayList<Block>();

		for (int dx = -(range); dx <= range; dx++)
		{
			for (int dy = -(rangeY); dy <= rangeY; dy++)
			{
				for (int dz = -(range); dz <= range; dz++)
				{
					int x = loc.getBlockX() + dx;
					int y = loc.getBlockY() + dy;
					int z = loc.getBlockZ() + dz;
					
					blocks.add(MinecartManiaWorld.getBlockAt(w, x, y, z));
				}				
			}
		}
		return blocks;
	}
}
