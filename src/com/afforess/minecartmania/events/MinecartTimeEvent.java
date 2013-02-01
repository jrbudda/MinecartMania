package com.afforess.minecartmania.events;

import java.util.Calendar;

import com.afforess.minecartmania.MMMinecart;

public class MinecartTimeEvent extends MinecartManiaEvent {
	private MMMinecart minecart;	
	private Calendar oldCalendar;
	private Calendar currentCalendar;
	public MinecartTimeEvent(MMMinecart cart, Calendar oldCal, Calendar newCal) {
		super("MinecartTimeEvent");
		minecart = cart;
		oldCalendar = oldCal;
		currentCalendar = newCal;
	}
	
	
	public MMMinecart getMinecart() {
		return minecart;
	}
	
	public Calendar getOldCalendar() {
		return oldCalendar;
	}
	
	public Calendar getCurrentCalendar() {
		return currentCalendar;
	}
}
