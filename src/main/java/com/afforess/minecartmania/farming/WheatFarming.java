package com.afforess.minecartmania.farming;

import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class WheatFarming {
    public static void doAutoFarm(MMStorageCart minecart) {
        if (!isAutoHarvestActive(minecart) && !isAutoHarvestActive(minecart) && !isAutoSeedActive(minecart)) {
            return;
        }
        if (minecart.getFarmingRange() < 1) {
            return;
        }
        Location loc = minecart.getLocation().clone();
        int range = minecart.getFarmingRange();
        int rangeY = minecart.getFarmingRangeY();
        for (int dx = -(range); dx <= range; dx++) {
            for (int dy = -(rangeY); dy <= rangeY; dy++) {
                for (int dz = -(range); dz <= range; dz++) {
                    //Setup data
                    int x = loc.getBlockX() + dx;
                    int y = loc.getBlockY() + dy;
                    int z = loc.getBlockZ() + dz;
                    int id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                    boolean dirty = false; //set when the data gets changed
                    //Harvest fully grown crops first
                    if (isAutoHarvestActive(minecart)) {
                        int data = MinecartManiaWorld.getBlockData(minecart.getWorld(), x, y, z);
                        if (id == Material.CROPS.getId()) {
                            //fully grown
                            if (data == 0x7) {
                                com.afforess.minecartmania.debug.Logger.debug("Full grown wheat found at: " + x + " " + y + " " + z);
                                minecart.addItem(Material.WHEAT.getId());
                                minecart.addItem(Material.SEEDS.getId());
                                if ((new Random()).nextBoolean()) { //Randomly add second seed.
                                    minecart.addItem(Material.SEEDS.getId());
                                }

                                minecart.getWorld().getBlockAt(x, y, z).setType(Material.AIR);

                                dirty = true;
                            }
                        }
                    }
                    //update data
                    if (dirty) {
                        id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                        dirty = false;
                    }
                    //till soil
                    if (isAutoTillActive(minecart)) {
                        if (id == Material.GRASS.getId() || id == Material.DIRT.getId()) {
                            if (MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y + 1, z) == Material.AIR.getId()) {
                                MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.SOIL.getId(), x, y, z);
                                dirty = true;
                            }
                        }
                    }

                    //update data
                    if (dirty) {
                        id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                        dirty = false;
                    }

                    //Seed tilled land
                    if (isAutoSeedActive(minecart)) {
                        if (id == Material.SOIL.getId()) {
                            if (MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y + 1, z) == Material.AIR.getId()) {
                                com.afforess.minecartmania.debug.Logger.debug("Seed");
                                if (minecart.removeItem(Material.SEEDS.getId())) {
                                    MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.CROPS.getId(), x, y + 1, z);
                                    MinecartManiaWorld.setBlockData(minecart.getWorld(), x, y + 1, z, 0);
                                    dirty = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isAutoTillActive(MMStorageCart minecart) {
        return FarmingBase.isFarmingActive(minecart, FarmType.Wheat) || minecart.getDataValue("AutoTill") != null;
    }

    private static boolean isAutoSeedActive(MMStorageCart minecart) {
        return FarmingBase.isFarmingActive(minecart, FarmType.Wheat) || minecart.getDataValue("AutoSeed") != null;
    }

    private static boolean isAutoHarvestActive(MMStorageCart minecart) {
        return FarmingBase.isFarmingActive(minecart, FarmType.Wheat) || minecart.getDataValue("AutoHarvest") != null;
    }
}
