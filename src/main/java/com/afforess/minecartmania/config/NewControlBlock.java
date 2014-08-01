package com.afforess.minecartmania.config;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.signs.SignAction;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewControlBlock {
    private List<SignAction> actions = new ArrayList<SignAction>();

    public NewControlBlock(Item item, List<SignAction> actions) {
        this.actions = actions;
    }

    public static boolean isCorrectState(Block block, RedstoneState state) {
        boolean power = block.isBlockIndirectlyPowered() || block.getRelative(org.bukkit.block.BlockFace.UP).isBlockIndirectlyPowered();

        if (block.getTypeId() == Item.POWERED_RAIL.getId()) {
            power = (block.getData() & 0x8) != 0;
        }

        switch (state) {
            case NoEffect:
                return true;
            case Enables:
                return power;
            case Disables:
                return !power;
            case TriggerOff:
            case TriggerOn:
                boolean on = false;
                boolean off = false;
                boolean last = false;

                if (block.hasMetadata("LastPower")) {
                    last = block.getMetadata("LastPower").get(0).asBoolean();
                }

                if (block.hasMetadata("ThisPower")) {
                    power = block.getMetadata("ThisPower").get(0).asBoolean();
                }

                off = last && !power;
                on = power && !last;

                return (on && state == RedstoneState.TriggerOn) || (off && state == RedstoneState.TriggerOff);
        }

        return false;
    }

    public boolean execute(com.afforess.minecartmania.minecarts.MMMinecart minecart, Location loc) {
        boolean success = false;

        for (SignAction a : actions) {
            if (minecart != null || ((minecart == null) && a.getexecuteAcceptsNull())) {

                if (isCorrectState(loc.getBlock(), a.redstonestate)) {
                    if (a.executeAsBlock(minecart, loc)) {
                        success = true;
                    }
                } else com.afforess.minecartmania.debug.Logger.debug("wrong restone state for " + a.getFriendlyName());

            }
        }

        return success;
    }

    public boolean hasSignAction(Class<? extends SignAction> action) {
        Iterator<SignAction> i = actions.iterator();
        while (i.hasNext()) {
            SignAction executor = i.next();
            if (action.isInstance(executor)) {
                return true;
            }
        }
        return false;
    }


}
