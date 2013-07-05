package com.afforess.minecartmania.listeners;


import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.NewControlBlock;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.events.ChestPoweredEvent;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.signs.sensors.GenericSensor;
import com.afforess.minecartmania.signs.sensors.Sensor;
import com.afforess.minecartmania.signs.sensors.SensorConstructor;
import com.afforess.minecartmania.signs.sensors.SensorManager;


public class BlockListener implements Listener{

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


					if (com.afforess.minecartmania.config.NewControlBlockList.isControlBlock(type)){
						NewControlBlock ncb = com.afforess.minecartmania.config.NewControlBlockList.getControlBlock(type);
						b.setMetadata("LastPower", new org.bukkit.metadata.FixedMetadataValue(MinecartMania.getInstance(), event.getOldCurrent() > 0));
						b.setMetadata("ThisPower", new org.bukkit.metadata.FixedMetadataValue(MinecartMania.getInstance(), event.getNewCurrent() > 0));

						ncb.execute(null, b.getLocation());
			
					}

					//					//spawn blocks
					//					if (ControlBlockList.isSpawnMinecartBlock(type)) {
					//						if (ControlBlockList.getControlBlock(type).getSpawnState() != RedstoneState.Enables || power) {
					//							if (ControlBlockList.getControlBlock(type).getSpawnState() != RedstoneState.Disables || !power) {
					//								if (MinecartUtils.isTrack(b.getRelative(0, 1, 0).getTypeId())) {
					//									Long lastSpawn = this.lastSpawn.get(b.getLocation()); //cooldown on spawner
					//									if (lastSpawn == null || (Math.abs(System.currentTimeMillis() - lastSpawn) > 1000)) {
					//										Location spawn = b.getLocation().clone();
					//										spawn.setY(spawn.getY() + 1);
					//										final MinecartManiaMinecart minecart = MinecartManiaWorld.spawnMinecart(spawn, getMinecartType(b.getLocation()), null);
					//										this.lastSpawn.put(b.getLocation(), System.currentTimeMillis());
					//										if (ControlBlockList.getLaunchSpeed(Item.materialToItem(b.getType())) != 0.0) {
					//											MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
					//												@Override
					//												public void run() {
					//													minecart.launchCart(ControlBlockList.getLaunchSpeed(Item.materialToItem(b.getType())));
					//												}
					//											});
					//										}
					//									}
					//								}
					//							}
					//						}
					//					}



				}
			}
		}
	}



	@EventHandler(ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent event) {

		com.afforess.minecartmania.debug.Logger.debug("OnSignChange " +  event.getLine(0));
		Player player = event.getPlayer();

		List<SignAction> actions = com.afforess.minecartmania.signs.ActionList.getSignActionsforLines(event.getLines());

		for (SignAction action :actions){
			if (!MinecartMania.permissions.canCreateSign(event.getPlayer(), action.getPermissionName())) {
				event.setCancelled(true);
				if(player !=null)	player.sendMessage(Settings.getLocal("LackPermissionForSign", action.getFriendlyName()));
				SignManager.remove(event.getBlock());
				break;
			}
		}

		//Schedule next tick so the sign updates naturally.
		MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(),new Runnable(){
			@Override
			public void run() {
			 SignManager.getOrCreateMMSign(event.getBlock().getLocation(), event.getPlayer());
			}
		});


	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {

		MMSign sign = SignManager.getOrCreateMMSign(event.getBlock());
		if(sign == null) return; 

		org.bukkit.entity.Player player = event.getPlayer();

		for(SignAction action:sign.getSignActions()){
			if (!MinecartMania.permissions.canBreakSign(player, action.getPermissionName())) {
				event.setCancelled(true);
				if(player !=null) player.sendMessage(Settings.getLocal("LackPermissionToRemoveSign", action.getFriendlyName()));
				break;
			}
		}

	}

	public static void checkSensor(Location loc, Player player){
		Sensor previous = SensorManager.getSensor(loc);
		if (previous == null) {
			Sensor sensor = SensorConstructor.constructSensor((org.bukkit.block.Sign)loc.getBlock().getState(),player);
			if (sensor != null) {
				SensorManager.addSensor(loc, sensor);
			}
		}
		else if (!SensorManager.verifySensor((org.bukkit.block.Sign)loc.getBlock().getState(), previous)) {
			Sensor sensor = SensorConstructor.constructSensor((org.bukkit.block.Sign)loc.getBlock().getState(), player);
			if (sensor != null) {
				SensorManager.addSensor(loc, sensor);
			}
			else {
				SensorManager.delSensor(loc);
			}
		}

	}

	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			checkSensor(event.getBlock().getLocation(), event.getPlayer());
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
