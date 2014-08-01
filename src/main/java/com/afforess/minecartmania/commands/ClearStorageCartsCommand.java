package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class ClearStorageCartsCommand extends ClearAllCartsCommand {

    @Override
    public CommandType getCommand() {
        return CommandType.ClearStorageCarts;
    }

    @Override
    public boolean shouldRemoveMinecart(MMMinecart minecart) {
        return minecart.isStorageMinecart();
    }

}