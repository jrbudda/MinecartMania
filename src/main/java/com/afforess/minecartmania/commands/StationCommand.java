package com.afforess.minecartmania.commands;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaPlayer;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StationCommand extends MinecartManiaCommand {

    public boolean isPlayerOnly() {
        return true;
    }

    public CommandType getCommand() {
        return CommandType.St;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        MinecartManiaPlayer mmp = MinecartManiaWorld.getMinecartManiaPlayer(player);
        String str = "NONE";
        if (args.length < 1) {
            if (mmp.getLastStation() != "") str = mmp.getLastStation();
            sender.sendMessage(Settings.getLocal("StationHelpString", str));
            return false;
        }


        String station = args[0];
        mmp.setLastStation(station);
        if (args.length > 1) {
            if (args[1].contains("s")) {
                mmp.setDataValue("Reset Station Data", Boolean.TRUE);
            }
        } else {
            mmp.setDataValue("Reset Station Data", null);
        }
        mmp.sendMessage(Settings.getLocal("AdminControlsStation", station));
        return true;
    }


}
