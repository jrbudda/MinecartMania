package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MinecartInfoCommand extends MinecartManiaCommand {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MinecartManiaWorld.pruneMinecarts();
        ArrayList<MMMinecart> minecarts = MinecartManiaWorld.getMinecartManiaMinecartList();
        int total = minecarts.size();
        int empty, passenger, powered, storage, moving, unmoving;
        empty = passenger = powered = storage = moving = unmoving = 0;
        HashMap<String, Integer> owners = new HashMap<String, Integer>();
        for (MMMinecart minecart : minecarts) {
            if (minecart.isStandardMinecart()) {
                if (minecart.hasPlayerPassenger()) {
                    passenger++;
                } else {
                    empty++;
                }
            } else if (minecart.isPoweredMinecart()) {
                powered++;
            } else if (minecart.isStorageMinecart()) {
                storage++;
            }
            if (minecart.isMoving()) {
                moving++;
            } else {
                unmoving++;
            }
            if (minecart.getOwner() instanceof Player) {
                String name = ((Player) minecart.getOwner()).getName();
                if (owners.containsKey(name)) {
                    owners.put(name, owners.get(name) + 1);
                } else {
                    owners.put(name, 1);
                }
            }
        }

        String most = null;
        int mostCarts = 0;
        Iterator<Entry<String, Integer>> i = owners.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, Integer> e = i.next();
            if (most == null || e.getValue() > mostCarts) {
                most = e.getKey();
                mostCarts = e.getValue();
            }
        }

        sender.sendMessage(Settings.getLocal("AdminControlsMMHeader"));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoTotalMinecarts", total));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoEmptyMinecarts", empty));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoOccupiedMinecarts", passenger));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoPoweredMinecarts", powered));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoStorageMinecarts", storage));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoMovingMinecarts", moving));
        sender.sendMessage(Settings.getLocal("AdminControlsInfoStalledMinecarts", unmoving));
        if (most != null) {
            sender.sendMessage(Settings.getLocal("AdminControlsInfoMostOwnedMinecarts", most, owners.get(most)));
        }

        return true;
    }

    public boolean isPlayerOnly() {
        return false;
    }

    public CommandType getCommand() {
        return CommandType.Info;
    }

}
