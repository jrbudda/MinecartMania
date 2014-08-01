package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ClearAllCartsCommand extends MinecartManiaCommand implements ClearMinecartCommand {

    public boolean isPlayerOnly() {
        return false;
    }

    public CommandType getCommand() {
        return CommandType.ClearAllCarts;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int distance = -1;
        boolean delete = false;
        if (args.length > 0) {
            for (String arg : args) {
                try {
                    distance = Integer.parseInt(StringUtils.getNumber(args[0]));
                } catch (Exception e) {
                    delete = delete || arg.contains("-d");
                }
            }
        }

        Vector location = null;
        if (sender instanceof Player) {
            location = ((Player) sender).getLocation().toVector();
        } else {
            distance = -1;
        }
        MinecartManiaWorld.pruneMinecarts();

        int count = 0;
        ArrayList<MMMinecart> minecartList = MinecartManiaWorld.getMinecartManiaMinecartList();
        for (MMMinecart minecart : minecartList) {
            if (!minecart.isDead() && !minecart.isDead()) {
                if (distance < 0 || (minecart.getLocation().toVector().distance(location) < distance)) {
                    if (shouldRemoveMinecart(minecart)) {
                        count++;
                        if (delete) minecart.killNoReturn();
                        else minecart.killOptionalReturn();
                    }
                }
            }
        }
        sender.sendMessage(Settings.getLocal("AdminControlsMinecartsRemoved", count));
        return true;
    }

    public boolean shouldRemoveMinecart(MMMinecart minecart) {
        return true;
    }

}
