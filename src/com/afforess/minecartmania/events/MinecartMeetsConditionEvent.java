package com.afforess.minecartmania.events;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.MMSign;

public class MinecartMeetsConditionEvent extends MinecartManiaEvent{
	private MinecartManiaMinecart minecart;
	private String[] Conditions;
	private boolean condition = false;

	public MinecartMeetsConditionEvent(MinecartManiaMinecart minecart, String[] conditions) {
		super("MinecartMeetsConditionEvent");
		this.minecart = minecart;
		this.Conditions = conditions;
	}
	
	public MinecartManiaMinecart getMinecart() {
		return minecart;
	}
	
	public String[] getConditions(){
		return this.Conditions;
	}
	
	public boolean isMeetCondition() {
		return condition;
	}
	
	public void setMeetCondition(boolean condition) {
		this.condition = condition;
	}

}
