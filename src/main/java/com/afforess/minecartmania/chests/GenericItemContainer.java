package com.afforess.minecartmania.chests;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.ItemUtils;

import java.util.HashSet;

public abstract class GenericItemContainer implements ItemContainer {
    protected final HashSet<CompassDirection> directions = new HashSet<CompassDirection>(4);
    protected String line = "";

    public GenericItemContainer(String line, CompassDirection direction) {
        directions.add(direction);
        this.line = line;
    }


    public boolean hasDirectionCondition() {
        return line.contains("+");
    }

    @Override
    public java.lang.String toString() {
        return "Collection Container " + line;
    }

    public boolean hasAmountCondition() {
        return line.contains("@");
    }


    public AbstractItem[] getRawItemList() {
        return ItemUtils.getItemStringToMaterial(line);
    }


    public AbstractItem[] getItemList(CompassDirection direction) {
        String[] list = {line};
        return ItemUtils.getItemStringListToMaterial(list, direction);
    }


    public void addDirection(CompassDirection direction) {
        if (hasDirectionCondition()) {
            directions.add(direction);
        }
    }
}
