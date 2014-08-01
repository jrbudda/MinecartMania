package com.afforess.minecartmania.farming;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Random;

public class MelonFarming extends FarmingBase {

    private static Random rand = new Random();

    public static void doAutoFarm(MMStorageCart minecart) {
        if (isMelonFarmingActive(minecart)) {
            ArrayList<Block> roots = findRoots(minecart, Item.MELON_BLOCK.getId());

            for (Block root : roots) {
                Block melonBlock = root.getRelative(BlockFace.UP);
                if (melonBlock.getTypeId() == Item.MELON_BLOCK.getId()) {
                    //number of slices is random between 4 and 7
                    int numSlices = rand.nextInt(5) + 3;

                    //check add slices to inventory
                    minecart.addItem(Item.MELON.getId(), numSlices);

                    //remove melon block
                    melonBlock.setTypeIdAndData(Item.AIR.getId(), (byte) Item.AIR.getData(), true);

                    //set root to soil/farmland
                    root.setTypeIdAndData(Item.SOIL.getId(), (byte) Item.SOIL.getData(), true);
                }
            }
        }
    }


    private static boolean isMelonFarmingActive(MMStorageCart minecart) {
        return FarmingBase.isFarmingActive(minecart, FarmType.Melon);
    }
}
