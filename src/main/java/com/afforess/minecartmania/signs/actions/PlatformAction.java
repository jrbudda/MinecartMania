package com.afforess.minecartmania.signs.actions;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class PlatformAction extends SignAction {

	private int range = -1;

	@Override
	public boolean execute(MMMinecart minecart) {
		if (range <=0) return false;
		if ( minecart.isStandardMinecart() && !minecart.hasPassenger()) {
			List<Entity> list = minecart.getBukkitEntity().getNearbyEntities(range*2, range*2, range*2);

			LivingEntity closest = null;
			double distance = -1;
			for (Entity le : list) {
				if (le instanceof LivingEntity){
					if (le.getLocation().toVector().distanceSquared(minecart.getLocation().toVector()) < distance || closest == null) {
						if (!le.isInsideVehicle()){
							closest = (LivingEntity) le;
							distance = le.getLocation().toVector().distanceSquared(minecart.getLocation().toVector());
						}
					}
				}
			}

			if (closest != null ) {
				minecart.setPassenger(closest);		
				return true;
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
		return "Platform";
	}

}
