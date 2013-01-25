package com.afforess.minecartmania.oldplugins;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.ChestControlSettingParser;
import com.afforess.minecartmania.config.MinecartManiaConfigurationParser;
import com.afforess.minecartmania.listeners.ChestActionListener;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;

public class MinecartManiaChestControl extends JavaPlugin {
	public static MinecartManiaLogger log = MinecartManiaLogger.getInstance();
	public static Server server;
	public static PluginDescriptionFile description;
	public static ChestActionListener listener = new ChestActionListener();

	public void onEnable(){
		server = this.getServer();
		description = this.getDescription();
	
		getServer().getPluginManager().registerEvents(listener, this);
		log.info( description.getName() + " version " + description.getVersion() + " is enabled!" );
	}
	
	public void onDisable(){
		
	}
}
