package com.afforess.minecartmania.signs;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import com.afforess.minecartmania.signs.actions.*;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;

public enum ActionList {
	LaunchPlayerSign(LaunchPlayerAction.class),
	LaunchMineCartSign(LaunchMinecartAction.class),
	SetStationSign(SetStationAction.class),
	StopAtDestinationSign(StopAtDestinationAction.class),
	LockCartSign(LockCartAction.class),
	UnlockCartSign(UnlockCartAction.class),
	AutoSeedSign(DataValuecAction.class, "AutoSeed"),
	AutoTillign(DataValuecAction.class, "AutoTill"),
	AutoHarvestSign(DataValuecAction.class, "AutoHarvest"),
	AutoTimberSign(DataValuecAction.class, "AutoTimber"),
	AutoForestSign(DataValuecAction.class, "AutoForest"),
	AutoSugarSign(DataValuecAction.class, "AutoSugar"),
	AutoPlantSign(DataValuecAction.class, "AutoPlant"),
	AutoCactusSign(DataValuecAction.class, "AutoCactus"),
	AutoReCactusSign(DataValuecAction.class, "AutoReCactus"),
	AutoSeedOffSign(DataValuecAction.class, "Seed Off", "AutoSeed", null),
	AutoTillOffSign(DataValuecAction.class, "Till Off", "AutoTill", null),
	AutoHarvestOffSign(DataValuecAction.class, "Harvest Off", "AutoHarvest", null),
	AutoTimberOffSign(DataValuecAction.class, "Timber Off", "AutoTimber", null),
	AutoForestOffSign(DataValuecAction.class, "Forest Off", "AutoForest", null),
	AutoSugarOffSign(DataValuecAction.class, "Sugar Off", "AutoSugar", null),
	AutoPlantOffSign(DataValuecAction.class, "Plant Off", "AutoPlant", null),
	AutoCactusOffSign(DataValuecAction.class, "Cactus Off", "AutoCactus", null),
	AutoReCactusOffSign(DataValuecAction.class, "ReCactus Off", "AutoReCactus", null),
	AlterRangeSign(AlterRangeAction.class),
	SetMaximumSpeedSign(SetMaxSpeedAction.class),
	EjectionSign(EjectionAction.class),
	AnnouncementSign(AnnouncementAction.class),
	HoldingForSign(HoldingForAction.class),
	ElevatorSign(ElevatorAction.class),
	PassPlayerSign(PassPlayerAction.class),
	EjectAtSign(EjectAtAction.class),
	EjectionConditionAction(EjectionConditionAction.class),
	FarmSign(FarmAction.class),
	MinimumItemSign(MinimumItemAction.class),
	MaximumItemSign(MaximumItemAction.class),
	JumpSign(JumpAction.class),	
	StationSign(StationAction.class),
	KillSign(KillAction.class),
	PlatformSign(PlatformAction.class),
	SpawnSign(SpawnAction.class),
	SetSpeedSign(SetSpeedAction.class)
	
	;

	ActionList(final Class<? extends SignAction> action) {
		this.action = action;
		this.setting = null;
		this.key = null;
		this.value = null;
	}

	ActionList(final Class<? extends SignAction> action, String setting) {
		this.action = action;
		this.setting = setting;
		this.key = null;
		this.value = null;
	}

	ActionList(final Class<? extends SignAction> action, String setting, String key, Object value) {
		this.action = action;
		this.setting = setting;
		this.key = key;
		this.value = value;
	}
	private final Class<? extends SignAction> action;
	private final String setting;
	private final String key;
	private final Object value;

	public Class<? extends SignAction> getSignClass() {
		return action;
	}

	public static java.util.List<SignAction> getSignActionsforLines(String[] lines){
		List<SignAction> out = new LinkedList<SignAction>();
		for (ActionList type : ActionList.values()) {
			SignAction action = type.getSignAction();
			if (action.process(lines)) {
				out.add(action);
			}
		}
		return out;
	}

	public SignAction getSignAction() {
		try {
			Constructor<? extends SignAction> constructor;
			SignAction action;
			if (this.setting == null) {
				constructor = this.action.getConstructor();
				action = constructor.newInstance();
			}
			else if (this.key == null) {
				constructor = this.action.getConstructor(String.class);
				action = constructor.newInstance(this.setting);
			}
			else {
				constructor = this.action.getConstructor(String.class, String.class, Object.class);
				action = constructor.newInstance(this.setting, this.key, this.value);
			}
			return action;
		} catch (Exception e) {
			MinecartManiaLogger.getInstance().severe("Failed to read sign!");
			MinecartManiaLogger.getInstance().severe("Sign was :" + this.action);
			e.printStackTrace();
		}
		return null;
	}
}
