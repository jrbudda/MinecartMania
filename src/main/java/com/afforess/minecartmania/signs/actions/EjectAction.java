package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.config.NewControlBlock;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.EntityUtils;
import com.afforess.minecartmania.utils.SignUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Map;

public class EjectAction extends SignAction {

    public boolean execute(MMMinecart minecart) {

        if (minecart.getPassenger() == null || loc == null) {
            return false;
        }

        //TODO: conditions.

        //		ArrayList<com.afforess.minecartmania.signs.MMSign> list = SignUtils.getAdjacentMinecartManiaSignList(minecart.getLocation(), 2);
        //
        //		boolean success = false;
        //		boolean found = false;
        //
        //		for (com.afforess.minecartmania.signs.MMSign sign : list) {
        //			if (sign.hasSignAction(EjectionConditionAction.class)) {
        //				found = true;
        //				if (sign.executeAction(minecart, EjectionConditionAction.class)) {
        //					success = true;
        //					break;
        //				}
        //			}
        //		}


        Location location = null;

        com.afforess.minecartmania.debug.Logger.debug("Looking for [eject at] signs");
        //look around for unprocessed ejectat signs
        ArrayList<MMSign> list = SignUtils.getAdjacentMMSignList(minecart.getLocation(), com.afforess.minecartmania.config.Settings.EjectSignRange);
        Double dist = null;

        for (com.afforess.minecartmania.signs.MMSign sign : list) {
            if (sign.executeAction(minecart, EjectAtAction.class) && (dist == null || sign.getLocation().distanceSquared(minecart.getLocation()) < dist)) {
                location = (Location) minecart.getDataValue("Eject At");
                dist = sign.getLocation().distanceSquared(minecart.getLocation());
            }
        }


        if (location == null) {
            com.afforess.minecartmania.debug.Logger.debug("Looking for [eject here] blocks");
            //look around for unprocessed ejecthere blocks
            Map<Location, NewControlBlock> blocklist = com.afforess.minecartmania.config.NewControlBlockList.getControlBlocksNearby(this.loc, com.afforess.minecartmania.config.Settings.EjectSignRange);
            dist = null;
            for (Location l : blocklist.keySet()) {
                NewControlBlock b = blocklist.get(l);
                if (b.hasSignAction(EjectAtAction.class) && (dist == null || l.distanceSquared(minecart.getLocation()) < dist)) {
                    if (b.execute(minecart, l)) {
                        dist = l.distanceSquared(minecart.getLocation());
                        location = (Location) minecart.getDataValue("Eject At");
                    }
                }
            }
        }
        if (location == null && com.afforess.minecartmania.config.Settings.RememeberEjectionLocations) {
            com.afforess.minecartmania.debug.Logger.debug("Checking for saved ejection spot");
            location = (Location) minecart.getDataValue("Eject At");
        }

        if (location == null && loc != null) {
            com.afforess.minecartmania.debug.Logger.debug("No ejection spot specified. Using original location");
            location = loc.clone().add(0, 1, 0);
        }

        if (location != null) {
            Entity passenger = minecart.getPassenger();

            Location flocation = EntityUtils.getValidLocation(location.getBlock(), 3);
            if (flocation == null) flocation = location.clone();
            flocation.setPitch(location.getPitch());
            flocation.setYaw(location.getYaw());

            if (flocation.getYaw() == 0 && flocation.getPitch() == 0) {
                flocation.setYaw(passenger.getLocation().getYaw());
                flocation.setPitch(passenger.getLocation().getPitch());
            }

            com.afforess.minecartmania.debug.Logger.debug("found valid eject location: " + flocation.toString());

            minecart.eject();
            return passenger.teleport(flocation);
        }

        return false;
    }


    public boolean async() {
        return false;
    }


    public boolean process(String[] lines) {
        for (String line : lines) {

            if (line.toLowerCase().contains("[eject") && !line.toLowerCase().contains("at") && !line.toLowerCase().contains("here")) {
                return true;
            }
        }
        return false;
    }


    public String getPermissionName() {
        return "ejectsign";
    }


    public String getFriendlyName() {
        return "Eject Sign";
    }

}
