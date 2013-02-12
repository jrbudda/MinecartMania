package com.afforess.minecartmania.config;

import org.bukkit.ChatColor;

import com.afforess.minecartmania.entity.Item;

public  class Settings {

	public static final int DefaultPlatformRange = 4;
	public static int MaxAllowedRange = 5;

	//core

	public static boolean DefaultSlowWhenEmpty;
	public static boolean RemoveDeadCarts;
	public static boolean LoadChunksOnTrack;
	public static boolean KillMobsOnTrack;
	public static boolean ReturnCartsToOwner;
	public static boolean DisappearonDisconnect;

	//physics
	public static boolean MinecartCollisions;
	public static boolean DefaultMagneticRail;
	public static double DefaultPassengerFrictionPercent;
	public static double DefaultEmptyFrictionPercent;
	public static double defaultJumpHeight;
	public static double DefaultMaxSpeedPercent;
	public static double MaxAllowedSpeedPercent;
	public static double DefaultDerailedFrictionPercent;
	public static double SlopeSpeedPercent;
	public static double MaxPassengerPushPercent;
	//new
	public static boolean KillPlayersOnTrack;


	//Signs
	public static int SensorDisableDelay = 8;

	//Intersections
	public static int IntersectionPromptsMode;
	public static int StationParingMode;
	public static boolean StationCommandSaveAfterUse;

	public static boolean StationsUseOldDirections = false;

	//Admin
	public static int EmptyMinecartKillTimer;
	public static int EmptyStorageMinecartKillTimer;
	public static int EmptyPoweredMinecartKillTimer;
	public static Item RailAdjusterTool;
	public static double KillPlayersOnTrackMinnimumSpeed;
	public static boolean IgnorePlayersOnTrack;

	public static boolean RememeberEjectionLocations;

	//Chests
	public static int	ChestDispenserSpawnDelay;
	public static int ItemCollectionRange;
	public static boolean StackAllItems;

	//


	public static boolean isKeepMinecartsLoaded() {
		return LoadChunksOnTrack;
	}

	public static boolean isMinecartsKillMobs() {
		return KillMobsOnTrack;
	}



	public static String getLocal(String key, Object ...args ){
		return String.format(parseColors(com.afforess.minecartmania.MinecartMania.getInstance().getConfig().getString("Messages."+key,"(key not found:" + key+")")),args);
	}

	public static String parseColors(String str) {
		for (ChatColor color : ChatColor.values()) {
			String name = "\\[" + color.name().toUpperCase() + "]";
			str = str.replaceAll(name, color.toString());
		}
		return str;
	}

}
