package com.afforess.minecartmania.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import com.afforess.minecartmania.signs.MMSign;

public abstract class ChestStorageUtil {
	
	public static Location getSpawnLocation(MinecartManiaChest chest) {
		Block center = chest.getLocation().getBlock();
		Location result = getAdjacentTrack(center);
		if (result == null && chest.getNeighborChest() != null) {
			result = getAdjacentTrack(chest.getNeighborChest().getLocation().getBlock());
		}
		return result;
	}
	
	private static Location getAdjacentTrack(Block center) {
		if (MinecartUtils.isTrack(center.getRelative(-1, 0, 0))) {
			return center.getRelative(-1, 0, 0).getLocation();
		}
		else if (MinecartUtils.isTrack(center.getRelative(-1, -1, 0)) && MinecartUtils.isSlopedTrack(center.getRelative(-1, -1, 0))) {
			return center.getRelative(-1, 0, 0).getLocation();
		}
		if (MinecartUtils.isTrack(center.getRelative(0, 0, -1))) {
			return center.getRelative(0, 0, -1).getLocation();
		}
		else if (MinecartUtils.isTrack(center.getRelative(0, -1, -1)) && MinecartUtils.isSlopedTrack(center.getRelative(0, -1, -1))) {
			return center.getRelative(0, 0, -1).getLocation();
		}
		if (MinecartUtils.isTrack(center.getRelative(1, 0, 0))) {
			return center.getRelative(1, 0, 0).getLocation();
		}
		else if (MinecartUtils.isTrack(center.getRelative(1, -1, 0)) && MinecartUtils.isSlopedTrack(center.getRelative(1, -1, 0))) {
			return center.getRelative(1, 0, 0).getLocation();
		}
		if (MinecartUtils.isTrack(center.getRelative(0, 0, 1))) {
			return center.getRelative(0, 0, 1).getLocation();
		}
		else if (MinecartUtils.isTrack(center.getRelative(0, -1, 1)) && MinecartUtils.isSlopedTrack(center.getRelative(0, -1, 1))) {
			return center.getRelative(0, 0, 1).getLocation();
		}
		return null;
	}
	
	public static boolean doMinecartCollection(MMMinecart minecart) {
		if (minecart.getBlockTypeAhead() != null) {
			if (minecart.getBlockTypeAhead().getType().getId() == Item.CHEST.getId()) {
				
				
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)minecart.getBlockTypeAhead().getState());
				
				if (ChestUtil.isNoCollection(chest)) {
					return false;
				}
				
				if (minecart instanceof MMStorageCart) {
					MMStorageCart storageCart = (MMStorageCart)minecart;
					boolean failed = false;
					for (ItemStack item : storageCart.getInventory().getContents()) {
						if (!chest.addItem(item)) {
							failed = true;
							break;
						}
					}
					if (!failed) {
						storageCart.getInventory().clear();
					}
				}
				if (chest.addItem(minecart.getType().getId())) {
					
					minecart.killNoReturn();
					return true;
				}
			}
		}
		return false;
	}

	public static boolean doCollectParallel(MMMinecart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		for (Block block : blockList) {
			if (block.getState() instanceof Chest) {
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
				ArrayList<MMSign> signList = SignUtils.getAdjacentMMSignList(chest.getLocation(), 1);
				for (MMSign sign : signList) {
					for (int i = 0; i < sign.getNumLines(); i++) {
						if (sign.getLine(i).toLowerCase().contains("[parallel")) {
							sign.setLine(i, "[Parallel]");
							if (!minecart.isMovingAway(block.getLocation())) {
								if (chest.addItem(minecart.getType().getId())) {
									minecart.killNoReturn();
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static void doItemCompression(MMStorageCart minecart) {

	}

//	public static boolean doEmptyChestInventory(MinecartManiaStorageCart minecart) {
//		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
//		for (Sign sign : signList) {
//			if (sign.getLine(0).toLowerCase().contains("trash items")) {
//				//return InventoryUtils.doInventoryTransaction(minecart, null, sign, minecart.getDirectionOfMotion());
//			}
//		}
//		return false;
//	}
//
//	public static void setMaximumItems(MinecartManiaStorageCart minecart) {
//		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
//		for (Sign sign : signList) {
//			if (sign.getLine(0).toLowerCase().contains("max items")) {
//				String[] list = {sign.getLine(1), sign.getLine(2), sign.getLine(3) };
//				AbstractItem[] items = ItemUtils.getItemStringListToMaterial(list);
//				for (AbstractItem item : items) {
//					if (!item.isInfinite()) {
//						minecart.setMaximumItem(item.type(), item.getAmount());
//					}
//				}
//				sign.setLine(0, "[Max Items]");
//				if (!sign.getLine(1).isEmpty()) {
//					sign.setLine(1, StringUtils.addBrackets(sign.getLine(1)));
//				}
//				if (!sign.getLine(2).isEmpty()) {
//					sign.setLine(2, StringUtils.addBrackets(sign.getLine(2)));
//				}
//				if (!sign.getLine(3).isEmpty()) {
//					sign.setLine(3, StringUtils.addBrackets(sign.getLine(3)));
//				}
//				sign.update();
//			}
//		}
//	}
//	
//	public static void setMinimumItems(MinecartManiaStorageCart minecart) {
//		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart, 2);
//		for (Sign sign : signList) {
//			if (sign.getLine(0).toLowerCase().contains("min items")) {
//				String[] list = {sign.getLine(1), sign.getLine(2), sign.getLine(3) };
//				AbstractItem[] items = ItemUtils.getItemStringListToMaterial(list);
//				for (AbstractItem item : items) {
//					if (!item.isInfinite()) {
//						minecart.setMinimumItem(item.type(), item.getAmount());
//					}
//				}
//				sign.setLine(0, "[Min Items]");
//				if (!sign.getLine(1).isEmpty()) {
//					sign.setLine(1, StringUtils.addBrackets(sign.getLine(1)));
//				}
//				if (!sign.getLine(2).isEmpty()) {
//					sign.setLine(2, StringUtils.addBrackets(sign.getLine(2)));
//				}
//				if (!sign.getLine(3).isEmpty()) {
//					sign.setLine(3, StringUtils.addBrackets(sign.getLine(3)));
//				}
//				sign.update();
//			}
//		}
//	}

}
