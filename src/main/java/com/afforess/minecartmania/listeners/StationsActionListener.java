package com.afforess.minecartmania.listeners;

import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.MinecartManiaPlayer;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.events.*;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.stations.StationConditions;
import com.afforess.minecartmania.utils.DirectionUtils;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.StationUtil;
import com.afforess.minecartmania.utils.StringUtils;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class StationsActionListener implements Listener {

    @EventHandler
    public void onMinecartActionEvent(MinecartActionEvent event) {
        MMMinecart minecart = event.getMinecart();

        if (!minecart.isAtIntersection()) {
            //set an intersection back the way it way.

            if (minecart.getDataValue("old rail data") != null) {

                @SuppressWarnings("unchecked")
                final ArrayList<Integer> blockData = (ArrayList<Integer>) minecart.getDataValue("old rail data");
                final World world = minecart.getWorld();

                MinecartManiaWorld.setBlockData(world, blockData.get(0), blockData.get(1), blockData.get(2), blockData.get(3));

                //				MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
                //					@Override
                //					public void run() {
                //
                //					}
                //				},2*20);

            }
            minecart.setDataValue("old rail data", null);
        }

        //stop moving, there is a queue ahead of us
        //TODO Fix this
        /*MinecartManiaMinecart minecartAhead = minecart.getMinecartAhead();
		while (true) {
			if (minecartAhead == null) {
				break;
			}
			if (minecartAhead.minecart.getEntityId() == minecart.minecart.getEntityId()) {
				break;
			}
			if (minecartAhead.getMinecartAhead() == null) {
				break;
			}
			if (minecartAhead.isMoving()) {
				break;
			}
			minecartAhead = minecartAhead.getMinecartAhead();
		}
		if (minecartAhead != null) {
			if (minecartAhead.isAtIntersection()) {
				if (!minecartAhead.isMoving()) {
					minecart.setDataValue("queued velocity", minecart.minecart.getVelocity().clone());
					minecart.stopCart();
					if (minecart.hasPlayerPassenger())
						ChatUtils.sendMultilineMessage(minecart.getPlayerPassenger(), "You've entered a queue. Please be patient.", ChatColor.YELLOW.toString());
				}
			}
		}*/

    }


    @EventHandler
    public void onMinecartMotionStartEvent(MinecartMotionStartEvent event) {
        MMMinecart minecart = event.getMinecart();
        if (minecart.isAtIntersection()) {
            StationUtil.updateQueue(minecart);
        }
    }

    @EventHandler
    public void onMinecartManiaMinecartDestroyedEvent(MinecartManiaMinecartDestroyedEvent event) {
        MMMinecart minecart = event.getMinecart();
        StationUtil.updateQueue(minecart);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.LOW)
    public void onMinecartClickedEvent(MinecartClickedEvent event) {
        if (event.isActionTaken()) {
            return;
        }
        MMMinecart minecart = event.getMinecart();
        if (StationUtil.isInQueue(minecart)) {
            event.setActionTaken(true);
            return;
        }

        CompassDirection facingDir;
        facingDir = DirectionUtils.getDirectionFromRotation((minecart.getPassenger().getLocation().getYaw()) % 360.0F);


        com.afforess.minecartmania.debug.Logger.debug("intersection click: " + facingDir);

        if (!event.getMinecart().isCooledDown()) {
            event.setActionTaken(true);
            return;
        }

        //	Vector velocity = (Vector)minecart.getDataValue("preintersection velocity");
        //	if (velocity == null) {
        //		return;
        //	}

        //	velocity = StationUtil.alterMotionFromDirection(facingDir, velocity);

        //responding to chat direction prompt
        if (minecart.isAtIntersection() && minecart.hasPlayerPassenger()) {
            if (StationUtil.isValidDirection(facingDir, minecart)) {

                int data = DirectionUtils.getMinetrackRailDataForDirection(facingDir, minecart.getDirection());
                Logger.debug("intersection click: valid dir: " + facingDir + " minecart dir: " + minecart.getDirection() + " data: " + data);
                if (data != -1) {
                    MinecartManiaWorld.setBlockData(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
                } else {
                    //trying to go back the other way.
                    minecart.reverse();
                }
                minecart.setFrozen(false);
            }

            event.setActionTaken(true);
        } else {

            if (minecart.getPlayerPassenger() != null) {
                new com.afforess.minecartmania.signs.actions.JumpAction().executeAsBlock(minecart, minecart.getLocation());
                event.setActionTaken(true);
            }
        }

        if (event.isActionTaken()) minecart.setCooldown();

    }

    @EventHandler
    public void onMinecartMeetConditionEvent(MinecartMeetsConditionEvent event) {
        if (event.isMeetCondition()) {
            return;
        }
        MMMinecart minecart = event.getMinecart();
        MinecartManiaPlayer player = null;
        Object old = null;
        if (minecart.hasPlayerPassenger()) {
            player = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger());
            old = player.getDataValue("Reset Station Data");
            player.setDataValue("Reset Station Data", true);
        }
        loop:
        for (int i = 0; i < event.getConditions().length; i++) {
            String line = StringUtils.removeBrackets(event.getConditions()[i].trim());
            for (StationConditions e : StationConditions.values()) {
                if (e.result(minecart, line)) {
                    event.setMeetCondition(true);
                    break loop;
                }
            }
        }
        if (player != null) {
            player.setDataValue("Reset Station Data", old);
        }
    }
}
