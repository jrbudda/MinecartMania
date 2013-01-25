package com.afforess.minecartmania.listeners;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.events.ChestPoweredEvent;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.events.MinecartDirectionChangeEvent;
import com.afforess.minecartmaniachestcontrol.itemcontainer.ItemCollectionManager;
import com.afforess.minecartmaniacore.entity.Item;
import com.afforess.minecartmaniacore.entity.MinecartManiaChest;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.BlockUtils;
import com.afforess.minecartmaniacore.utils.ChestStorageUtil;
import com.afforess.minecartmaniacore.utils.ChestUtil;
import com.afforess.minecartmaniacore.utils.ComparableLocation;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

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
					CompassDirection direction = ChestUtil.getDirection(chest.getLocation(), spawnLocation);
					MinecartManiaMinecart minecart = MinecartManiaWorld.spawnMinecart(spawnLocation, minecartType, chest);
					minecart.setMotion(direction, (Double)MinecartManiaWorld.getConfigurationValue("SpawnAtSpeed",.4));
					event.setActionTaken(true);
				}
			}
		}
	}
	

	@EventHandler
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			final MinecartManiaMinecart minecart = event.getMinecart();
			
			boolean action = false;
			
			if (!action) {
				action = ChestStorageUtil.doMinecartCollection(minecart);
			}
			if (!action) {
				action = ChestStorageUtil.doCollectParallel(minecart);
			}
			if (!action && minecart.isStorageMinecart()) {

				ItemCollectionManager.processItemContainer((MinecartManiaStorageCart)event.getMinecart());
				HashSet<ComparableLocation> locations = calculateLocationsInRange((MinecartManiaStorageCart)event.getMinecart());
				findSigns(locations);
				ItemCollectionManager.createItemContainers((MinecartManiaStorageCart)event.getMinecart(), locations);
				ChestStorageUtil.doItemCompression((MinecartManiaStorageCart) minecart);
			}
			event.setActionTaken(action);
		}
	}

	@EventHandler
	public void onMinecartDirectionChangeEvent(MinecartDirectionChangeEvent event) {
		if (event.getMinecart().isStorageMinecart()) {
			ItemCollectionManager.updateContainerDirections((MinecartManiaStorageCart)event.getMinecart());
		}
	}
	
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
	
	private void findSigns(Collection<ComparableLocation> locations) {
		Iterator<ComparableLocation> i = locations.iterator();
		while (i.hasNext()) {
			Location temp = i.next();
			if (!(temp.getBlock().getState() instanceof org.bukkit.block.Sign)) {
				i.remove();
			}
		}
	}

}
