package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.MinecartUtils;
import org.bukkit.util.Vector;

public class LaunchMinecartAction extends SignAction {
    private volatile Vector launchSpeed = null;
    private volatile boolean previous = false;
    private boolean reverse = false;

    public boolean execute(MMMinecart minecart) {
        if (minecart == null) {
            minecart = MinecartUtils.getNearestMinecartInRange(loc, 2);
        }

        if (minecart == null) return false;

        if (minecart.isMoving()) {
            return false;
        }

        final MMMinecart mc = minecart;

        if (previous) {
            if (minecart.getPreviousDirectionOfMotion() != null && minecart.getPreviousDirectionOfMotion() != CompassDirection.NO_DIRECTION) {
                if (MinecartUtils.validMinecartTrack(minecart.getLocation(), minecart.getPreviousDirectionOfMotion())) {
                    mc.setMotion(mc.getPreviousDirectionOfMotion(), 0.6D);
                } else minecart.launchCart(reverse);
            } else minecart.launchCart(reverse);

            minecart.setFrozen(false);
            return true;

        } else if (launchSpeed != null) {
            mc.setMotion(launchSpeed);
        } else {
            minecart.launchCart(reverse);
        }

        return false;

    }


    public boolean async() {
        return false;
    }

    public boolean process(String[] lines) {
        this.executeAcceptsNull = true;
        if (launchSpeed == null) {
            previous = false;
            launchSpeed = null;
            for (String line : lines) {
                if (line.toLowerCase().contains("[previous dir2")) {
                    reverse = true;
                    previous = true;
                    break;
                } else if (line.toLowerCase().contains("[previous dir")) {
                    previous = true;
                    break;
                } else if (line.toLowerCase().contains("[launch n")) {
                    launchSpeed = new Vector(0, 0, -0.6D);
                    break;
                } else if (line.toLowerCase().contains("[launch e")) {
                    launchSpeed = new Vector(0.6D, 0, 0);
                    break;
                } else if (line.toLowerCase().contains("[launch s")) {
                    launchSpeed = new Vector(0, 0, 0.6D);
                    break;
                } else if (line.toLowerCase().contains("[launch w")) {
                    launchSpeed = new Vector(-0.6D, 0, 0);
                    break;
                } else if (line.toLowerCase().contains("[launch2") && !line.toLowerCase().contains("player")) {
                    reverse = true;
                    return true;
                } else if (line.toLowerCase().contains("[launch") && !line.toLowerCase().contains("player")) {
                    return true;
                }

            }

        }
        return launchSpeed != null || previous;
    }


    public String getPermissionName() {
        return "launchersign";
    }


    public String getFriendlyName() {
        return "Launcher " + (previous ? "Previous " : "") + (reverse ? "Reverse" : "");
    }

}
