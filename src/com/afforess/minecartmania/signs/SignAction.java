package com.afforess.minecartmania.signs;

import org.bukkit.Location;

import com.afforess.minecartmania.MinecartManiaMinecart;

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
	public abstract boolean execute(MinecartManiaMinecart minecart);

	public boolean execute(MinecartManiaMinecart minecart, Location loc){
		this.loc = loc;
		return execute(minecart);
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
