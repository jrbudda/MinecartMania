package com.afforess.minecartmania.stations;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import com.afforess.minecartmania.signs.actions.StationAction;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.ItemUtils;

public enum StationConditions implements Condition {
    Default {
        public boolean result(MMMinecart minecart, String str) {
            return str.equals("D") || str.toLowerCase().contains("default");
        }
    },
    Empty {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.isStandardMinecart() && minecart.getPassenger() == null && str.toLowerCase().contains("empty");
        }

    },
    Player {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.hasPlayerPassenger() && str.toLowerCase().contains("player");
        }

    },
    Mob {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.getPassenger() != null && !minecart.hasPlayerPassenger() && str.toLowerCase().contains("mob");
        }
    },
    Entity {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.getPassenger() != null && str.toLowerCase().contains("ent:") && str.toLowerCase().contains(minecart.getPassenger().getType().toString());
        }
    },
    StationCommand {
        public boolean result(MMMinecart minecart, String str) {
            return StationAction.MatchStationName(minecart, str);
        }
    },
    PlayerName {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.hasPlayerPassenger() && str.equalsIgnoreCase(minecart.getPlayerPassenger().getName());
        }
    },
    ContainsItem {
        public boolean result(MMMinecart minecart, String str) {
            if (minecart.hasPlayerPassenger() && minecart.getPlayerPassenger().getItemInHand() != null) {
                Item itemInHand = Item.getItem(minecart.getPlayerPassenger().getItemInHand().getTypeId(), minecart.getPlayerPassenger().getItemInHand().getDurability());
                AbstractItem[] signData = ItemUtils.getItemStringToMaterial(str);
                for (AbstractItem item : signData) {
                    if (item != null && item.equals(itemInHand)) {
                        return true;
                    }
                }
            } else if (minecart.isStorageMinecart()) {
                AbstractItem[] signData = ItemUtils.getItemStringToMaterial(str);
                for (AbstractItem item : signData) {
                    if (item != null && (((MMStorageCart) minecart).amount(item.type()) > (item.isInfinite() ? 0 : item.getAmount()))) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    Cargo {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.isStorageMinecart() && str.toLowerCase().contains("cargo") && ((MMStorageCart) minecart).isEmpty();
        }
    },
    Storage {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.isStorageMinecart() && str.toLowerCase().contains("storage");
        }
    },
    Powered {
        public boolean result(MMMinecart minecart, String str) {
            return minecart.isPoweredMinecart() && str.toLowerCase().contains("powered");
        }
    },
    West {
        public boolean result(MMMinecart minecart, String str) {
            CompassDirection dir = com.afforess.minecartmania.config.Settings.StationsUseOldDirections ? StationAction.convertToOldDirections(minecart.getDirection()) : minecart.getDirection();
            return (str.equals("W") || str.toLowerCase().contains("west")) && !str.contains("-") && dir == CompassDirection.WEST;
        }
    },
    East {
        public boolean result(MMMinecart minecart, String str) {
            CompassDirection dir = com.afforess.minecartmania.config.Settings.StationsUseOldDirections ? StationAction.convertToOldDirections(minecart.getDirection()) : minecart.getDirection();
            return (str.equals("E") || str.toLowerCase().contains("east")) && !str.contains("-") && dir == CompassDirection.EAST;
        }
    },
    North {
        public boolean result(MMMinecart minecart, String str) {
            CompassDirection dir = com.afforess.minecartmania.config.Settings.StationsUseOldDirections ? StationAction.convertToOldDirections(minecart.getDirection()) : minecart.getDirection();

            return (str.equals("N") || str.toLowerCase().contains("north")) && !str.contains("-") && dir == CompassDirection.NORTH;
        }
    },
    South {
        public boolean result(MMMinecart minecart, String str) {
            CompassDirection dir = com.afforess.minecartmania.config.Settings.StationsUseOldDirections ? StationAction.convertToOldDirections(minecart.getDirection()) : minecart.getDirection();
            return (str.equals("S") || str.toLowerCase().contains("south")) && !str.contains("-") && dir == CompassDirection.SOUTH;
        }
    },
    Redstone {
        public boolean result(MMMinecart minecart, String str) {
            return str.toLowerCase().contains("redstone") && (minecart.isPoweredBeneath() ||
                    MinecartManiaWorld.isBlockIndirectlyPowered(minecart.getWorld(), minecart.getX(), minecart.getY() - 2, minecart.getZ()));
        }
    }

}
