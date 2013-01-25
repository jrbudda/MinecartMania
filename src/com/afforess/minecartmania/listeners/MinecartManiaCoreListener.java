package com.afforess.minecartmania.listeners;

import java.util.ArrayList;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.events.MinecartClickedEvent;
import com.afforess.minecartmania.events.MinecartDirectionChangeEvent;
import com.afforess.minecartmania.events.MinecartIntersectionEvent;
import com.afforess.minecartmania.events.MinecartMotionStartEvent;
import com.afforess.minecartmania.events.MinecartMotionStopEvent;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.signs.actions.LaunchPlayerAction;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.utils.SignUtils;
import com.afforess.minecartmaniacore.utils.WordUtils;

public class MinecartManiaCoreListener implements Listener{
	public MinecartManiaCoreListener() {

	}
	
	@EventHandler
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			Minecart cart = (Minecart)event.getVehicle();
			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart(cart);
			
			if (minecart.isDead()) {
				return;
			}

//	MinecartManiaLogger.getInstance().info(minecart.getEntityId() + ":" + WordUtils.printLoc(minecart.getLocation()) + " " + WordUtils.printVec(minecart.getMotion())+ " " + minecart.isOnRails());
			
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
			minecart.doLauncherBlock();
			
			//total hack workaround because of the inability to create runnables/threads w/o IllegalAccessError
			if (minecart.getDataValue("launch") != null) {
				MinecartManiaLogger.getInstance().debug("launch");
				minecart.launchCart();
				minecart.setDataValue("launch", null);
			}
			
			if (minecart.hasChangedPosition() || minecart.createdLastTick) {
				minecart.updateChunks();
				if (minecart.isAtIntersection()) {
					MinecartIntersectionEvent mie = new MinecartIntersectionEvent(minecart);
					MinecartManiaLogger.getInstance().info("intersection");
					MinecartMania.callEvent(mie);
					mie.logProcessTime();
				}
				
				if (!minecart.createdLastTick) {
					MinecartActionEvent mae = new MinecartActionEvent(minecart);
					MinecartMania.callEvent(mae);
					mae.logProcessTime();
				}
				
				if(minecart.isOnControlBlock()){		
					minecart.doSpeedMultiplierBlock();
					minecart.doCatcherBlock();
					minecart.doPlatformBlock(); //platform must be after catcher block
					minecart.doElevatorBlock();
					minecart.doEjectorBlock();
				}

				MinecartUtils.updateNearbyItems(minecart);
				
				minecart.updateLocation();
				
				//should do last
				minecart.doKillBlock();
				minecart.createdLastTick = false;
			}
		}
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if (event.getVehicle() instanceof Minecart && !event.isCancelled()) {
			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)event.getVehicle());
			minecart.kill(false);
		}
	}

	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)event.getVehicle());
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
			if (!event.isCancelled()) {
				MinecartManiaLogger.getInstance().debug("Damage: " + event.getDamage() + " Existing: " + minecart.getDamage());
				if ((event.getDamage() * 10) + minecart.getDamage() > 40) {
					minecart.kill();
					event.setCancelled(true);
					event.setDamage(0);
				}
				if (minecart.getPassenger() != null) {
					if (minecart.isOnRails()) {
						if(event.getAttacker() != null && event.getAttacker().getEntityId() == minecart.getPassenger().getEntityId()) {
							MinecartClickedEvent mce = new MinecartClickedEvent(minecart);
							MinecartMania.callEvent(mce);
							if (mce.isActionTaken()) {
								event.setDamage(0);
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			Minecart cart = (Minecart)event.getVehicle();
			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart(cart);
			Entity collisioner = event.getEntity();
			
			if (minecart.doCatcherBlock()) {
				event.setCancelled(true);
				event.setCollisionCancelled(true);
				event.setPickupCancelled(true);
				return;
			}
			
			if (collisioner instanceof LivingEntity) {
				LivingEntity victim = (LivingEntity)(collisioner);
				if (!(victim instanceof Player) && !(victim instanceof Wolf)) {
					if (Settings.isMinecartsKillMobs()) {
						if (minecart.isMoving()) {
							victim.remove();
							event.setCancelled(true);
							event.setCollisionCancelled(true);
							event.setPickupCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.isCancelled() || !(event.getVehicle() instanceof Minecart)) {
			return;
		}
		
		final MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)event.getVehicle());
		if (minecart.getPassenger() != null) {
			return;
		}
		if (ControlBlockList.getLaunchSpeed(minecart.getItemBeneath()) != 0.0D) {
			if (!minecart.isMoving()) {
				ArrayList<Sign> signs = SignUtils.getAdjacentSignList(minecart, 2);
				for (Sign s : signs) {
					com.afforess.minecartmania.signs.Sign sign = SignManager.getSignAt(s.getBlock());
					if (sign.executeAction(minecart, LaunchPlayerAction.class)) {
						break;
					}
				}
			}
		}
	}


}
