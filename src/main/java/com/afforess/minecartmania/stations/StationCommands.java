package com.afforess.minecartmania.stations;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.actions.StationAction;
import com.afforess.minecartmania.utils.DirectionUtils;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.MinecartUtils;
import org.bukkit.block.Block;

import java.util.ArrayList;

public enum StationCommands implements Direction {
    Straight {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("STR") || str.toLowerCase().contains("straight"))
                return setMotion(minecart.getDirection(), minecart, false);
            else return false;
        }
    },
    North {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("N") || str.toLowerCase().contains("north"))
                return setMotion(CompassDirection.NORTH, minecart, true);
            else return false;
        }
    },
    East {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("E") || str.toLowerCase().contains("east"))
                return setMotion(CompassDirection.EAST, minecart, true);
            else return false;
        }
    },
    South {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("S") || str.toLowerCase().contains("south"))
                return setMotion(CompassDirection.SOUTH, minecart, true);
            else return false;
        }
    },
    West {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("W") || str.toLowerCase().contains("west"))
                return setMotion(CompassDirection.WEST, minecart, true);
            else return false;
        }
    },
    Left {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("L") || str.toLowerCase().contains("left")) {

                return setMotion(DirectionUtils.getLeftDirection(minecart.getDirection()), minecart, false);
            } else return false;
        }
    },
    Right {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("R") || str.toLowerCase().contains("right")) {

                return setMotion(DirectionUtils.getRightDirection(minecart.getDirection()), minecart, false);
            } else return false;
        }
    },
    Destroy {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("D") || str.toLowerCase().contains("destroy")) {
                new com.afforess.minecartmania.signs.actions.EjectAction().executeAsBlock(minecart, minecart.getLocation());
                minecart.setDestination("");
                minecart.killOptionalReturn();
                return true;
            }

            return false;
        }
    },
    Prompt {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("P") || str.toLowerCase().contains("prompt")) {
                return new com.afforess.minecartmania.signs.actions.PromptAction().executeAsBlock(minecart, minecart.getLocation());
            }
            return false;
        }
    },
    Eject {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("EJ") || str.toLowerCase().contains("eject")) {
                return new com.afforess.minecartmania.signs.actions.EjectAction().executeAsBlock(minecart, minecart.getLocation());
            }
            return false;
        }
    },
    Stop {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.toLowerCase().contains("stop")) {
                minecart.stopCart();
                return true;
            }
            return false;
        }
    },
    Vanish {
        public boolean execute(MMMinecart minecart, String str) {
            if (str.equals("V") || str.toLowerCase().contains("vanish")) {
                minecart.setDestination("");
                new com.afforess.minecartmania.signs.actions.EjectAction().executeAsBlock(minecart, minecart.getLocation());
                minecart.killNoReturn();
                return true;
            }

            return false;
        }
    };


    private static boolean setMotion(com.afforess.minecartmania.utils.DirectionUtils.CompassDirection direction, MMMinecart minecart, boolean convert) {

        if (Settings.StationsUseOldDirections && convert) direction = StationAction.convertFromOldDirections(direction);

        if (MinecartUtils.validMinecartTrack(minecart.getLocation(), direction)) {

            int data = DirectionUtils.getMinetrackRailDataForDirection(direction, minecart.getDirection());
            if (data != -1) {
                //Force the game to remember the old data of the rail we are on, and reset it once we are done
                Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ());
                ArrayList<Integer> blockData = new ArrayList<Integer>();
                blockData.add(new Integer(oldBlock.getX()));
                blockData.add(new Integer(oldBlock.getY()));
                blockData.add(new Integer(oldBlock.getZ()));
                blockData.add(new Integer(oldBlock.getData()));
                minecart.setDataValue("old rail data", blockData);

                //change the track dirtion
                oldBlock.setData((byte) data);

                return true;
            } else {
                if (DirectionUtils.getOppositeDirection(direction).equals(minecart.getDirection())) minecart.reverse();
                return true;
            }
        }
        return false;
    }


}
