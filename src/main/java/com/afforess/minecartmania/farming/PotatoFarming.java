package com.afforess.minecartmania.farming;

import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class PotatoFarming extends FarmingBase {

    private static Random rand = new Random();

    public static void doAutoFarm(MMStorageCart minecart) {
        if (isPotatoFarmingActive(minecart)) {
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
                        int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y + 1, z);
                        boolean dirty = false; //set when the data gets changed
                        //Harvest fully grown crops first

                        int data = MinecartManiaWorld.getBlockData(minecart.getWorld(), x, y, z);

                        if (id == Material.POTATO.getId()) {
                            //fully grown

                            if (data == 0x7) {
                                int c = rand.nextInt(4) + 1; //1-4
                                for (int i = 0; i < c; i++) {
                                    minecart.addItem(Material.POTATO_ITEM.getId());
                                }

                                if (rand.nextInt(100) < 2) minecart.addItem(Material.POISONOUS_POTATO.getId()); //2%

                                MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.AIR.getId(), x, y, z);
                                dirty = true;
                            }
                        }

                        //update data
                        if (dirty) {
                            id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                            aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y + 1, z);
                            dirty = false;
                        }
                        //till soil

                        if (id == Material.GRASS.getId() || id == Material.DIRT.getId()) {
                            if (aboveId == Material.AIR.getId()) {
                                MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.SOIL.getId(), x, y, z);
                                dirty = true;
                            }
                        }

                        //update data
                        if (dirty) {
                            id = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                            aboveId = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y + 1, z);
                            dirty = false;
                        }
                        //Seed tilled land

                        if (id == Material.SOIL.getId()) {
                            if (aboveId == Material.AIR.getId()) {
                                if (minecart.removeItem(Material.POTATO_ITEM.getId())) {
                                    MinecartManiaWorld.setBlockAt(minecart.getWorld(), Material.POTATO.getId(), x, y + 1, z);
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


    private static boolean isPotatoFarmingActive(MMStorageCart minecart) {
        return FarmingBase.isFarmingActive(minecart, FarmType.Potato);
    }
}
