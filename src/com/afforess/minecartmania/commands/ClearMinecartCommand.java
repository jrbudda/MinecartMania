package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.minecarts.MMMinecart;

public interface ClearMinecartCommand extends Command {
	public boolean shouldRemoveMinecart(MMMinecart minecart);
}
