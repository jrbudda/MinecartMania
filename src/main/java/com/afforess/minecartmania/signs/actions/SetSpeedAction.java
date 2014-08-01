package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class SetSpeedAction extends SignAction {

    protected double percent = 0;
    protected boolean isMultiplier = false;

    public boolean execute(MMMinecart minecart) {
        if (isMultiplier) {
            minecart.multiplyMotion(percent);
        } else {
            minecart.setMotion(minecart.getDirection(), .4D * percent / 100);
        }
        return true;
    }

    public boolean async() {
        return false;
    }

    public boolean process(String[] lines) {
        for (String line : lines) {
            if (line.toLowerCase().contains("[set speed")) {
                String[] split = line.toLowerCase().split(":");
                if (split.length != 2) continue;
                if (split[1].contains("x")) isMultiplier = true;
                percent = Double.parseDouble(StringUtils.getNumber(split[1].split("x")[0]));
                return true;
            }
        }
        return false;
    }


    public String getPermissionName() {
        return "setspeedsign";
    }


    public String getFriendlyName() {
        return "Set Speed " + percent + (isMultiplier ? "x" : "");
    }

}
