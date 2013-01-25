package com.afforess.minecartmania.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.config.RedstoneState;
import com.afforess.minecartmania.events.ChestPoweredEvent;
import com.afforess.minecartmania.events.MinecartManiaSignFoundEvent;
import com.afforess.minecartmania.signs.MinecartTypeSign;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.signs.sensors.GenericSensor;
import com.afforess.minecartmania.signs.sensors.Sensor;
import com.afforess.minecartmania.signs.sensors.SensorConstructor;
import com.afforess.minecartmania.signs.sensors.SensorManager;
import com.afforess.minecartmaniacore.entity.Item;
import com.afforess.minecartmaniacore.entity.MinecartManiaChest;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.utils.SignUtils;

public class BlockListener implements Listener{
	private HashMap<Location, Long> lastSpawn = new HashMap<Location, Long>();

	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {    
		if (event.getOldCurrent() > 0 && event.getNewCurrent() > 0) {
			return;
		}
		
		
		boolean power = event.getNewCurrent() > 0;
		Block block = event.getBlock();

		int range = 1;
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					//check all around powered block
					
					
					//chests
					final Block b = MinecartManiaWorld.getBlockAt(block.getWorld(), block.getX() + dx, block.getY() + dy, block.getZ() + dz);
					if (b.getState() instanceof Chest) {
						Chest chest = (Chest)b.getState();
						MinecartManiaChest mmc = MinecartManiaWorld.getMinecartManiaChest(chest);
						if (mmc != null) {
							boolean previouslyPowered = mmc.isRedstonePower();
							if (!previouslyPowered && power) {
								mmc.setRedstonePower(power);
								ChestPoweredEvent cpe = new ChestPoweredEvent(mmc, power);
								MinecartMania.callEvent(cpe);
							}
							else if (previouslyPowered && !power) {
								mmc.setRedstonePower(power);
								ChestPoweredEvent cpe = new ChestPoweredEvent(mmc, power);
								MinecartMania.callEvent(cpe);
							}
						}
					}
					
					Item type = Item.getItem(b.getTypeId(), b.getData());
					if (Item.getItem(b.getTypeId()).size() == 1) {
						type = Item.getItem(b.getTypeId()).get(0);
					}
					
					//spawn blocks
					if (ControlBlockList.isSpawnMinecartBlock(type)) {
						if (ControlBlockList.getControlBlock(type).getSpawnState() != RedstoneState.Enables || power) {
							if (ControlBlockList.getControlBlock(type).getSpawnState() != RedstoneState.Disables || !power) {
								if (MinecartUtils.isTrack(b.getRelative(0, 1, 0).getTypeId())) {
									Long lastSpawn = this.lastSpawn.get(b.getLocation());
									if (lastSpawn == null || (Math.abs(System.currentTimeMillis() - lastSpawn) > 1000)) {
										Location spawn = b.getLocation().clone();
										spawn.setY(spawn.getY() + 1);
										final MinecartManiaMinecart minecart = MinecartManiaWorld.spawnMinecart(spawn, getMinecartType(b.getLocation()), null);
										this.lastSpawn.put(b.getLocation(), System.currentTimeMillis());
										if (ControlBlockList.getLaunchSpeed(Item.materialToItem(b.getType())) != 0.0) {
											MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
												@Override
												public void run() {
													minecart.launchCart(ControlBlockList.getLaunchSpeed(Item.materialToItem(b.getType())));
												}
											});
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static Item getMinecartType(Location loc) {
		ArrayList<Sign> signList = SignUtils.getAdjacentMinecartManiaSignList(loc, 2);
		for (Sign sign : signList) {
			if (sign instanceof MinecartTypeSign) {
				MinecartTypeSign type = (MinecartTypeSign)sign;
				if (type.canDispenseMinecartType(Item.MINECART)) {
					return Item.MINECART;
				}
				if (type.canDispenseMinecartType(Item.POWERED_MINECART)) {
					return Item.POWERED_MINECART;
				}
				if (type.canDispenseMinecartType(Item.STORAGE_MINECART)) {
					return Item.STORAGE_MINECART;
				}
			}
		}

		//Returns standard minecart by default
		return Item.MINECART;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!(event.getBlock().getState() instanceof org.bukkit.block.Sign)) {
			return;
		}
		Sign sign = SignManager.getSignAt(event.getBlock().getLocation(), event.getPlayer());
		String[] old = new String[4];
		for (int i = 0; i < 4; i++) {
			old[i] = ((org.bukkit.block.Sign)event.getBlock().getState()).getLine(i);
			sign.setLine(i, event.getLine(i), false);
		}

		MinecartManiaSignFoundEvent mmsfe = new MinecartManiaSignFoundEvent(sign, event.getPlayer());
		MinecartMania.callEvent(mmsfe);
		sign = mmsfe.getSign();

		Collection<SignAction> actions = sign.getSignActions();
		Iterator<SignAction> i = actions.iterator();
		org.bukkit.entity.Player player = event.getPlayer();
		while (i.hasNext()) {
			SignAction action = i.next();
			if (!MinecartMania.permissions.canCreateSign(player, action.getName())) {
				event.setCancelled(true);
				player.sendMessage(LocaleParser.getTextKey("LackPermissionForSign", action.getFriendlyName()));
				SignManager.updateSign(sign.getLocation(), null);
				break;
			}
		}

		if (!event.isCancelled() && sign instanceof MinecartTypeSign) {
			if (!MinecartMania.permissions.canCreateSign(player, "minecarttypesign")) {
				player.sendMessage(LocaleParser.getTextKey("LackPermissionForSign", "Minecart Type Sign"));
				SignManager.updateSign(sign.getLocation(), null);
				event.setCancelled(true);
			}
		}

		if (event.isCancelled()) {
			for (int j = 0; j < 4; j++) {
				sign.setLine(j, old[j], false);
			}
		}
		else {
			SignManager.updateSign(sign.getLocation(), sign);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getBlock().getState() instanceof org.bukkit.block.Sign) {
			Sign sign = SignManager.getSignAt(event.getBlock().getLocation());
			Collection<SignAction> actions = sign.getSignActions();
			Iterator<SignAction> i = actions.iterator();
			org.bukkit.entity.Player player = event.getPlayer();
			while (i.hasNext()) {
				SignAction action = i.next();
				if (!MinecartMania.permissions.canBreakSign(player, action.getName())) {
					event.setCancelled(true);
					player.sendMessage(LocaleParser.getTextKey("LackPermissionToRemoveSign", action.getFriendlyName()));
					break;
				}
			}

			if (sign instanceof MinecartTypeSign) {
				if (!MinecartMania.permissions.canBreakSign(player, "minecarttypesign")) {
					player.sendMessage(LocaleParser.getTextKey("LackPermissionToRemoveSign", "Minecart Type Sign"));
					event.setCancelled(true);
				}
			}
		}
		if (event.isCancelled()) {
			final Location loc =	event.getBlock().getLocation();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
				public void run() {
					loc.getBlock().getState().update(true);
				}
			}, 5);
		}
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sensor previous = SensorManager.getSensor(event.getBlock().getLocation());
			if (previous == null) {
				Sensor sensor = SensorConstructor.constructSensor((org.bukkit.block.Sign)event.getBlock().getState(), event.getPlayer());
				if (sensor != null) {
					SensorManager.addSensor(event.getBlock().getLocation(), sensor);
				}
			}
			else if (!SensorManager.verifySensor((org.bukkit.block.Sign)event.getBlock().getState(), previous)) {
				Sensor sensor = SensorConstructor.constructSensor((org.bukkit.block.Sign)event.getBlock().getState(), event.getPlayer());
				if (sensor != null) {
					SensorManager.addSensor(event.getBlock().getLocation(), sensor);
				}
				else {
					SensorManager.delSensor(event.getBlock().getLocation());
				}
			}
		}
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) {
			return;
		}
		//Forces diode not to update and disable itself 
		if (event.getBlock().getTypeId() == Material.DIODE_BLOCK_ON.getId()) {
			ConcurrentHashMap<Block, Sensor> sensorList = SensorManager.getSensorList();
			Iterator<Entry<Block, Sensor>> i = sensorList.entrySet().iterator();
			while(i.hasNext()) {
				Entry<Block, Sensor> e = i.next();
				if (SensorManager.isSign(e.getKey()) && ((GenericSensor)e.getValue()).equals(event.getBlock().getLocation())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}


}
