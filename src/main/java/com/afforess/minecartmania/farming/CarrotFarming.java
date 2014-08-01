package com.afforess.minecartmania.farming;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;

import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMStorageCart;

public class CarrotFarming extends FarmingBase {

	private static Random rand = new Random();

	public static void doAutoFarm(MMStorageCart minecart)
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
						int id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
						int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
						boolean dirty = false; //set when the data gets changed
						//Harvest fully grown crops first

						int data = MinecartManiaWorld.getBlockData(minecart.getWorld(), x, y, z);
						if (id == Material.CARROT.getId()) {
							//fully grown
									
							if (data == 0x7) {			
								for (int i = 0; i <= rand.nextInt(5); i++) {
									minecart.addItem(Material.CARROT_ITEM.getId());
								}
								
								
								MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
								dirty = true;
							}
						}
						//update data
						if (dirty) {
							id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
							aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
							dirty = false;
						}
						//till soil

						if (id == Material.GRASS.getId() ||  id == Material.DIRT.getId()) {
							if (aboveId == Material.AIR.getId()) {
								MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.SOIL.getId(), x, y, z);
								dirty = true;
							}
						}

						//update data
						if (dirty) {
							id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
							aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
							dirty = false;
						}
						//Seed tilled land 

						if (id == Material.SOIL.getId()) {
							if (aboveId == Material.AIR.getId()) {
								if (minecart.removeItem(Material.CARROT_ITEM.getId())) {
									MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.CARROT.getId(), x, y+1, z);
									MinecartManiaWorld.setBlockData(minecart.getWorld(), x, y+1, z, 0);
									dirty = true;
								}
							}
						}


					}
				}
			}
		}
	}


	private static boolean isFarmingActive(MMStorageCart minecart)
	{
		return FarmingBase.isFarmingActive(minecart, FarmType.Carrot);
	}
}
