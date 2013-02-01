package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MMMinecart;

public interface ClearMinecartCommand extends Command {
	public boolean shouldRemoveMinecart(MMMinecart minecart);
}
