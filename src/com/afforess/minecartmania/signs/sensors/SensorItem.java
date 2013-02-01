package com.afforess.minecartmania.signs.sensors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.MinecartManiaPlayer;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;

public class SensorItem extends GenericSensor{
	private List<AbstractItem> detect = new ArrayList<AbstractItem>();

	public SensorItem(SensorType type, Sign sign, String name, List<AbstractItem> item) {
		super(type, sign, name);
		this.detect = item;
	}

	public void input(MMMinecart minecart) {
		boolean state = false;
		if (minecart != null) {
			for (AbstractItem item : detect) {
				if (minecart.isStorageMinecart()) {
					if (((MinecartManiaStorageCart)minecart).amount(item.type()) > (item.isInfinite() ? 0 : item.getAmount())) {
						state = true;
					}
					else {
						state = false;
						break;
					}
				}
				else if (minecart.hasPlayerPassenger()) {
					MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger());
					if (player.amount(item.type()) > (item.isInfinite() ? 0 : item.getAmount())) {
						state = true;
					}
					else {
						state = false;
						break;
					}
				}
			}
		}
		setState(state);
	}
}
