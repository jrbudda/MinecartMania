package com.afforess.minecartmania.utils;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.Comparator;

public class SignUtils {


    public static boolean signMatches(Sign s1, Sign s2) {
        return s1.getBlock().getLocation().equals(s2.getBlock().getLocation());
    }

    /**
     * Returns the sign at the given world, x, y, z, coordinate, or null if none exits
     *
     * @param w World
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Sign getSignAt(World w, int x, int y, int z) {
        switch (w.getBlockTypeIdAt(x, y, z)) {
            case 63:
            case 68:
                return (Sign) w.getBlockAt(x, y, z).getState();
            default:
                return null;
        }
    }


    public static ArrayList<Sign> getAdjacentSignList(Location location, int range) {
        return getAdjacentSignList(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), range);
    }


    private static ArrayList<Sign> getAdjacentSignList(World w, int x, int y, int z, int range) {
        ArrayList<Sign> signList = new ArrayList<Sign>();

        //		if (!force && Settings.isLimitedSignRange()) {
        //			signList.addAll(getParallelSignList(w, x, y, z));
        //			signList.addAll(getSignBeneathList(w, x, y, z, 2));
        //			return signList;
        //		}

        for (int dx = -(range); dx <= range; dx++) {
            for (int dy = -(range); dy <= range; dy++) {
                for (int dz = -(range); dz <= range; dz++) {
                    Sign sign = getSignAt(w, x + dx, y + dy, z + dz);
                    if (sign != null) {
                        signList.add(sign);
                    }
                }
            }
        }


        java.util.Collections.sort(signList, new SignDistanceComparator(x, y, z));

        return signList;
    }

    private static ArrayList<Sign> getAdjacentSignListforDirection(World w, int x, int y, int z, int range, CompassDirection dir) {
        ArrayList<Sign> signList = new ArrayList<Sign>();

        switch (dir) {
            case NORTH:
            case SOUTH:
                for (int dx = -(range); dx <= range; dx++) {
                    for (int dy = -(range); dy <= range; dy++) {
                        Sign sign = getSignAt(w, x + dx, y + dy, z);
                        if (sign != null) {
                            signList.add(sign);
                        }
                    }
                }
                return signList;
            case EAST:
            case WEST:
                for (int dy = -(range); dy <= range; dy++) {
                    for (int dz = -(range); dz <= range; dz++) {
                        Sign sign = getSignAt(w, x, y + dy, z + dz);
                        if (sign != null) {
                            signList.add(sign);
                        }
                    }
                }
                return signList;
            default:
                return getAdjacentSignList(w, x, y, z, range);
        }


    }


    public static ArrayList<com.afforess.minecartmania.signs.MMSign> getAdjacentMMSignList(Location location, int range) {
        ArrayList<Sign> list = getAdjacentSignList(location, range);
        ArrayList<com.afforess.minecartmania.signs.MMSign> signList = new ArrayList<com.afforess.minecartmania.signs.MMSign>(list.size());
        for (Sign s : list) {
            signList.add(SignManager.getOrCreateMMSign(s.getBlock()));
        }
        return signList;
    }

    public static ArrayList<com.afforess.minecartmania.signs.MMSign> getAdjacentMMSignListforDirection(Location location, int range, CompassDirection dir) {
        ArrayList<Sign> list = getAdjacentSignListforDirection(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), range, dir);
        ArrayList<com.afforess.minecartmania.signs.MMSign> signList = new ArrayList<com.afforess.minecartmania.signs.MMSign>(list.size());
        for (Sign s : list) {
            signList.add(SignManager.getOrCreateMMSign(s.getBlock()));
        }
        return signList;
    }

    public static Item getNearbyMinecartTypeSpecifier(Location location, Item defaultItem) {
        com.afforess.minecartmania.debug.Logger.debug("Looking for [type:] signs");
        ArrayList<MMSign> list = SignUtils.getAdjacentMMSignList(location, com.afforess.minecartmania.config.Settings.SpawnSignRange);

        for (com.afforess.minecartmania.signs.MMSign sign : list) {
            for (String l : sign.getLines()) {
                if (l.toLowerCase().contains("[type:stor")) {
                    return Item.STORAGE_MINECART;
                } else if (l.toLowerCase().contains("[type:pow")) {
                    return Item.POWERED_MINECART;
                } else if (l.toLowerCase().contains("[type:hop")) {
                    return Item.MINECART_HOPPER;
                } else if (l.toLowerCase().contains("[type:tnt")) {
                    return Item.MINECART_TNT;
                } else if (l.toLowerCase().contains("[type:nor")) {
                    return Item.MINECART;
                }
            }
        }
        return defaultItem;
    }


    static class SignDistanceComparator implements Comparator<Sign> {
        private int x, y, z;

        public SignDistanceComparator(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        protected double getSquaredDistanceFromLocation(Sign sign) {
            double x = sign.getX() - this.x;
            double y = sign.getY() - this.y;
            double z = sign.getZ() - this.z;
            return x * x + y * y + z * z;
        }

        public int compare(Sign sign1, Sign sign2) {
            double i1 = getSquaredDistanceFromLocation(sign1);
            double i2 = getSquaredDistanceFromLocation(sign1);

            // If the distance differs, threshold it and return.
            if (i1 != i2)
                return (int) Math.min(Math.max(i1 - i2, -1), 1);
            int d;

            // If the distance of two blocks is the same, sort them by x, then y, then z.
            // There's no particular reason for this, just that we don't want to claim
            // that two different blocks are the same

            d = (sign1.getX() - sign2.getX());
            if (d != 0)
                return Math.min(Math.max(d, -1), 1);

            d = (sign1.getY() - sign2.getY());
            if (d != 0)
                return Math.min(Math.max(d, -1), 1);

            d = (sign1.getZ() - sign2.getZ());

            return Math.min(Math.max(d, -1), 1);
        }


    }
}