package com.afforess.minecartmania.listeners;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.ControlBlockList;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.events.MinecartClickedEvent;
import com.afforess.minecartmania.events.MinecartIntersectionEvent;
import com.afforess.minecartmania.events.MinecartLaunchedEvent;
import com.afforess.minecartmania.events.MinecartManiaMinecartDestroyedEvent;
import com.afforess.minecartmania.events.MinecartMeetsConditionEvent;
import com.afforess.minecartmania.events.MinecartMotionStartEvent;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.stations.SignCommands;
import com.afforess.minecartmania.stations.StationCondition;
import com.afforess.minecartmaniacore.utils.DirectionUtils;
import com.afforess.minecartmaniacore.utils.StationUtil;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.StringUtils;
import com.afforess.minecartmaniacore.entity.MinecartManiaPlayer;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class StationsActionListener implements Listener {

	@EventHandler
	public void onMinecartActionEvent(MinecartActionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();

		if (!minecart.isAtIntersection()) {
			//set an intersection back the way it way.
			@SuppressWarnings("unchecked")
			final	ArrayList<Integer> blockData = (ArrayList<Integer>)minecart.getDataValue("old rail data");
			final World world = minecart.getWorld();

			if (minecart.getDataValue("old rail data") != null) {
				MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
					@Override
					public void run() {
						MinecartManiaWorld.setBlockData(world, blockData.get(0), blockData.get(1), blockData.get(2), blockData.get(3));
					}
				},2*20);

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
	public void onMinecartIntersectionEvent(MinecartIntersectionEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();

		if (event.isActionTaken()) {
			return;
		}

		if (ControlBlockList.isValidStationBlock(minecart)) {
			SignCommands.processStation(event);
		}

		if (event.isActionTaken()) {
			return;
		}

		if (StationUtil.shouldPromptUser(minecart, event)) {

			//	minecart.setDataValue("preintersection velocity", minecart.getMotion().clone());
			//	minecart.stopCart();
			minecart.setFrozen(true);
			com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info("freeze" );
			Player passenger = minecart.getPlayerPassenger();
			//set the track straight
			int data = DirectionUtils.getMinetrackRailDataForDirection(minecart.getDirection(), minecart.getDirection());
			Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ());
			ArrayList<Integer> blockData = new ArrayList<Integer>();
			blockData.add(new Integer(oldBlock.getX()));
			blockData.add(new Integer(oldBlock.getY()));
			blockData.add(new Integer(oldBlock.getZ()));
			blockData.add(new Integer(oldBlock.getData()));
			minecart.setDataValue("old rail data", blockData);
			if (data != -1) {
				MinecartManiaWorld.setBlockData(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
			}
			passenger.sendMessage(LocaleParser.getTextKey("StationsTapInDirection"));
			event.setActionTaken(true);
		}

	}

	@EventHandler
	public void onMinecartLaunchedEvent(MinecartLaunchedEvent event) {
		if (event.isActionTaken()) {
			return;
		}
		SignCommands.processStation(event);
	}

	@EventHandler
	public void onMinecartMotionStartEvent(MinecartMotionStartEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		if (minecart.isAtIntersection()) {
			StationUtil.updateQueue(minecart);
		}
	}

	@EventHandler
	public void onMinecartManiaMinecartDestroyedEvent(MinecartManiaMinecartDestroyedEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		StationUtil.updateQueue(minecart);
	}

	@EventHandler(priority = org.bukkit.event.EventPriority.LOW)
	public void onMinecartClickedEvent(MinecartClickedEvent event) {
		if (event.isActionTaken()) {
			return;
		}
		MinecartManiaMinecart minecart = event.getMinecart();
		if (StationUtil.isInQueue(minecart)) {
			event.setActionTaken(true);
			return;
		}

		CompassDirection facingDir;
		facingDir  = DirectionUtils.getDirectionFromRotation((minecart.getPassenger().getLocation().getYaw()) % 360.0F);


		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info("intersection click: " + facingDir);

		if(!event.getMinecart().isCooledDown()) {
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
				com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info("intersection click: valid dir" );
				int data = DirectionUtils.getMinetrackRailDataForDirection(facingDir, minecart.getDirection());
				if (data != -1) {
					MinecartManiaWorld.setBlockData(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
				}
				com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info("intersection click: unfreezer" );
				minecart.setFrozen(false);
				//	minecart.setDataValue("preintersection velocity", null);
			}

			event.setActionTaken(true);
		}
		else{
			if (minecart.getDataValue("Lock Cart") != null && minecart.isMoving()) {
				if (minecart.hasPlayerPassenger()) {
					minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("SignCommandsMinecartLockedError"));
				}
				event.setActionTaken(true);
			}
			if (minecart.getPlayerPassenger()!=null){
				new com.afforess.minecartmania.signs.actions.JumpAction().execute(minecart);
				event.setActionTaken(true);
			}			
		}

		if(event.isActionTaken())	minecart.setCooldown();

	}

	@EventHandler
	public void onMinecartMeetConditionEvent(MinecartMeetsConditionEvent event) {
		if (event.isMeetCondition()) {
			return;
		}
		MinecartManiaMinecart minecart = event.getMinecart();
		MinecartManiaPlayer player = null;
		Object old = null;
		if (minecart.hasPlayerPassenger()) {
			player = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger());
			old = player.getDataValue("Reset Station Data");
			player.setDataValue("Reset Station Data", true);
		}
		loop:	for (int i = 0; i < event.getConditions().length ; i++) {
			String line = StringUtils.removeBrackets(event.getConditions()[i].trim()); 
			for (StationCondition e : StationCondition.values()) {
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
