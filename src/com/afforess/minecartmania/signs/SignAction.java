package com.afforess.minecartmania.signs;

import org.bukkit.Location;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.RedstoneState;

/**
 * An action specific to a sign
 * @author Afforess
 */
public abstract class SignAction{

	public Location loc  = null;

	/**
	 * Executes the action 
	 * @param minecart used in executing this action
	 * @return true if an action was exectued
	 */
	protected abstract boolean execute(MMMinecart minecart);

	public RedstoneState redstonestate = RedstoneState.NoEffect; 

	
	public boolean executeAsBlock(MMMinecart minecart, Location loc){
		this.loc = loc;
    	return executeDebug(minecart);	
	}
	
	public boolean executeAsSign(MMMinecart minecart){
    	return executeDebug(minecart);	
	}

	private boolean executeDebug(MMMinecart minecart){
		if(this.execute(minecart)){
			com.afforess.minecartmania.debug.Logger.debug("Executed %s",getFriendlyName());
			return true;
		}
		else com.afforess.minecartmania.debug.Logger.debug("Failed   %s",getFriendlyName());
		return false;
	}

	
	protected boolean executeAcceptsNull = false;

	public boolean getexecuteAcceptsNull() {
		return executeAcceptsNull;
	}

	/**
	 * Whether this action can be exectuted on a separate thread
	 * @return true if this can be executed on a separate thread
	 */
	public abstract boolean async();


     /**
	 * Process this sign and set up any variables
	 * @param sign to process
	 * @return true if the sign is valid
	 */
	public abstract boolean process(String[] lines);
	//TODO: make process static and return a new instance if sucessful
	

	/**
	 * Get's the name of this action
	 * @return name
	 */
	public abstract String getPermissionName();

	/**
	 * Get's the human-readable name of this action
	 * @return name
	 */
	public abstract String getFriendlyName();
}
