package com.afforess.minecartmania.signs.actions;

import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.signs.SignAction;

public class CompressItemsAction extends SignAction {

	@Override
	public boolean execute(MMMinecart incminecart) {
		if(!(incminecart instanceof MinecartManiaStorageCart)) return false;

		MinecartManiaStorageCart	 minecart = (MinecartManiaStorageCart) (incminecart);

		HashSet<Block> blockList = minecart.getAdjacentBlocks(2);
		for (Block block : blockList) {
			if (block.getTypeId() == Item.WORKBENCH.getId()) {
				//TODO handling for custom recipies?
				Item[][] compressable = { {Item.IRON_INGOT, Item.GOLD_INGOT, Item.LAPIS_LAZULI, Item.EMERALD}, {Item.IRON_BLOCK , Item.GOLD_BLOCK, Item.LAPIS_BLOCK, Item.EMERALD_BLOCK} };
				int n = 0;
				for (Item m : compressable[0]) {
					int amt = 0;
					int slot = 0;
					for (ItemStack item : minecart.getContents()) {
						if (item != null && m.equals(Item.getItem(item))) {
							amt += item.getAmount();
							minecart.setItem(slot, null);
						}
						slot++;
					}
					int compressedAmt = amt / 9;
					int left = amt % 9;
					while (compressedAmt > 0) {
						minecart.addItem(compressable[1][n].getId(), Math.min(64, compressedAmt));
						compressedAmt -= Math.min(64, compressedAmt);
					}
					if (left > 0) {
						ItemStack item = compressable[0][n].toItemStack();
						item.setAmount(left);
						minecart.addItem(item);
					}

					n++;
				}
			}
		}
		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[compress")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "compressitemssign";
	}

	@Override
	public String getFriendlyName() {
		return "Compress Items";
	}

}
