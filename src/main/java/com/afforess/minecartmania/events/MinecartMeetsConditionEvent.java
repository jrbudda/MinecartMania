package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartMeetsConditionEvent extends MinecartManiaEvent {
    private MMMinecart minecart;
    private String[] Conditions;
    private boolean condition = false;

    public MinecartMeetsConditionEvent(MMMinecart minecart, String[] conditions) {
        super("MinecartMeetsConditionEvent");
        this.minecart = minecart;
        this.Conditions = conditions;
    }

    public MMMinecart getMinecart() {
        return minecart;
    }

    public String[] getConditions() {
        return this.Conditions;
    }

    public boolean isMeetCondition() {
        return condition;
    }

    public void setMeetCondition(boolean condition) {
        this.condition = condition;
    }

}
