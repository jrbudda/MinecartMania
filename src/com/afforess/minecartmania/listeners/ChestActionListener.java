package com.afforess.minecartmania.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.chests.CollectionUtils;
import com.afforess.minecartmania.chests.ItemContainer;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.events.ChestPoweredEvent;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.events.MinecartDirectionChangeEvent;
import com.afforess.minecartmania.utils.BlockUtils;
import com.afforess.minecartmania.utils.ChestStorageUtil;
import com.afforess.minecartmania.utils.ChestUtil;
import com.afforess.minecartmania.utils.ComparableLocation;

public class ChestActionListener implements Listener{

	@EventHandler
	public void onChestPoweredEvent(ChestPoweredEvent event) {
		if (event.isPowered() && !event.isActionTaken()) {

			MinecartManiaChest chest = event.getChest();
			Item minecartType = ChestUtil.getMinecartType(chest);
			Location spawnLocation = ChestUtil.getSpawnLocationSignOverride(chest);
			if (spawnLocation == null) {
				spawnLocation = ChestStorageUtil.getSpawnLocation(chest);
			}
			if (spawnLocation != null && chest.contains(minecartType)) {
				if (chest.canSpawnMinecart() && chest.removeItem(minecartType.getId())) {

					//			CompassDirection direction = ChestUtil.getDirection(chest.getLocation(), spawnLocation);
					MinecartManiaWorld.spawnMinecart(spawnLocation, minecartType, chest);
					//			minecart.setMotion(direction, (Double)MinecartManiaWorld.getConfigurationValue("SpawnAtSpeed",.4));
					event.setActionTaken(true);
				}
			}
		}
	}

	@EventHandler
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			final MMMinecart minecart = event.getMinecart();

			boolean action = false;

			if (!action) {
				action = ChestStorageUtil.doMinecartCollection(minecart);
			}
			if (!action) {
				action = ChestStorageUtil.doCollectParallel(minecart);
			}
			if (!action && minecart.isStorageMinecart() && minecart.isOnRails()) {
				MinecartManiaStorageCart mmscart = (MinecartManiaStorageCart)event.getMinecart();
	
				ArrayList<Sign> signs = com.afforess.minecartmania.utils.SignUtils.getAdjacentSignList(mmscart.getLocation(), mmscart.getItemRange());
				
				Logger.debug("Found " + signs.size() + " signs ");

				ArrayList<ItemContainer> containers = CollectionUtils.getItemContainers((MinecartManiaStorageCart)event.getMinecart(), signs);

				Logger.debug("Found " + containers.size() + " containers ");

				if (containers != null) {
					for (ItemContainer container : containers) {
						com.afforess.minecartmania.debug.Logger.debug("Processing container " + container.toString());
						container.addDirection(minecart.getDirectionOfMotion());
						container.doCollection((MinecartManiaInventory) minecart);				
					}
				}

				ChestStorageUtil.doItemCompression((MinecartManiaStorageCart) minecart);

			}
			event.setActionTaken(action);
		}
	}

	//	@EventHandler
	//	public void onMinecartDirectionChangeEvent(MinecartDirectionChangeEvent event) {
	//		if (event.getMinecart().isStorageMinecart()) {
	//			CollectionUtils.updateContainerDirections((MinecartManiaStorageCart)event.getMinecart());
	//		}
	//	}

	private HashSet<ComparableLocation> calculateLocationsInRange(MinecartManiaStorageCart minecart) {
		HashSet<ComparableLocation> previousBlocks = toComparableLocation(BlockUtils.getAdjacentLocations(minecart.getPrevLocation(), minecart.getItemRange()));
		HashSet<ComparableLocation> current = toComparableLocation(BlockUtils.getAdjacentLocations(minecart.getLocation(), minecart.getItemRange()));
		current.removeAll(previousBlocks);
		return current;
	}

	private static HashSet<ComparableLocation> toComparableLocation(HashSet<Location> set) {
		HashSet<ComparableLocation> newSet = new HashSet<ComparableLocation>(set.size());
		for (Location loc : set) {
			newSet.add(new ComparableLocation(loc));
		}
		return newSet;
	}


}
