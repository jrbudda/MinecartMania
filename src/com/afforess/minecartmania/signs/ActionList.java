package com.afforess.minecartmania.signs;

import java.lang.reflect.Constructor;

import com.afforess.minecartmania.signs.actions.AlterRangeAction;
import com.afforess.minecartmania.signs.actions.AnnouncementAction;
import com.afforess.minecartmania.signs.actions.EjectAtAction;
import com.afforess.minecartmania.signs.actions.EjectionAction;
import com.afforess.minecartmania.signs.actions.EjectionConditionAction;
import com.afforess.minecartmania.signs.actions.ElevatorAction;
import com.afforess.minecartmania.signs.actions.FarmAction;
import com.afforess.minecartmania.signs.actions.GenericAction;
import com.afforess.minecartmania.signs.actions.HoldingForAction;
import com.afforess.minecartmania.signs.actions.JumpAction;
import com.afforess.minecartmania.signs.actions.LaunchMinecartAction;
import com.afforess.minecartmania.signs.actions.LaunchPlayerAction;
import com.afforess.minecartmania.signs.actions.LockCartAction;
import com.afforess.minecartmania.signs.actions.MaximumItemAction;
import com.afforess.minecartmania.signs.actions.MinimumItemAction;
import com.afforess.minecartmania.signs.actions.PassPlayerAction;
import com.afforess.minecartmania.signs.actions.SetMaxSpeedAction;
import com.afforess.minecartmania.signs.actions.SetStationAction;
import com.afforess.minecartmania.signs.actions.StopAtDestinationAction;
import com.afforess.minecartmania.signs.actions.UnlockCartAction;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;

public enum ActionList {
	LaunchPlayerSign(LaunchPlayerAction.class),
	LaunchMineCartSign(LaunchMinecartAction.class),
	SetStationSign(SetStationAction.class),
	StopAtDestinationSign(StopAtDestinationAction.class),
	LockCartSign(LockCartAction.class),
	UnlockCartSign(UnlockCartAction.class),
	AutoSeedSign(GenericAction.class, "AutoSeed"),
	AutoTillign(GenericAction.class, "AutoTill"),
	AutoHarvestSign(GenericAction.class, "AutoHarvest"),
	AutoTimberSign(GenericAction.class, "AutoTimber"),
	AutoForestSign(GenericAction.class, "AutoForest"),
	AutoSugarSign(GenericAction.class, "AutoSugar"),
	AutoPlantSign(GenericAction.class, "AutoPlant"),
	AutoCactusSign(GenericAction.class, "AutoCactus"),
	AutoReCactusSign(GenericAction.class, "AutoReCactus"),
	AutoSeedOffSign(GenericAction.class, "Seed Off", "AutoSeed", null),
	AutoTillOffSign(GenericAction.class, "Till Off", "AutoTill", null),
	AutoHarvestOffSign(GenericAction.class, "Harvest Off", "AutoHarvest", null),
	AutoTimberOffSign(GenericAction.class, "Timber Off", "AutoTimber", null),
	AutoForestOffSign(GenericAction.class, "Forest Off", "AutoForest", null),
	AutoSugarOffSign(GenericAction.class, "Sugar Off", "AutoSugar", null),
	AutoPlantOffSign(GenericAction.class, "Plant Off", "AutoPlant", null),
	AutoCactusOffSign(GenericAction.class, "Cactus Off", "AutoCactus", null),
	AutoReCactusOffSign(GenericAction.class, "ReCactus Off", "AutoReCactus", null),
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
	
	public SignAction getSignAction(Sign sign) {
		try {
			Constructor<? extends SignAction> constructor;
			SignAction action;
			if (this.setting == null) {
				constructor = this.action.getConstructor(Sign.class);
				action = constructor.newInstance(sign);
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
