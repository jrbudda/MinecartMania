package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.MinecartManiaMinecart;

public interface ClearMinecartCommand extends Command {
	public boolean shouldRemoveMinecart(MinecartManiaMinecart minecart);
}
