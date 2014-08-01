package com.afforess.minecartmania.utils;

import net.minecraft.server.v1_7_R4.Block;
import org.bukkit.Location;
import org.bukkit.material.Sign;

public class EntityUtils {

    public static Location getValidLocation(org.bukkit.block.Block base, int Yrange) {
        if (base == null) return null;
        //TODO: ugh.

        Location out = null;
        org.bukkit.block.Block b = base;
        boolean issign = b.getType() == org.bukkit.Material.SIGN || b.getType() == org.bukkit.Material.SIGN_POST;

        if (issign) {
            Sign sign = (Sign) b.getState().getData();
            out = searchvert(base.getRelative(sign.getFacing()), 3);
            if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.NORTH), 3);
            if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.SOUTH), 3);
            if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.EAST), 3);
            if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.WEST), 3);
        } else {
            if (canStand(base)) out = base.getLocation();
            else {
                out = searchvert(base, 3);
                if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.NORTH), 3);
                if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.SOUTH), 3);
                if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.EAST), 3);
                if (out == null) out = searchvert(base.getRelative(org.bukkit.block.BlockFace.WEST), 3);
                else base = b;
            }
        }
        if (out != null) return out.add(.5, 0, .5);
        else return null;
    }

    private static Location searchvert(org.bukkit.block.Block start, int count) {
        start = start.getRelative(org.bukkit.block.BlockFace.DOWN);
        for (int i = 0; i < count; i++) {
            start = start.getRelative(org.bukkit.block.BlockFace.UP);
            if (canStand(start)) return start.getLocation();
        }
        return null;
    }

    static boolean canStand(org.bukkit.block.Block base) {
        org.bukkit.block.Block below = base.getRelative(0, -1, 0);
        if (!below.isEmpty() && Block.getById(below.getTypeId()).getMaterial().isSolid()) {
            if (base.isEmpty() || Block.getById(base.getTypeId()).getMaterial().isSolid() == false) {
                return true;
            }
        }
        return false;
    }


}
