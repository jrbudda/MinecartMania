package com.afforess.minecartmania.chests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;

import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.entity.MinecartManiaDoubleChest;
import com.afforess.minecartmania.entity.MinecartManiaFurnace;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.utils.BlockUtils;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.ItemUtils;
import com.afforess.minecartmania.utils.StringUtils;

public class CollectionUtils {


	public static boolean isFurnaceFuelLine(String line) {
		return line.toLowerCase().contains("[fuel:");
	}

	public static boolean isFurnaceSmeltLine(String line) {
		return line.toLowerCase().contains("[smelt:");
	}

	/**
	 * Merges lines on a sign into a single line for processing, when the direction on the lines match. Needed for support of the '!' character.
	 */
	public static ArrayList<String> getItemLines(Sign sign) {
		HashMap<CompassDirection, String> directions = new HashMap<CompassDirection, String>(5);
		ArrayList<String> lines = new ArrayList<String>(3);
		for (int line = 1; line < 4; line++) {
			String text = StringUtils.removeBrackets(sign.getLine(line)).trim();
			if (!text.isEmpty() && !isFurnaceFuelLine(text) && !isFurnaceSmeltLine(text)) {
				CompassDirection direction = ItemUtils.getLineItemDirection(text);
				if (!directions.containsKey(direction)) {
					directions.put(direction, text);
				}
				else {
					String format = text;
					if (direction != CompassDirection.NO_DIRECTION) {
						format = format.substring(2);
					}
					directions.put(direction, directions.get(direction) + ":" + format);
				}
			}
		}

		Logger.debug("Merged Item Strings");
		Iterator<Entry<CompassDirection, String>> i = directions.entrySet().iterator();
		while (i.hasNext()) {
			Entry<CompassDirection, String> entry = i.next();
			lines.add(entry.getValue());
			Logger.debug("Item String: " + entry.getValue());
		}
		return lines;
	}

	public static MinecartManiaInventory getMinecartManiaInventory(Block block) {
		MinecartManiaInventory inventory = null;
		if (block.getState() instanceof Chest) {
			inventory = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
			//check for double chest
			if (inventory != null && ((MinecartManiaChest) inventory).getNeighborChest() != null) {
				inventory = new MinecartManiaDoubleChest((MinecartManiaChest) inventory, ((MinecartManiaChest) inventory).getNeighborChest());
			}
		}
		else if (block.getState() instanceof Dispenser) {
			inventory = MinecartManiaWorld.getMinecartManiaDispenser((Dispenser)block.getState());
		}
		else if (block.getState() instanceof Furnace) {
			inventory = MinecartManiaWorld.getMinecartManiaFurnace((Furnace)block.getState());
		}
		return inventory;
	}

	public static ArrayList<ItemContainer> getItemContainers(Location location, CompassDirection direction, boolean collection) {
		ArrayList<ItemContainer> containers = new ArrayList<ItemContainer>();
		HashSet<Block> blocks = BlockUtils.getAdjacentBlocks(location, 1);
		HashSet<Block> toSkip = new HashSet<Block>();
		for (Block block : blocks) {
			if (getMinecartManiaInventory(block) != null && !toSkip.contains(block)) {
				MinecartManiaInventory inventory = getMinecartManiaInventory(block);
				if (inventory instanceof MinecartManiaDoubleChest) {
					MinecartManiaChest other = MinecartManiaChest.getNeighborChest(block.getWorld(), block.getX(), block.getY(), block.getZ());
					toSkip.add(other.getLocation().getBlock());
				}
				ArrayList<String> lines = getItemLines(((Sign)location.getBlock().getState()));
				for (String text : lines) {
					if (!text.isEmpty() && !isFurnaceFuelLine(text) && !isFurnaceSmeltLine(text)) {
						ItemContainer temp = null;
						if (collection) {
							Logger.debug("Found Inventory To Collect From");
							temp = new ItemCollectionContainer(inventory, text, direction);
						}
						else {
							if (inventory instanceof MinecartManiaFurnace) {
								Logger.debug("Found Furnace To Deposit From");
								temp = new FurnaceDepositItemContainer((MinecartManiaFurnace)inventory, text, direction);
							}
							else {
								Logger.debug("Found Inventory To Deposit From");
								temp = new ItemDepositContainer(inventory, text, direction);
							}
						}
						if (temp != null) {
							containers.add(temp);
						}
					}
				}
			}
		}
		return containers;
	}

	public static ArrayList<ItemContainer> getTrashItemContainers(Location location, CompassDirection direction) {
		ArrayList<ItemContainer> containers = new ArrayList<ItemContainer>();
		ArrayList<String> lines = getItemLines(((Sign)location.getBlock().getState()));
		for (String text : lines) {
			if (!text.isEmpty() && !isFurnaceFuelLine(text) && !isFurnaceSmeltLine(text)) {
				containers.add(new TrashItemContainer(text, direction));
			}
		}
		return containers;
	}

	public static ArrayList<ItemContainer> getFurnaceContainers(Location location, CompassDirection direction) {
		ArrayList<ItemContainer> containers = new ArrayList<ItemContainer>();
		HashSet<Block> blocks = BlockUtils.getAdjacentBlocks(location, 1);
		for (Block block : blocks) {
			if (getMinecartManiaInventory(block) != null && getMinecartManiaInventory(block) instanceof MinecartManiaFurnace) {
				MinecartManiaFurnace furnace = (MinecartManiaFurnace)getMinecartManiaInventory(block);
				for (int line = 0; line < 4; line++) {
					String text = ((Sign)location.getBlock().getState()).getLine(line);
					if (isFurnaceFuelLine(text)) {
						Logger.debug("Found Furnece to add Fuel to");
						containers.add(new FurnaceFuelContainer(furnace, text, direction));
					}
					else if (isFurnaceSmeltLine(text)) {
						Logger.debug("Found Furnece to add materials to");
						containers.add(new FurnaceSmeltContainer(furnace, text, direction));
					}
				}
			}
		}
		return containers;
	}



//	public static 	 ArrayList<ItemContainer>   getItemContainers(MinecartManiaStorageCart minecart, ArrayList<Sign> signs) {
//		ArrayList<ItemContainer> containers = new ArrayList<ItemContainer>();
//
//		for (Sign sign : signs) {
////			if (isItemCollectionSign(sign)) {
////				Logger.debug("Found Collect Item Sign");
////				bracketizeSign(sign);
////				containers.addAll(getItemContainers(sign.getBlock().getLocation(), minecart.getDirection(), true));
////			}
////			else if (isItemDepositSign(sign)) {
////				Logger.debug("Found Deposit Item Sign");
////				bracketizeSign(sign);
////				containers.addAll(getItemContainers(sign.getBlock().getLocation(), minecart.getDirection(), false));
////			}
////			else if (isTrashItemSign(sign)) {
////				Logger.debug("Found Trash Item Sign");
////				bracketizeSign(sign);
////				containers.addAll(getTrashItemContainers(sign.getBlock().getLocation(), minecart.getDirection()));
////			}
//			containers.addAll(getFurnaceContainers(sign.getBlock().getLocation(), minecart.getDirection()));
//		}
//
//		return containers;
//		
//	}

}
