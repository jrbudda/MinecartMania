package com.afforess.minecartmania;

import com.afforess.minecartmania.commands.Command;
import com.afforess.minecartmania.commands.CommandType;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaPlayer;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.entity.MinecartOwner;
import com.afforess.minecartmania.listeners.*;
import com.afforess.minecartmania.minecarts.MMDataTable;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.signs.sensors.SensorDataTable;
import com.afforess.minecartmania.signs.sensors.SensorManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinecartMania extends JavaPlugin {

    private static final int DATABASE_VERSION = 4;
    public static PermissionManager permissions;
    public static Plugin instance;
    public static File file;

    public static Command getMinecartManiaCommand(String command) {
        for (CommandType c : CommandType.values()) {
            if (c.toString().equalsIgnoreCase(command)) {
                return c.getCommand();
            }
        }
        return null;
    }

    public static MinecartMania getInstance() {
        return (MinecartMania) instance;
    }

    public static PluginDescriptionFile getPluginDescription() {
        return instance.getDescription();
    }

    public static File getPluginFile() {
        return file;
    }

    public static void callEvent(Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void onLoad() {
        setNaggable(false);
    }

    public void onEnable() {
        instance = this;
        file = this.getFile();

        writeItemsFile();

        permissions = new PermissionManager(getServer());

        getServer().getPluginManager().registerEvents(new CoreListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new MinecartTimer(), this);

        //TODO - don't use these anymore
        getServer().getPluginManager().registerEvents(new SignsActionListener(), this);
        getServer().getPluginManager().registerEvents(new StationsActionListener(), this);
        getServer().getPluginManager().registerEvents(new FarmingActionListener(), this);
        getServer().getPluginManager().registerEvents(new ChestActionListener(), this);

        reloadMyConfig();

        //database setup
        File ebeans = new File(new File(this.getDataFolder().getParent()).getParent(), "ebean.properties");
        if (!ebeans.exists()) {
            try {
                ebeans.createNewFile();
                PrintWriter pw = new PrintWriter(ebeans);
                pw.append("# General logging level: (none, explicit, all)");
                pw.append('\n');
                pw.append("ebean.logging=none");
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        setupDatabase();

        SensorManager.loadsensors();

        if (Settings.PreserveMinecartsonRiderLogout) LoadCartsForRiders();

        Logger.info(this.getDescription().getName() + " version " + this.getDescription().getVersion() + " is enabled!");
    }

    private void LoadCartsForRiders() {
        //this will only be used on a /reload, since otherwise no players are online.

        List<MMDataTable> entries = MMDataTable.getAlltCarts();
        if (entries != null) {
            for (MMDataTable entry : entries) {
                if (getServer().getPlayer(entry.owner) != null) {
                    Player p = getServer().getPlayer(entry.owner);
                    if (p.isOnline()) {
                        MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(p);
                        PlayerListener.spawnCartForRider(player, entry);
                        MMDataTable.delete(entry);
                    }
                }
            }
        }
    }

    private void SaveCartsWithRiders() {
        MinecartManiaWorld.pruneMinecarts();
        for (MMMinecart minecart : MinecartManiaWorld.getMinecartManiaMinecartList()) {
            MMDataTable data = null;
            if (minecart.hasPlayerPassenger()) {
                Logger.debug("Saving minecart for " + minecart.getPlayerPassenger().getName());
                data = new MMDataTable(minecart, minecart.getPlayerPassenger().getName());
                try {
                    MMDataTable.save(data);
                    minecart.killNoReturn();
                } catch (Exception e) {
                    Logger.severe("Failed to save minecart!");
                    e.printStackTrace();
                    Logger.logCore(e.getMessage(), false);
                }
            } else {
            }
        }
    }

    public void onDisable() {
        if (Settings.PreserveMinecartsonRiderLogout) SaveCartsWithRiders();
        getServer().getScheduler().cancelTasks(this);
        Logger.info(this.getDescription().getName() + " version " + this.getDescription().getVersion() + " is disabled!");
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        String commandPrefix;
        if (commandLabel.equals("mm")) {
            if (args == null || args.length == 0) {
                new com.afforess.minecartmania.commands.HelpCommand().onCommand(sender, cmd, commandLabel, args);
                return false;
            }
            commandPrefix = args[0];

            if (args.length > 1) {
                args = Arrays.copyOfRange(args, 1, args.length);
            } else {
                String[] temp = {};
                args = temp;
            }

        } else {
            commandPrefix = commandLabel;
        }
        commandPrefix = commandPrefix.toLowerCase();

        Command command = getMinecartManiaCommand(commandPrefix);
        if (command == null) {
            command = new com.afforess.minecartmania.commands.HelpCommand();
        }

        if (command.canExecuteCommand(sender)) {
            command.onCommand(sender, cmd, commandPrefix, args);
        } else {
            sender.sendMessage(Settings.getLocal("LackPermissionForCommand"));
        }


        return true;
    }

    private void writeItemsFile() {
        try {
            File items = new File("plugins" + File.separator + "MinecartMania" + File.separator + "items.txt");
            PrintWriter pw = new PrintWriter(items);
            pw.append("This file is a list of all the data values, and matching item names for Minecart Mania. \nThis list is never used, and changes made to this file will be ignored");
            pw.append("\n");
            pw.append("\n");
            pw.append("Items:");
            pw.append("\n");
            for (Item item : Item.values()) {
                String name = "Item Name: " + item.toString();
                pw.append(name);
                String id = "";
                for (int i = name.length() - 1; i < 40; i++) {
                    id += " ";
                }
                pw.append(id);
                id = "Item Id: " + String.valueOf(item.getId());
                pw.append(id);
                String data = "";
                for (int i = id.length() - 1; i < 15; i++) {
                    data += " ";
                }
                data += "Item Data: " + String.valueOf(item.getData());
                pw.append(data);
                pw.append("\n");
            }
            pw.close();
        } catch (Exception e) {
        }
    }

    private int getDatabaseVersion() {
        try {
            getDatabase().find(MinecartOwner.class).findRowCount();
        } catch (PersistenceException ex) {
            return 0;
        }
        try {
            getDatabase().find(MMDataTable.class).findRowCount();
        } catch (PersistenceException ex) {
            return 1;
        }
        try {
            getDatabase().find(MMDataTable.class).findList();
        } catch (PersistenceException ex) {
            return 2;
        }
        try {
            getDatabase().find(SensorDataTable.class).findList();
        } catch (PersistenceException ex) {
            return 3;
        }
        return DATABASE_VERSION;
    }

    protected void setupInitialDatabase() {
        try {
            getDatabase().find(MinecartOwner.class).findRowCount();
            getDatabase().find(MMDataTable.class).findRowCount();
            getDatabase().find(SensorDataTable.class).findRowCount();
        } catch (PersistenceException ex) {
            Logger.debug("Installing database");
            try {
                installDDL();
            } catch (Exception e) {
            }
        }
    }

    protected void setupDatabase() {
        try {

            int version = getDatabaseVersion();
            switch (version) {
                case 0:
                    setupInitialDatabase();
                    break;
                case 1:
                    upgradeDatabase(1);
                    break;
                case 2:
                    upgradeDatabase(2);
                    break;
                case 3:
                    upgradeDatabase(3);
                    break;
                case 4: /*up to date database*/
                    break;

            }
        } catch (Exception e) {

        }
    }

    private void upgradeDatabase(int current) {
        Logger.info(String.format("Upgrading database from version %d to version %d", current, DATABASE_VERSION));
        if (current != DATABASE_VERSION) {
            this.removeDDL();
            setupInitialDatabase();
        }
        /*
		 * Add additional versions here
		 */
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(MinecartOwner.class);
        list.add(MMDataTable.class);
        list.add(SensorDataTable.class);
        return list;
    }

    public void reloadMyConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();

        Logger.switchDebugMode(com.afforess.minecartmania.debug.DebugMode.debugModeFromString(getConfig().getString("LoggingMode", "Normal")));

        Settings.MaxAllowedRange = getConfig().getInt("MaxAllowedRange");

        Settings.DefaultDerailedFrictionPercent = getConfig().getInt("DerailedFrictionPercent", 100);
        Settings.DefaultPassengerFrictionPercent = getConfig().getInt("PassengerFrictionPercent", 100);
        Settings.DefaultEmptyFrictionPercent = getConfig().getInt("EmptyFrictionPercent", 100);
        Settings.KillPlayersOnTrackMinnimumSpeed = getConfig().getInt("KillPlayersOnTrackMinnimumSpeed", 90);

        Settings.KillMobsOnTrack = getConfig().getBoolean("KillMobsOnTrack", true);
        Settings.KillPlayersOnTrack = getConfig().getBoolean("KillPlayersOnTrack", false);

        Settings.DefaultSlowWhenEmpty = getConfig().getBoolean("SlowWhenEmpty", true);
        Settings.LoadChunksOnTrack = getConfig().getBoolean("KeepChunksLoaded", false);
        Settings.ReturnCartsToOwner = getConfig().getBoolean("ReturnCartsToOwner", false);
        Settings.PushBrakingAllowed = getConfig().getBoolean("PushBrakingAllowed", false);

        Settings.StationParingMode = getConfig().getInt("StationParsingMethod", 0);
        Settings.IntersectionPromptsMode = getConfig().getInt("IntersectionPromptsMethod", 0);

        Settings.defaultJumpHeight = getConfig().getInt("DefaultJumpHeight", 4);

        Settings.RemoveDeadCarts = getConfig().getBoolean("RemoveDeadMinecarts", false);

        Settings.DefaultMaxSpeedPercent = getConfig().getInt("MaxSpeedPercent", 200);
        Settings.MaxAllowedSpeedPercent = getConfig().getInt("MaxAllowedSpeedPercent", 500);
        Settings.MinecartCollisions = getConfig().getBoolean("MinecartCollisions", false);
        Settings.SlopeSpeedPercent = getConfig().getInt("SlopeSpeedPercent", 100);
        Settings.MinecartGravity = getConfig().getInt("GravityPercent", 100);

        Settings.PreserveMinecartsonRiderLogout = getConfig().getBoolean("PreserveMinecartOnLogout", true);

        Settings.DefaultMagneticRail = getConfig().getBoolean("MagneticRail", false);
        Settings.EmptyMinecartKillTimer = getConfig().getInt("EmptyMinecartKillTimer", 60);
        Settings.EmptyPoweredMinecartKillTimer = getConfig().getInt("EmptyPoweredMinecartKillTimer", 60);
        Settings.EmptyStorageMinecartKillTimer = getConfig().getInt("EmptyStorageMinecartKillTimer", 60);
        Settings.MaxPassengerPushPercent = getConfig().getInt("MaxPushSpeedPercent", 25);

        Settings.StationSignRange = getConfig().getInt("StationSignRange", 2);
        Settings.SpawnSignRange = getConfig().getInt("SpawnSignRange", 2);
        Settings.EjectSignRange = getConfig().getInt("EjectSignRange", 8);
        Settings.ActionSignRange = getConfig().getInt("ActionSignRange", 2);

        Settings.ItemCollectionRange = getConfig().getInt("DefaultItemCollectionRange", 4);
        Settings.ItemCollectionRangeY = getConfig().getInt("DefaultItemCollectionRangeY", 0);

        Settings.FarmRange = getConfig().getInt("DefaultFarmingRange", 4);
        Settings.FarmRangeY = getConfig().getInt("DefaultFarmingRangeY", 4);

        Settings.RememeberEjectionLocations = getConfig().getBoolean("RememberEjectionLocations", true);

        Settings.StationsUseOldDirections = getConfig().getBoolean("StationsUseOldDirections", false);

        Settings.RailAdjusterTool = Item.getNearestMatchingItem(getConfig().getString("RailAdjusterTool", "270"));


        com.afforess.minecartmania.config.Settings.StationCommandSaveAfterUse = true;

        ConfigurationSection blocks = getConfig().getConfigurationSection("ControlBlocks");
        if (blocks != null) {
            com.afforess.minecartmania.config.NewControlBlockList.controlBlocks.clear();
            for (String block : blocks.getKeys(false)) {

                Logger.info("Adding control block: " + block);

                Item item = Item.getItembyName(block);
                if (item == null) {
                    Logger.severe("Invalid Block Item: " + block);
                    continue;
                }

                List<String> signs = blocks.getStringList(block);

                List<SignAction> actions = new ArrayList<SignAction>();

                for (String sign : signs) {

                    Logger.debug("processing sign: " + sign);

                    String[] lines = sign.split("/");

                    List<SignAction> thiactions = com.afforess.minecartmania.signs.ActionList.getSignActionsforLines(lines);

                    for (SignAction a : thiactions) {
                        Logger.info("Adding " + a.getFriendlyName() + "(restone " + a.redstonestate + ")");
                        actions.add(a);
                    }
                }

                if (actions.size() > 0) {
                    com.afforess.minecartmania.config.NewControlBlockList.controlBlocks.put(item, new com.afforess.minecartmania.config.NewControlBlock(item, actions));
                } else Logger.severe("No valid actions found for " + item + "!");
            }
        }

        ConfigurationSection aliases = getConfig().getConfigurationSection("ItemAliases");

        if (aliases != null) {

            com.afforess.minecartmania.config.ItemAliasList.clear();

            for (String aliasname : aliases.getKeys(false)) {
                Logger.info("Adding alias: " + aliasname);


                List<String> itemstrings = aliases.getStringList(aliasname);
                List<Item> items = new ArrayList<Item>();

                for (String i : itemstrings) {

                    Item item = Item.getNearestMatchingItem(i);

                    if (i == null) {
                        Logger.severe("Invalid item '" + i + "'  in alias: " + aliasname);
                    } else items.add(item);

                }

                if (items.size() > 0) {
                    com.afforess.minecartmania.config.ItemAliasList.add(aliasname, items);
                }
            }
        }


    }


}
