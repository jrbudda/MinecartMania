package com.afforess.minecartmania.listeners;


import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.NewControlBlockList;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.events.MinecartClickedEvent;
import com.afforess.minecartmania.events.MinecartDirectionChangeEvent;
import com.afforess.minecartmania.events.MinecartMotionStartEvent;
import com.afforess.minecartmania.events.MinecartMotionStopEvent;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.actions.StationAction;
import com.afforess.minecartmania.utils.MinecartUtils;
import com.afforess.minecartmania.utils.SignCommands;

public class CoreListener implements Listener{
	public CoreListener() {

	}

	@EventHandler
	public void onCreate(org.bukkit.event.vehicle.VehicleCreateEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			Logger.debug("onCreate " + event.getVehicle().toString());
			Minecart cart = (Minecart)event.getVehicle();
			MMMinecart minecart = MinecartManiaWorld.CreateMMMinecart(cart, true, null);
			minecart.findOwner();
		}
		
	}

	@EventHandler
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			Minecart cart = (Minecart)event.getVehicle();
			MMMinecart minecart = MinecartManiaWorld.CreateMMMinecart(cart, false, null);

			if (minecart.isDead()) {
				return;
			}

			//	MinecartManiaLogger.info(minecart.getEntityId() + ":" + WordUtils.printLoc(minecart.getLocation()) + " " + WordUtils.printVec(minecart.getMotion())+ " " + minecart.isOnRails());

			minecart.updateCalendar(); 
			if (minecart.isMoving()) {
				if (minecart.getDirectionOfMotion() != minecart.getPreviousDirectionOfMotion()) {
					MinecartMania.callEvent(new MinecartDirectionChangeEvent(minecart, minecart.getPreviousDirectionOfMotion(), minecart.getDirectionOfMotion()));
					minecart.setPreviousDirectionOfMotion(minecart.getDirectionOfMotion());
				}
			}

			//Fire new events
			if (minecart.wasMovingLastTick() && !minecart.isMoving()) {
				MinecartMotionStopEvent mmse = new MinecartMotionStopEvent(minecart);
				MinecartMania.callEvent(mmse);
				mmse.logProcessTime();
			}

			else if (!minecart.wasMovingLastTick() && minecart.isMoving()) {
				MinecartMotionStartEvent mmse = new MinecartMotionStartEvent(minecart);
				MinecartMania.callEvent(mmse);
				mmse.logProcessTime();
			}

			minecart.setWasMovingLastTick(minecart.isMoving());
			//	minecart.doLauncherBlock();

			//total hack workaround because of the inability to create runnables/threads w/o IllegalAccessError
			if (minecart.getDataValue("launch") != null) {
				minecart.launchCart(false);
				minecart.setDataValue("launch", null);
			}


			if (minecart.hasChangedPosition() || minecart.createdLastTick) {
				minecart.updateChunks();

				if (minecart.isAtIntersection()) {
					Logger.debug("intersection");

					if (NewControlBlockList.hasSignAction(minecart.getBlockBeneath(), StationAction.class)) {
						//on a station block, do nothing, the block run will itsself.

					}		
					else if(Settings.IntersectionPromptsMode == 0 ) {
						//always prompt
						new com.afforess.minecartmania.signs.actions.PromptAction().executeAsBlock(minecart, minecart.getLocation());

					}
				}

				if (!minecart.createdLastTick) {
					MinecartActionEvent mae = new MinecartActionEvent(minecart);
					MinecartMania.callEvent(mae);
					mae.logProcessTime();
				}

				minecart.handleControlBlocksAndSigns();

				MinecartUtils.updateNearbyItems(minecart);

				if (minecart.isOnRails()){
					SignCommands.updateSensors(minecart);
				}	

				minecart.updateLocation();
				minecart.createdLastTick = false;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		//does this go off?
		if (event.getVehicle() instanceof Minecart) {
			Logger.debug("ondestroy");
			MMMinecart minecart = MinecartManiaWorld.getMMMinecart((Minecart)event.getVehicle());
			event.setCancelled(true);
			minecart.killOptionalReturn();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onVehicleDamage(VehicleDamageEvent event) {

		if (event.getVehicle() instanceof Minecart) {
			MMMinecart minecart = MinecartManiaWorld.getMMMinecart((Minecart)event.getVehicle());

			//Start workaround for double damage events
			long lastDamage = -1;
			if (minecart.getDataValue("Last Damage") != null) {
				lastDamage = (Long)minecart.getDataValue("Last Damage");
			}
			if (lastDamage > -1) {
				if ((lastDamage + 100) > System.currentTimeMillis()) {
					return;
				}
			}
			minecart.setDataValue("Last Damage", System.currentTimeMillis());
			//End Workaround


			if (minecart.getPassenger() != null) {
				if (minecart.isOnRails()) {
					if(event.getAttacker() != null && event.getAttacker().getEntityId() == minecart.getPassenger().getEntityId()) {
						MinecartClickedEvent mce = new MinecartClickedEvent(minecart);
						MinecartMania.callEvent(mce);

						//dont break carts youre riding in.
						event.setDamage(0.0);
						event.setCancelled(true);

					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {	
		// this is not effectinve at doing anything except canceling pickup

		if (event.getVehicle() instanceof Minecart) {

			MMMinecart minecart = MinecartManiaWorld.CreateMMMinecart((Minecart)event.getVehicle(), false, null);
			Entity collisioner = event.getEntity();

			Logger.debug("Collision! " + event.getVehicle().getLocation() + " " + collisioner);

			if (minecart.isFrozen()){
				event.setCancelled(true);
				event.setCollisionCancelled(true);
				event.setPickupCancelled(true);
				return;
			}

			event.setCancelled(!Settings.MinecartCollisions);
			event.setCollisionCancelled(!Settings.MinecartCollisions);

			if (collisioner instanceof Player && !minecart.isApproaching(collisioner.getLocation())){
				//allow player push
				event.setCancelled(false);
				event.setCollisionCancelled(false);
				event.setPickupCancelled(true);			
			}
			else if(collisioner instanceof Minecart) {
				MMMinecart otherminecart = MinecartManiaWorld.CreateMMMinecart((Minecart) collisioner, false, null);
				if (otherminecart.isFrozen()){
					event.setCancelled(true);
					event.setCollisionCancelled(true);
					return;
				}
			}	
			else if (collisioner instanceof LivingEntity){
				event.setPickupCancelled(true);				 
			}		
		}

	}



	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void onVehicleEnter(VehicleEnterEvent event) {

		if(!(event.getVehicle() instanceof Minecart))return; 

		Logger.debug(event.getEntered().toString() + " enters cart " + event.getVehicle().getEntityId());


		if (event.getEntered() instanceof Player) {
			if (MinecartUtils.isBlockedFromEntering((Player)event.getEntered())) {
				event.setCancelled(true);
				((Player)event.getEntered()).sendMessage(Settings.getLocal("AdminControlsBlockMinecartEntry"));
				return;
			}
		}

		final MMMinecart minecart = MinecartManiaWorld.getMMMinecart((Minecart)event.getVehicle());

		//now handled in playerEntityInteract
		//		if (minecart !=null && minecart.getDataValue("Lock Cart") != null && minecart.isMoving()) {
		//			if (event.getEntered() instanceof Player)	((Player) event.getEntered()).sendMessage(Settings.getLocal("SignCommandsMinecartLockedError"));
		//			event.setCancelled(true);
		//			return;
		//		}

		//proc the cart on sucessful entity entrance. Delay one tick so the passenger shows up to the actions.
		if (event.isCancelled() == false){
			MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
				@Override
				public void run() {
					minecart.handleControlBlocksAndSigns();
					SignCommands.updateSensors(minecart);
				}
			});
		}

	}

	@EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getVehicle().isDead()) return;

		Logger.debug("Vehicle exit " + event.getExited().toString());

		if (event.getVehicle() instanceof Minecart) {

			MMMinecart minecart = MinecartManiaWorld.getMMMinecart((Minecart)event.getVehicle());

			if (minecart !=null && event.getExited() instanceof Player && minecart.isLocked()) {
				((Player)event.getExited()).sendMessage(Settings.getLocal("SignCommandsMinecartLockedError"));
				event.setCancelled(true);
			}
			else SignCommands.updateSensors(minecart);	
		}

	}

	@EventHandler
	public void onVehicleBounce(org.bukkit.event.vehicle.VehicleBlockCollisionEvent event) {
		//this event is still called if you cancel a vehicleentitycollision event.

		if (event.getVehicle() instanceof Minecart) {
			Logger.debug("Bounce! " + event.getVehicle().getLocation() + " " + event.getBlock().toString());
			//	event.getBlock().setType(org.bukkit.Material.PUMPKIN);
		}
	}


}
