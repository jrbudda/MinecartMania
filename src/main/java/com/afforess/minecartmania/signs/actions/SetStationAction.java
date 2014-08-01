package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class SetStationAction extends SignAction {
    protected String station = null;


    public boolean execute(MMMinecart minecart) {
        minecart.setDestination(station);
        return true;
    }


    public boolean async() {
        return true;
    }


    public boolean process(String[] lines) {
        if (lines[0].toLowerCase().contains("[set station")) {
            String val[] = lines[0].split(":");

            station = "";

            if (val.length >= 2) station += StringUtils.removeBrackets(val[1].trim());
            //check following lines
            for (int i = 1; i < lines.length; i++) {
                station += StringUtils.removeBrackets(lines[i].trim());
            }
        }

        return station != null;
    }

    public String getPermissionName() {
        return "setstationsign";
    }


    public String getFriendlyName() {
        return "Set Station " + (station == null ? "" : station);
    }

}
