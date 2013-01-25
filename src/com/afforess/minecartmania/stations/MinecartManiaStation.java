package com.afforess.minecartmania.stations;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.MinecartManiaConfigurationParser;
import com.afforess.minecartmania.listeners.StationsActionListener;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;


public class MinecartManiaStation extends JavaPlugin{
	public static MinecartManiaLogger log = MinecartManiaLogger.getInstance();
	public static Server server;
	public static PluginDescriptionFile description;
	public static StationsActionListener listener = new StationsActionListener();

	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public void onEnable() {
		server = this.getServer();
		description = this.getDescription();
		MinecartManiaConfigurationParser.read(description.getName().replace("Reborn", "") + "Configuration.xml", MinecartMania.getDataDirectoryRelativePath(), new StationSettingParser());
		getServer().getPluginManager().registerEvents(listener, this);
		log.info( description.getName() + " version " + description.getVersion() + " is enabled!" );
	}
}
