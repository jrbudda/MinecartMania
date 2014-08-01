package com.afforess.minecartmania.signs;

import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.signs.actions.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


public enum ActionList {
    LaunchPlayerSign(LaunchPlayerAction.class),
    LaunchMineCartSign(LaunchMinecartAction.class),
    SetStationSign(SetStationAction.class),
    StopAtDestinationSign(StopAtDestinationAction.class),
    LockCartSign(LockCartAction.class),
    UnlockCartSign(UnLockCartAction.class),
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
    EjectionSign(EjectAction.class),
    AnnouncementSign(AnnouncementAction.class),
    HoldingForSign(HoldingForAction.class),
    ElevatorSign(ElevatorAction.class),
    EjectAtSign(EjectAtAction.class),
    FarmSign(FarmAction.class),
    MinimumItemSign(MinimumItemAction.class),
    MaximumItemSign(MaximumItemAction.class),
    JumpSign(JumpAction.class),
    StationSign(StationAction.class),
    KillSign(KillAction.class),
    PlatformSign(PlatformAction.class),
    SpawnSign(SpawnAction.class),
    SetSpeedSign(SetSpeedAction.class),
    MagnetSign(MagnetAction.class),
    PromptSign(PromptAction.class),
    CatchSign(CatchAction.class),
    CompressItemsSigns(CompressItemsAction.class),
    CollectItemsSign(CollectItemsAction.class),
    DepositItemsSign(DepositItemsAction.class),
    TrashItemsSign(TrashItemsAction.class),
    SmeltItemsSign(SmeltItemsAction.class);
    private final Class<? extends SignAction> action;
    private final String setting;
    private final String key;
    private final Object value;
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

    public static java.util.List<SignAction> getSignActionsforLines(String[] lines) {
        List<SignAction> out = new ArrayList<SignAction>();

        com.afforess.minecartmania.config.RedstoneState rs = com.afforess.minecartmania.config.RedstoneState.NoEffect;

        if (lines[0].contains("(ON)")) {
            rs = com.afforess.minecartmania.config.RedstoneState.Enables;
            lines[0] = lines[0].replace("(ON)", "");
        } else if (lines[0].contains("(OFF)")) {
            rs = com.afforess.minecartmania.config.RedstoneState.Disables;
            lines[0] = lines[0].replace("(OFF)", "");
        } else if (lines[0].contains("(TRIGGERON)")) {
            rs = com.afforess.minecartmania.config.RedstoneState.TriggerOn;
            lines[0] = lines[0].replace("(TRIGGERON)", "");
        } else if (lines[0].contains("(TRIGGEROFF)")) {
            rs = com.afforess.minecartmania.config.RedstoneState.TriggerOff;
            lines[0] = lines[0].replace("(TRIGGEROFF)", "");
        }

        for (ActionList type : ActionList.values()) {
            //TODO dont make instances unless sucessfull.
            SignAction action = type.getInstance();

            if (lines.length == 0) continue;

            //the following will determine the redstone state of the whole sign.
            if (action.process(lines)) {
                action.redstonestate = rs;
                out.add(action);
            }

        }
        return out;
    }

    public Class<? extends SignAction> getSignClass() {
        return action;
    }

    public SignAction getInstance() {
        try {
            Constructor<? extends SignAction> constructor;
            SignAction action;
            if (this.setting == null) {
                constructor = this.action.getConstructor();
                action = constructor.newInstance();
            } else if (this.key == null) {
                constructor = this.action.getConstructor(String.class);
                action = constructor.newInstance(this.setting);
            } else {
                constructor = this.action.getConstructor(String.class, String.class, Object.class);
                action = constructor.newInstance(this.setting, this.key, this.value);
            }
            return action;
        } catch (Exception e) {
            Logger.severe("Failed to read sign!");
            Logger.severe("Sign was :" + this.action);
            e.printStackTrace();
        }
        return null;
    }
}
