package com.afforess.minecartmania.listeners;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.events.*;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.signs.actions.HoldSignData;
import com.afforess.minecartmania.utils.SignCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SignsActionListener implements Listener {

    @EventHandler
    public void onMinecartLaunchedEvent(MinecartLaunchedEvent event) {
        if (event.isActionTaken()) {
            return;
        }
        if (event.getMinecart().getDataValue("hold sign data") != null) {
            event.setActionTaken(true);
            return;
        }
    }


    @EventHandler
    public void onMinecartManiaMinecartCreatedEvent(MinecartManiaMinecartCreatedEvent event) {
        SignCommands.updateSensors(event.getMinecart());
    }

    @EventHandler
    public void onMinecartTimeEvent(MinecartTimeEvent event) {
        MMMinecart minecart = event.getMinecart();
        HoldSignData data = (HoldSignData) minecart.getDataValue("hold sign data");
        /*if (data == null) {
			try {
				data = MinecartManiaSignCommands.instance.getDatabase().find(HoldSignData.class).where().idEq(minecart.minecart.getEntityId()).findUnique();
			}
			catch (Exception e) {
				List<HoldSignData> list = MinecartManiaSignCommands.instance.getDatabase().find(HoldSignData.class).where().idEq(minecart.minecart.getEntityId()).findList();
				MinecartManiaSignCommands.instance.getDatabase().delete(list);
			}
		}*/
        if (data != null) {
            data.setTime(data.getTime() - 1);
            com.afforess.minecartmania.signs.MMSign sign = SignManager.getOrCreateMMSign(data.getSignLocation());
            if (sign == null) {
                minecart.setMotion(data.getMotion());
                minecart.setDataValue("hold sign data", null);
                minecart.setDataValue("HoldForDelay", null);
                return;
            }
            //update sign counter
            if (data.getLine() < sign.getNumLines() && data.getLine() > -1) {
                if (data.getTime() > 0) {
                    sign.setLine(data.getLine(), "[Holding For " + data.getTime() + "]");

                } else {
                    sign.setLine(data.getLine(), "");
                }
            }

            if (data.getTime() == 0) {
                minecart.setMotion(data.getMotion());
                minecart.setDataValue("hold sign data", null);
                minecart.setDataValue("HoldForDelay", null);
                //MinecartManiaSignCommands.instance.getDatabase().delete(data);
            } else {
                minecart.setDataValue("hold sign data", data);
                //MinecartManiaSignCommands.instance.getDatabase().update(data);
            }
        }
    }

    @EventHandler
    public void onMinecartManiaMinecartDestroyedEvent(MinecartManiaMinecartDestroyedEvent event) {
        SignCommands.updateSensors(event.getMinecart(), null);
    }

    @EventHandler
    public void onMinecartMotionStopEvent(MinecartMotionStopEvent event) {
        com.afforess.minecartmania.debug.Logger.debug("Motion Stop " + event.getMinecart().getEntityId());
        MMMinecart minecart = event.getMinecart();
        if (minecart.isLocked()) {
            minecart.setLocked(false);
            if (minecart.hasPlayerPassenger()) {
                minecart.getPlayerPassenger().sendMessage(Settings.getLocal("SignCommandsMinecartUnlocked"));
            }
        }
    }

    @EventHandler
    public void onMinecartMotionStartEvent(MinecartMotionStartEvent event) {
        MMMinecart minecart = event.getMinecart();
        if (minecart.getDataValue("HoldForDelay") != null) {
            minecart.stopCart();
            HoldSignData data = (HoldSignData) minecart.getDataValue("hold sign data");
            minecart.teleport(data.getMinecartLocation());
        }
    }


}
