package com.afforess.minecartmania.signs.actions;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class PlatformAction extends SignAction {

	private int range = -1;

	@Override
	public boolean execute(MinecartManiaMinecart minecart) {
		if (range <=0) return false;
		if ( minecart.isStandardMinecart() && minecart.getPassenger() == null) {
			List<Entity> list = minecart.getBukkitEntity().getNearbyEntities(range, range, range);

			LivingEntity closest = null;
			double distance = -1;
			for (Entity le : list) {
				if (le instanceof LivingEntity){
					if (le.getLocation().toVector().distanceSquared(minecart.getLocation().toVector()) < distance || closest == null) {
						closest = (LivingEntity) le;
						distance = le.getLocation().toVector().distanceSquared(minecart.getLocation().toVector());
					}
				}
			}

			if (closest != null && closest.getLocation().toVector().distanceSquared(minecart.getLocation().toVector()) < range) {
				//Let the world know about this
				VehicleEnterEvent vee = new VehicleEnterEvent(minecart.getBukkitEntity(), closest);
				MinecartMania.callEvent(vee);
				if (!vee.isCancelled()) {
					minecart.setPassenger(closest);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[platform")) {
				String[] split = line.split(":");
				if (split.length != 2) {
					range =com.afforess.minecartmania.config.Settings.DefaultPlatformRange;
					return true;
				}
				range = Integer.parseInt(StringUtils.getNumber(split[1]));
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "platformsign";
	}

	@Override
	public String getFriendlyName() {
		return "Platform Sign";
	}

}
