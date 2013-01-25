package com.afforess.minecartmania.config;

import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public  class Settings {

	//core
	public static int DefaultMaxSpeedPercent = 200;
	public static int DefaultDerailedFrictionPercent = 100;
	public static boolean DefaultSlowWhenEmpty;
	public static boolean ClearTrack = true;
	public static int RangeXZ = 4;
	public static int RangeY = 4;
     public static int MaxRange = 5;
    public static boolean LoadChunksOnTrack = false;
	public static boolean KillMobsOnTrack = false;
	public static boolean ReturnCartsToOwner = false;
	public static boolean StackAllItems = true;
	public static boolean LimitedSignRange = true;
	public static boolean DisappearonDisconnect = true;
	
	//new
	public static boolean KillPlayersOnTrack;
	public static int DefaultPassengerFrictionPercent;
	public static int DefaultEmptyFrictionPercent;
	
	//Signs
	public static String AnnouncementPrefix;
    public static int SensorDisableDelay = 8;

    //Intersections
	public static int IntersectionPromptsMode;
	public static int StationParingMode;
	public static boolean StationCommandSaveAfterUse;
	
	//Admin
	public static int EmptyMinecartKillTimer;
	public static int EmptyStorageMinecartKillTimer;
	public static int EmptyPoweredMinecartKillTimer;
	public static int RailAdjusterTool;
	public static int KillPlayersOnTrackMinnimumSpeed;
	public static boolean IgnorePlayersOnTrack;
	
	
	//Chests
	public static boolean SpawnAtSpeed;
	public static int	ChestDispenserSpawnDelay;
	public static int ItemCollectionRange;
	
	
	//
	public static int defaultJumpHeight = 2;
	
	public static int getMaximumMinecartSpeedPercent() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("MaximumMinecartSpeedPercent",200);
	}

	public static int getDefaultMinecartDerailedSpeedPercent() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("DefaultMinecartDerailedSpeedPercent",100);
	}	 


	public static Boolean getDefaultMinecartSlowerWhenEmpty() {
		return (Boolean)MinecartManiaWorld.getConfigurationValue("MinecartSlowerWhenEmpty",true);
	}	 
	public static int getMinecartsClearRailsSetting() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("MinecartsClearRails", true);
	}

	public static int getMinecartRange() {
		return RangeXZ;
	}

	public static int getMinecartRangeY() {
		return RangeY;
	}

	public static int getMinecartMaximumRange() {
		return MaxRange;
	}

	public static boolean isKeepMinecartsLoaded() {
		return LoadChunksOnTrack;
	}

	public static boolean isMinecartsKillMobs() {
		return KillMobsOnTrack;
	}

	public static boolean isReturnMinecartToOwner() {
		return ReturnCartsToOwner;
	}

	public static boolean isStackAllItems() {
		return StackAllItems;
	}

	public static boolean isLimitedSignRange() {
		return LimitedSignRange;
	}

	public static boolean isDisappearOnDisconnect() {
		return DisappearonDisconnect;
	}

}
