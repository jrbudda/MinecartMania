package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class LockCartAction extends SignAction {
    @Override
    public boolean execute(MMMinecart minecart) {

        if (!minecart.isLocked() && minecart.hasPlayerPassenger()) {
            minecart.getPlayerPassenger().sendMessage(Settings.getLocal("SignCommandsMinecartLocked"));
        }
        minecart.setLocked(true);
        return true;

    }

    @Override
    public boolean process(String[] lines) {
        for (String line : lines) {
            if (line.toLowerCase().contains("[lock cart")) {
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
    public String getPermissionName() {
        return "lockcart";
    }

    @Override
    public String getFriendlyName() {
        return "Lock Cart";
    }

}
