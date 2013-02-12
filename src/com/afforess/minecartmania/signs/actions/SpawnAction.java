package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.MMSign;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.signs.MinecartTypeSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.SignUtils;

public class SpawnAction extends SignAction {
	private HashMap<Location, Long> lastSpawn = new HashMap<Location, Long>();

	@Override
	public boolean execute(MMMinecart minecart) {

		Long lastSpawn = this.lastSpawn.get(loc); //cooldown on spawner
		if (lastSpawn == null || (Math.abs(System.currentTimeMillis() - lastSpawn) > 1000)) {

			Location spawn = loc.clone().add(0, 1, 0);
			final MMMinecart newminecart = MinecartManiaWorld.spawnMinecart(spawn, getMinecartType(spawn), null);

			this.lastSpawn.put(loc, System.currentTimeMillis());

			return true;

			//dont do this here, spawning should proc the control blocks too.
			//			if (ControlBlockList.getLaunchSpeed(Item.materialToItem(b.getType())) != 0.0) {
			//				MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
			//					@Override
			//					public void run() {
			//						newminecart.launchCart(ControlBlockList.getLaunchSpeed(Item.materialToItem(b.getType())));
			//					}
			//				});
			//			}

		}

		return false;
	}

	private static Item getMinecartType(Location loc) {
		ArrayList<MMSign> signList = SignUtils.getAdjacentMMSignList(loc, 2);
		for (MMSign sign : signList) {
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
	
	
	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[spawn")) {
				this.executeAcceptsNull = true;
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "spawnsign";
	}

	@Override
	public String getFriendlyName() {
		return "Spawn";
	}

}
