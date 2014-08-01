package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.chests.CollectionUtils;
import com.afforess.minecartmania.chests.ItemContainer;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import com.afforess.minecartmania.signs.SignAction;

import java.util.ArrayList;

public class DepositItemsAction extends SignAction {

    @Override
    public boolean execute(MMMinecart incminecart) {
        if (!(incminecart instanceof MMStorageCart)) return false;

        MMStorageCart minecart = (MMStorageCart) (incminecart);
        ArrayList<ItemContainer> derp = CollectionUtils.getItemContainers(this.loc.getBlock().getLocation(), minecart.getDirection(), false);

        for (ItemContainer cont : derp) {
            cont.addDirection(minecart.getDirection());
            cont.doCollection(minecart);
        }

        return true;
    }

    @Override
    public boolean async() {
        return true;
    }

    @Override
    public boolean process(String[] lines) {
        return lines[0].toLowerCase().contains("[deposit item");
    }

    @Override
    public String getPermissionName() {
        return "deposititemssign";
    }

    @Override
    public String getFriendlyName() {
        return "Deposit Items";
    }

}