package com.afforess.minecartmania.minecarts;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_7_R4.EntityMinecartAbstract;
import net.minecraft.server.v1_7_R4.EntityMinecartRideable;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftMinecart;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.NewControlBlockList;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.entity.MinecartManiaInventory;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.entity.MinecartOwner;
import com.afforess.minecartmania.events.MinecartManiaMinecartCreatedEvent;
import com.afforess.minecartmania.events.MinecartManiaMinecartDestroyedEvent;
import com.afforess.minecartmania.events.MinecartTimeEvent;
import com.afforess.minecartmania.utils.BlockUtils;
import com.afforess.minecartmania.utils.DirectionUtils;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.MinecartUtils;
import com.afforess.minecartmania.utils.SignUtils;
import com.afforess.minecartmania.utils.ThreadSafe;
import com.avaje.ebean.EbeanServer;


public class MMMinecart {
	public static final double MAXIMUM_MOMENTUM = 1E150D;
	protected Calendar cal;
	private long cooldown;
	public boolean createdLastTick = true;
	protected ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<String,Object>();
	//protected ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<String,Object>();
	protected volatile boolean dead = false;
	protected Minecart minecart;
	protected MinecartOwner owner = null;
	protected volatile CompassDirection previousFacingDir = DirectionUtils.CompassDirection.NO_DIRECTION;
	protected volatile Vector previousLocation;


	protected String destination = "";

	public String getDestination() {

		if(destination != "") return destination;

		if (hasPlayerPassenger() && MinecartManiaWorld.getMinecartManiaPlayer(getPlayerPassenger()).getLastStation() !=""){
			return MinecartManiaWorld.getMinecartManiaPlayer(getPlayerPassenger()).getLastStation();
		}

		return "";
	}

	public void setDestination(String destination) {
		if (hasPlayerPassenger()){
			MinecartManiaWorld.getMinecartManiaPlayer(getPlayerPassenger()).setLastStation(destination);
		}
		this.destination = destination;
	}

	protected volatile boolean wasMovingLastTick;


	protected boolean locked = false;

	protected int oldid = 0;

	public int oldID(){
		return oldid;
	}

	//	public MMMinecart(Minecart cart) {
	//		oldid = cart.getEntityId();
	//		minecart = replaceCart(cart); 
	//		initialize();
	//		findOwner();
	//	}


	public MMMinecart(Minecart cart, boolean owned, String owner) {

		oldid = cart.getEntityId();
		minecart = replaceCart(cart);

		setOwner(owner);		

		//clear previous owners
		/*List<MinecartOwner> list = MinecartManiaCore.instance.getDatabase().find(MinecartOwner.class).where().idEq(minecart.getEntityId()).findList();
		for (MinecartOwner temp : list) {
			MinecartManiaCore.instance.getDatabase().delete(temp);
		}
		//save new owner
		MinecartManiaCore.instance.getDatabase().save(this.owner);*/

		initialize();
	}

	public boolean Ejecting = false; 
	public boolean eject() {
		setLocked(false);
		return minecart.eject();
	}

	/**
	 ** Attempts to find the player that spawned this minecart.
	 */
	public void findOwner() {
		final EbeanServer db = MinecartMania.getInstance().getDatabase();
		try {
			MinecartOwner temp = db.find(MinecartOwner.class).where().idEq(minecart.getEntityId()).findUnique();
			if (temp != null) {			
				owner = temp;		
				return;
			}
		}
		catch (Exception e) {
			//clear duplicates
			Logger.debug("Clearing Duplicate Minecart Id's : " + minecart.getEntityId());
			try {
				List<MinecartOwner> list = db.find(MinecartOwner.class).where().idEq(minecart.getEntityId()).findList();
				for (MinecartOwner temp : list) {
					db.delete(temp);
				}
			}
			catch (NullPointerException npe) {
				Logger.info("Failed to clear duplicate minecart entities!");
			}
			catch (Exception ex){
				Logger.info("db error " + ex.getMessage());
			}
		}
		double closest = Double.MAX_VALUE;

		Player closestPlayer = null;
		for (LivingEntity le : minecart.getWorld().getLivingEntities()) {
			if (le instanceof Player) {
				double distance = le.getLocation().toVector().distance(minecart.getLocation().toVector());
				if (distance < closest) {
					closestPlayer = (Player)le;
					closest = distance;
				}
			}
		}
		
		if (closestPlayer != null) 	setOwner(closestPlayer.getName());
		else setOwner(null);
		
		if (owner.hasOwner()) {
			db.save(this.owner);
		}
		
	}

	public boolean isLocked(){
		return locked;
	}

	public void setLocked(boolean locked){
		this.locked = locked;  
	}

	public HashSet<Block> getAdjacentBlocks(int range) {
		return BlockUtils.getAdjacentBlocks(getLocation(), range);
	}

	//	public boolean doCatcherBlock() {
	//		if (ControlBlockList.isCatcherBlock(getItemBeneath())){
	//			if (ControlBlockList.isValidCatcherBlock(this)) {
	//				MinecartCaughtEvent mce = new MinecartCaughtEvent(this);
	//				MinecartMania.callEvent(mce);
	//				if (!mce.isActionTaken()) {
	//					setFrozen(true);
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}
	//
	//

	//	public boolean doEjectorBlock() {
	//		if (ControlBlockList.isValidEjectorBlock(this)) {
	//			if (minecart.getPassenger() != null) {
	//				double ejectY = ControlBlockList.getControlBlock(getItemBeneath()).getEjectY();
	//				MinecartPassengerEjectEvent mpee = new MinecartPassengerEjectEvent(this, minecart.getPassenger());
	//				MinecartMania.callEvent(mpee);
	//				if (!mpee.isCancelled()) {
	//					Entity passenger = minecart.getPassenger();
	//					if (minecart.eject()) {
	//						Location dest = passenger.getLocation();
	//						dest.setY(dest.getY() + ejectY);
	//						passenger.teleport(dest);
	//						return true;
	//					}
	//					return false;
	//				}
	//			}
	//		}
	//		return false;
	//	}

	//	public boolean doElevatorBlock() {
	//		if (ControlBlockList.isValidElevatorBlock(this)) {
	//			//Get where we are
	//			Block elevatorBlock = getBlockBeneath();
	//			int y = elevatorBlock.getY();
	//			//Find the closest elevator block. yOffset of 1 above us is our own track, so we can't look there, so don't.
	//			//If we start yOffset = 2, we will miss the possible track directly below us. It won't fit a person, but will fit a storage cart.
	//			for (int yOffset = 1; yOffset < 256; yOffset++) {
	//				if (y + yOffset < 256 && yOffset > 1) {
	//					//See if we have a valid destination
	//					if (MinecartUtils.isTrack(elevatorBlock.getRelative(0, yOffset, 0))
	//							&& ControlBlockList.isElevatorBlock(Item.getItem(elevatorBlock.getRelative(0, yOffset-1, 0)))) {
	//						//do the teleport and return
	//						MinecartElevatorEvent event = new MinecartElevatorEvent(this, elevatorBlock.getRelative(0, yOffset, 0).getLocation());
	//						MinecartMania.callEvent(event);
	//						if (!event.isCancelled()) {
	//							return minecart.teleport(event.getTeleportLocation());
	//						}
	//					}					
	//				} 
	//				if (y - yOffset > 0) {
	//					//See if we have a valid destination
	//					if (MinecartUtils.isTrack(elevatorBlock.getRelative(0, -yOffset, 0))
	//							&& ControlBlockList.isElevatorBlock(Item.getItem(elevatorBlock.getRelative(0, -yOffset-1, 0)))) {
	//						//do the teleport and return
	//						MinecartElevatorEvent event = new MinecartElevatorEvent(this, elevatorBlock.getRelative(0, -yOffset, 0).getLocation());
	//						MinecartMania.callEvent(event);
	//						if (!event.isCancelled()) {
	//							return minecart.teleport(event.getTeleportLocation());
	//						}
	//					}										
	//				}
	//			}
	//		}
	//		return false;
	//	}


	//	public boolean doKillBlock() {
	//		if (ControlBlockList.isValidKillMinecartBlock(this)) {
	//			kill(getOwner() instanceof MinecartManiaChest);
	//			return true;
	//		}
	//		return false;
	//	}
	//
	//	public void doLauncherBlock() {
	//		if (ControlBlockList.getLaunchSpeed(getItemBeneath()) != 0.0D){
	//			if (ControlBlockList.isValidLauncherBlock(this)) {
	//				if (!isMoving()) {
	//					launchCart(ControlBlockList.getLaunchSpeed(getItemBeneath()));
	//				}
	//			}
	//		}
	//	}


	public MMMinecart getAdjacentMinecartFromDirection(DirectionUtils.CompassDirection direction) {

		if (direction == DirectionUtils.CompassDirection.NORTH) return MinecartManiaWorld.getMinecartManiaMinecartAt(getX(), getY(), getZ() - 1);
		if (direction == DirectionUtils.CompassDirection.EAST) return MinecartManiaWorld.getMinecartManiaMinecartAt(getX() + 1, getY(), getZ());
		if (direction == DirectionUtils.CompassDirection.SOUTH) return MinecartManiaWorld.getMinecartManiaMinecartAt(getX(), getY(), getZ() + 1);
		if (direction == DirectionUtils.CompassDirection.WEST) return MinecartManiaWorld.getMinecartManiaMinecartAt(getX() - 1, getY(), getZ());

		return null;
	}


	//	public boolean doPlatformBlock() {
	//		if (ControlBlockList.isValidPlatformBlock(this) && isStandardMinecart()) {
	//			if (minecart.getPassenger() == null) {
	//				List<LivingEntity> list = minecart.getWorld().getLivingEntities();
	//				double range = ControlBlockList.getControlBlock(getItemBeneath()).getPlatformRange();
	//				range *= range;
	//				LivingEntity closest = null;
	//				double distance = -1;
	//				for (LivingEntity le : list) {
	//					if (le.getLocation().toVector().distanceSquared(minecart.getLocation().toVector()) < distance || closest == null) {
	//						closest = le;
	//						distance = le.getLocation().toVector().distanceSquared(minecart.getLocation().toVector());
	//					}
	//				}
	//				if (closest != null && closest.getLocation().toVector().distanceSquared(minecart.getLocation().toVector()) < range) {
	//					//Let the world know about this
	//					VehicleEnterEvent vee = new VehicleEnterEvent(minecart, closest);
	//					MinecartMania.callEvent(vee);
	//					if (!vee.isCancelled()) {
	//						minecart.setPassenger(closest);
	//						return true;
	//					}
	//				}
	//			}
	//		}
	//		return false;
	//	}

	public Block getBlockBeneath() {
		if (NewControlBlockList.getControlBlock(Item.getItem(getLocation().getBlock())) != null) {
			//control rail
			return getLocation().getBlock();
		}
		else {
			//block under rail;
			return  getLocation().getBlock().getRelative(org.bukkit.block.BlockFace.DOWN);
		}
	}

	//	public boolean doSpeedMultiplierBlock() {
	//		double multiplier = ControlBlockList.getSpeedMultiplier(this);
	//		MinecartManiaLogger.debug( "dospeed" +  multiplier);
	//		if (multiplier != 1.0D) {
	//			MinecartSpeedMultiplierEvent msme = new MinecartSpeedMultiplierEvent(this, multiplier);
	//			MinecartMania.callEvent(msme);
	//			multiplyMotion(msme.getSpeedMultiplier());
	//			return msme.isCancelled();
	//		}
	//
	//		//		why do this twice??
	//
	//		//		//check for powered rails
	//		//		multiplier = ControlBlockList.getSpeedMultiplier(this);
	//		//		if (multiplier != 1.0D) {
	//		//			MinecartSpeedMultiplierEvent msme = new MinecartSpeedMultiplierEvent(this, multiplier);
	//		//			MinecartManiaCore.callEvent(msme);
	//		//			multiplyMotion(msme.getSpeedMultiplier());
	//		//	    	return msme.isCancelled();
	//		//		}
	//
	//		return false;
	//	}

	public int getBlockIdBeneath() {
		return getBlockBeneath().getTypeId();
	}

	public HashSet<Block> getBlocksBeneath(int range) {
		return BlockUtils.getBlocksBeneath(getLocation(), range);
	}

	public Block getBlockTypeAhead() {
		return DirectionUtils.getBlockTypeAhead(minecart.getWorld(), getDirectionOfMotion(), getX(), getY(), getZ());
	}

	public Block getBlockTypeBehind() {
		return DirectionUtils.getBlockTypeAhead(minecart.getWorld(), DirectionUtils.getOppositeDirection(getDirectionOfMotion()), getX(), getY(), getZ());
	}

	public Minecart getBukkitEntity(){
		return this.minecart;
	}

	/**
	 * Get's the chunk this minecart is at
	 * @return chunk
	 */
	public final Chunk getChunkAt() {
		return getLocation().getBlock().getChunk();
	}

	/**
	 * Returns the value from the loaded data
	 * @param the string key the data value is associated with
	 */
	@ThreadSafe
	public final Object getDataValue(String key) {
		return data.get(key);
	}

	/**
	 * Attempts a "best guess" at the direction of the minecart. 
	 * If the minecart is moving, it will return the correct direction, but if it's stopped, it will use the value stored in memory.
	 * @return CompassDirection that the minecart is moving towards
	 */
	public CompassDirection getDirection() {
		if (isMoving()) {
			return getDirectionOfMotion();
		}
		return getPreviousDirectionOfMotion();
	}

	/**
	 * Get's the direction that this minecart is moving, or NO_DIRECTION if it is not moving
	 * @return direction
	 */
	public CompassDirection getDirectionOfMotion() {

		if (getMotionZ() < 0.0D) return CompassDirection.NORTH;
		if (getMotionX() > 0.0D) return CompassDirection.EAST;
		if (getMotionZ() > 0.0D) return CompassDirection.SOUTH;
		if (getMotionX() < 0.0D) return CompassDirection.WEST;

		return CompassDirection.NO_DIRECTION;
	}

	public int getEntityId() {
		return minecart.getEntityId();
	}

	public IMMEntity getHandle(){
		return  (IMMEntity) ((CraftMinecart) minecart).getHandle();
	}

	public Item getItemBeneath() {
		return Item.getItem(getBlockBeneath());
	}

	/**
	 * Get's the location of this minecart in the world
	 * @return location
	 */
	public final Location getLocation() {
		return minecart.getLocation();
	}

	public MMMinecart getMinecartAhead() {
		return getAdjacentMinecartFromDirection(getDirection());
	}

	public MMMinecart getMinecartBehind() {
		return getAdjacentMinecartFromDirection(DirectionUtils.getOppositeDirection(getDirection()));
	}

	/**
	 * Returns the motion of the cart
	 * @return motion
	 */
	public Vector getMotion() {
		return minecart.getVelocity();
	}

	/**
	 * Get's the motion in the X direction
	 * @return X motion
	 */
	public double getMotionX() {
		return getHandle().getEntity().motX;
	}

	/**
	 * Get's the motion in the Y direction
	 * @return Y motion
	 */
	public double getMotionY() {
		return getHandle().getEntity().motY;
	}

	/**
	 * Get's the motion in the Z direction
	 * @return Z motion
	 */
	public double getMotionZ() {
		return getHandle().getEntity().motZ;
	}

	/**
	 * Get's the block this minecart is current occupying
	 * @return block
	 */
	public final Block getOccupiedBlock() {
		return getLocation().getBlock();
	}

	/**
	 * attempts to find and return the owner of this object, a player or a minecart mania chest.
	 * It will fail if the owner is offline, wasn't found, or the chest was destroyed.
	 * @return Player or Minecart Mania Chest that spawned this minecart.
	 */
	public Object getOwner() {
		if (owner == null) return null;
		return owner.getRealOwner();
	}

	public void setOwner(String newOwner) {
		
		if (newOwner != null) {
			owner = new MinecartOwner(newOwner);
		}
		else {
			owner = new MinecartOwner();
		}
		
		owner.setId(minecart.getEntityId());
		owner.setWorld(minecart.getWorld().getName());


	}

	public ArrayList<Block> getParallelBlocks() {
		ArrayList<Block> blocks = new ArrayList<Block>(4);
		Block occupied = getOccupiedBlock();
		blocks.add(occupied.getRelative(-1, 0, 0));
		blocks.add(occupied.getRelative(1, 0, 0));
		blocks.add(occupied.getRelative(0, 0, -1));
		blocks.add(occupied.getRelative(0, 0, 1));
		return blocks;
	}

	public Entity getPassenger() {
		return minecart.getPassenger();
	}

	public Player getPlayerPassenger() {
		if (minecart.getPassenger() == null) {
			return null;
		}
		if (minecart.getPassenger() instanceof Player) {
			return (Player)minecart.getPassenger();
		}
		return null;
	}

	/**
	 * Get's the previous direction of motion for this minecart
	 * @return previous direction
	 */
	public CompassDirection getPreviousDirectionOfMotion() {
		return previousFacingDir;
	}

	@Deprecated
	public DirectionUtils.CompassDirection getPreviousFacingDir() {
		return previousFacingDir;
	}


	public HashSet<Block> getPreviousLocationAdjacentBlocks(int range) {
		return BlockUtils.getAdjacentBlocks(getPrevLocation(), range);
	}

	public HashSet<Block> getPreviousLocationBlocksBeneath(int range) {
		return BlockUtils.getBlocksBeneath(getPrevLocation(), range);
	}

	public ArrayList<Block> getPreviousLocationParallelBlocks() {
		ArrayList<Block> blocks = new ArrayList<Block>(4);
		Block occupied = getPrevLocation().getBlock();
		blocks.add(occupied.getRelative(-1, 0, 0));
		blocks.add(occupied.getRelative(1, 0, 0));
		blocks.add(occupied.getRelative(0, 0, -1));
		blocks.add(occupied.getRelative(0, 0, 1));
		return blocks;
	}

	/**
	 * Get's the previous position (from the last tick) of the minecart in the world
	 * @return previous position
	 */
	@ThreadSafe
	public final Vector getPreviousPosition() {
		return previousLocation.clone(); //cloned to avoid others messing with the mutable reference
	}

	/**
	 * Get's the previous location (from the last tick) of the minecart in the world
	 * @return previous location
	 */
	public final Location getPrevLocation() {
		//using minecart.getWorld is safe for estimating the previous location because teleporting minecarts between worlds does not work
		return previousLocation.toLocation(minecart.getWorld());
	}


	public Item getType() {
		if (minecart instanceof org.bukkit.entity.minecart.RideableMinecart) 	return Item.MINECART;
		else 	if (minecart instanceof org.bukkit.entity.minecart.HopperMinecart) 	return Item.MINECART_HOPPER;
		else 	if (minecart instanceof org.bukkit.entity.minecart.CommandMinecart) 	return Item.MINECART_COMMAND;
		else 	if (minecart instanceof org.bukkit.entity.minecart.ExplosiveMinecart) 	return Item.MINECART_TNT;
		else 	if (minecart instanceof org.bukkit.entity.minecart.StorageMinecart) 	return Item.STORAGE_MINECART;
		else 	if (minecart instanceof org.bukkit.entity.minecart.PoweredMinecart) 	return Item.POWERED_MINECART;
		else return Item.MINECART;
	}


	/**
	 * Get's the world this minecart is in
	 * @return world
	 */
	public final org.bukkit.World getWorld() {
		return minecart.getWorld();
	}

	/**
	 * Get's the X coordinate of this minecart
	 * @return X coordinate
	 */
	public final int getX(){
		return getLocation().getBlockX();
	}

	/**
	 * Get's the Y coordinate of this minecart
	 * @return Y coordinate
	 */
	public final int getY(){
		return getLocation().getBlockY();
	}

	/**
	 * Get's the Z coordinate of this minecart
	 * @return Z coordinate of this minecart
	 */
	public final int getZ(){
		return getLocation().getBlockZ();
	}

	public void handleControlBlocksAndSigns(){
		if(isOnControlBlock() && isOnRails()){
			//there isnt that better?

			org.bukkit.block.Block block = getBlockBeneath(); // this will return the rail block if its set as a control block.
			com.afforess.minecartmania.config.NewControlBlock cb = NewControlBlockList.getControlBlock(Item.getItem(block));

			cb.execute(this, block.getLocation());			


			//					minecart.doSpeedMultiplierBlock();
			//					minecart.doCatcherBlock();
			//					minecart.doPlatformBlock(); //platform must be after catcher block
			//					minecart.doElevatorBlock();
			//					minecart.doEjectorBlock();

		}

		if(isOnRails()){
			ArrayList<com.afforess.minecartmania.signs.MMSign> list = SignUtils.getAdjacentMMSignListforDirection(minecart.getLocation(), Settings.ActionSignRange, getDirection());
			for (com.afforess.minecartmania.signs.MMSign sign : list) {
				com.afforess.minecartmania.debug.Logger.debug("Processing sign " + sign.getLine(0));
				sign.executeActions(this);
			}	
		}
	}

	/**
	 * Checks to see if the minecart has moved positions from it's previous position
	 * @return true if the minecart has moved positions
	 */
	public boolean hasChangedPosition() {
		if (getPreviousPosition().getBlockX() != minecart.getLocation().getBlockX()) {
			return true;
		}
		if (getPreviousPosition().getBlockY() != minecart.getLocation().getBlockY()) {
			return true;
		}
		if (getPreviousPosition().getBlockZ() != minecart.getLocation().getBlockZ()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the minecart has a passenger on board
	 * @return
	 */
	public boolean hasPassenger() {
		return minecart.getPassenger() != null;	
	}

	/**
	 * Returns true if the minecart has a player passenger onboard
	 * @return true if player onboard
	 */
	public boolean hasPlayerPassenger() {
		return getPlayerPassenger() != null;
	}

	private void initialize() {
		cal = Calendar.getInstance();
		setWasMovingLastTick(isMoving());
		previousLocation = minecart.getLocation().toVector().clone();
		previousLocation.setY(previousLocation.getX() -1); //fool game into thinking we've already moved

		minecart.setSlowWhenEmpty(Settings.DefaultSlowWhenEmpty);

		minecart.setMaxSpeed(0.4D * Settings.DefaultMaxSpeedPercent /100 );

		getHandle().setDerailedFriction(Settings.DefaultDerailedFrictionPercent);
		getHandle().setEmptyFriction(Settings.DefaultEmptyFrictionPercent) ;
		getHandle().setSlopeSpeed(Settings.SlopeSpeedPercent);
		getHandle().setPassengerFriction(Settings.DefaultPassengerFrictionPercent);
		getHandle().setMagnetic(Settings.DefaultMagneticRail);
		getHandle().setCollisions(Settings.MinecartCollisions);
		getHandle().setMaxPushSpeed(Settings.MaxPassengerPushPercent);
		getHandle().setGravity(Settings.MinecartGravity);

		MinecartMania.callEvent(new MinecartManiaMinecartCreatedEvent(this));		
	}

	public boolean isApproaching(Location v) {
		if (!isMoving()) {
			return false;
		}
		CompassDirection direction = getDirection();

		if (direction == CompassDirection.WEST) {
			if (((minecart.getLocation().getX() - v.getX()) < 3.0D) && ((minecart.getLocation().getX() - v.getX()) > 0.0D))
				return Math.abs(minecart.getLocation().getZ() - v.getZ()) < 1.5D;
		}
		else if (direction == CompassDirection.EAST) {
			if (((minecart.getLocation().getX() - v.getX()) > -3.0D) && ((minecart.getLocation().getX() - v.getX()) < 0.0D))
				return Math.abs(minecart.getLocation().getZ() - v.getZ()) < 1.5D;
		}
		else if (direction == CompassDirection.NORTH) {
			if (((minecart.getLocation().getZ() - v.getZ()) < 3.0D) && ((minecart.getLocation().getZ() - v.getZ()) > 0.0D))
				return Math.abs(minecart.getLocation().getX() - v.getX()) < 1.5D;
		}
		else if (direction == CompassDirection.SOUTH) {
			if (((minecart.getLocation().getZ() - v.getZ()) > -3.0D) && ((minecart.getLocation().getZ() - v.getZ()) < 0.0D))
				return Math.abs(minecart.getLocation().getX() - v.getX()) < 1.5D;
		}


		return false;
	}

	/**
	 ** Determines whether or not the track the minecart is currently on is the center piece of a large track intersection. Returns true if it is an intersection.
	 **/
	public boolean isAtIntersection() {
		if (this.isOnRails()) {
			return MinecartUtils.isAtIntersection(minecart.getWorld(), getX(), getY(), getZ());
		}
		return false;
	}

	public boolean isCooledDown(){
		return System.currentTimeMillis() > cooldown;

	}

	/**
	 * Is true if the minecart is dead, and has yet to be removed by the garbage collector
	 * @return is dead
	 */
	@ThreadSafe
	public final boolean isDead() {
		if (minecart.isDead()) {
			dead = true;
		}
		return dead;
	}

	public boolean isFrozen(){
		return getHandle().getFrozen();
	}


	public boolean isGoingDownhill(){
		return getHandle().getDownhill();
	}

	public boolean isGoingUphill(){
		return getHandle().getUphill();
	}

	/**
	 * Returns true if the minecart is moving
	 * @return true if moving
	 */
	public boolean isMoving() {
		if(isFrozen()) return false;
		return getMotionX() != 0D || getMotionY() != 0D || getMotionZ() != 0D;
	}

	public boolean isMovingAway(Location l) {

		//West of us
		if ((l.getBlockX() - getX()) < 0) {
			if (getDirection().equals(CompassDirection.WEST))
				return true;
		}
		//East of us
		if ((l.getBlockX() - getX()) > 0) {
			if (getDirection().equals(CompassDirection.EAST))
				return true;
		}
		//North of us
		if ((l.getBlockZ() - getZ()) < 0) {
			if (getDirection().equals(CompassDirection.NORTH))
				return true;
		}
		//South of us
		if ((l.getBlockZ() + getZ()) > 0) {
			if (getDirection().equals(CompassDirection.SOUTH))
				return true;
		}

		return false;
	}

	public boolean isOnControlBlock(){
		return NewControlBlockList.isControlBlock(getItemBeneath());
	}



	public boolean isOnRails() {
		return getHandle().getOnRails();
	}

	public boolean isOnSlope(){
		return isGoingUphill()|| isGoingDownhill();
	}



	/**
	 * Attempts to determine if the given object is the owner if this minecart.
	 * Valid datatypes: Entity, Vector, Location, Chest, MinecartManiaChest
	 * @param obj to test
	 * @return true if obj represents the owner
	 */
	public boolean isOwner(Object obj) {
		Object owner = getOwner();
		if (owner == null) {
			return false;
		}
		if (owner instanceof Player && obj instanceof Entity) {
			return ((Player)owner).getEntityId() == ((Entity)obj).getEntityId();
		}
		if (owner instanceof MinecartManiaChest) {
			if (obj instanceof Vector) {
				return ((MinecartManiaChest) owner).getLocation().equals(((Vector)obj).toLocation(getWorld()));
			}
			if (obj instanceof Location) {
				return ((MinecartManiaChest) owner).getLocation().equals((Location)obj);
			}
			if (obj instanceof Chest) {
				return ((MinecartManiaChest) owner).getLocation().equals(((Chest)obj).getBlock().getLocation());
			}
			if (obj instanceof MinecartManiaChest) {
				return ((MinecartManiaChest) owner).getLocation().equals(((MinecartManiaChest)obj).getLocation());
			}
		}
		return false;
	}

	public boolean isPoweredBeneath() {
		if (MinecartManiaWorld.isBlockPowered(minecart.getWorld(), getX(), getY()-2, getZ()) || 
				MinecartManiaWorld.isBlockIndirectlyPowered(minecart.getWorld(), getX(), getY()-1, getZ()) || 
				MinecartManiaWorld.isBlockIndirectlyPowered(minecart.getWorld(), getX(), getY(), getZ())) {
			return true;
		}
		return false;
	}

	public boolean isPoweredMinecart() {
		return minecart instanceof PoweredMinecart;
	}

	public boolean isStandardMinecart() {
		return minecart instanceof org.bukkit.entity.minecart.RideableMinecart;
	}

	public boolean isStorageMinecart() {
		return minecart instanceof StorageMinecart;
	}

	private void kill(boolean returnToOwner, boolean dropContents) {
		if (!isDead()) {

			ArrayList<ItemStack> items = new ArrayList<ItemStack>();

			if((returnToOwner && getOwner() !=null) || !Settings.RemoveDeadCarts)	items.add(new ItemStack(getType().toMaterial(), 1));

			if (isStorageMinecart()) {
				for (ItemStack i : ((MMStorageCart)this).getContents()) {
					if (i != null && i.getTypeId() != 0) {
						items.add(i);
					}
				}
			}

			if (returnToOwner && getOwner() !=null ) {

				//give the items back inside too

				Object owner = getOwner();
				MinecartManiaInventory inventory = null;
				Player invOwner = null;		

				if (owner instanceof Player ) {
					inventory = MinecartManiaWorld.getMinecartManiaPlayer((Player)owner);
				}
				else if (owner instanceof MinecartManiaChest) {
					inventory = ((MinecartManiaChest)owner);
					String temp = ((MinecartManiaChest)owner).getOwner();
					if (temp != null) {
						invOwner = Bukkit.getServer().getPlayer(temp);
					}
				}	

				if (inventory != null) {
					Logger.debug("Returning cart to owner " + inventory.getBukkitInventory().getHolder().toString());
					for (ItemStack i : items) {
						if (!inventory.addItem(i, invOwner)) {
							minecart.getWorld().dropItemNaturally(minecart.getLocation(), i);
						}
					}
					items.clear();
				}
				else {
					Logger.debug("Could not find owner to return cart");
					if(com.afforess.minecartmania.config.Settings.RemoveDeadCarts) items.clear();
				}	
			}

			if(dropContents){
				for (ItemStack i : items) {
					minecart.getWorld().dropItemNaturally(minecart.getLocation(), i);
				}
			}

			//Fire destroyed event
			MinecartManiaMinecartDestroyedEvent mmmee = new MinecartManiaMinecartDestroyedEvent(this);
			MinecartMania.callEvent(mmmee);

			MinecartManiaWorld.LoadChunksAround(getLocation(),2);

			Logger.debug("Removing cart " + getEntityId());
			minecart.remove();
			MinecartManiaWorld.delMinecartManiaMinecart(this.getEntityId());
			dead = true;
		}
	}

	public void killNoReturn() {
		kill(false, false);
	}

	public void killOptionalReturn() {
		kill(Settings.ReturnCartsToOwner, true);
	}

	public void launchCart(boolean reverseSearch) {
		launchCart(0.6D, reverseSearch);
	}

	private void launchCart(double speed, boolean reverseSearch) {
		//TODO: ummm.... bad bad bad place for this, and likely unnecessary, and wrong.
		//		ArrayList<MMSign> signList = SignUtils.getAdjacentMinecartManiaSignList(getLocation(), 2);
		//
		//		for (MMSign sign : signList) {
		//			if (sign.executeAction(this, LaunchMinecartAction.class)) {
		//				break;
		//			}
		//		}

		if (!isMoving()) {
			if(reverseSearch){
				if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.WEST)) {
					setMotion(DirectionUtils.CompassDirection.WEST, speed);
				}
				else if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.SOUTH)) {
					setMotion(DirectionUtils.CompassDirection.SOUTH, speed);
				}
				else if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.EAST)) {
					setMotion(DirectionUtils.CompassDirection.EAST, speed);

				}
				else if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.NORTH)) {
					setMotion(DirectionUtils.CompassDirection.NORTH, speed);

				}			
			}
			else {
				if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.NORTH)) {
					setMotion(DirectionUtils.CompassDirection.NORTH, speed);

				}
				else if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.EAST)) {
					setMotion(DirectionUtils.CompassDirection.EAST, speed);

				}
				else if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.SOUTH)) {
					setMotion(DirectionUtils.CompassDirection.SOUTH, speed);

				}
				else if (MinecartUtils.validMinecartTrack(minecart.getLocation(), DirectionUtils.CompassDirection.WEST)) {
					setMotion(DirectionUtils.CompassDirection.WEST, speed);

				}

			}	

		}

		setFrozen(false);


		//		//Create event, then stop the cart and wait for the results
		//		MinecartLaunchedEvent mle = new MinecartLaunchedEvent(this, minecart.getVelocity().clone());
		//		setFrozen(true);
		//		MinecartMania.callEvent(mle);
		//		if (mle.isActionTaken()) {
		//			return;
		//		}
		//		else {
		//		minecart.setVelocity(mle.getLaunchSpeed());

		//	}

	}

	/**
	 * Multiplies the minecarts current motion by the given multiplier in a safe way that will avoid
	 * causing overflow, which will cause the minecart to grind to a halt.
	 * @param multiplier
	 */
	public void multiplyMotion(double multiplier) {
		if (MAXIMUM_MOMENTUM / Math.abs(multiplier) > Math.abs(getMotionX())) {
			setMotionX((getMotionX() * multiplier));
		}
		if (MAXIMUM_MOMENTUM / Math.abs(multiplier) > Math.abs(getMotionZ())) {
			setMotionZ(getMotionZ() * multiplier);
		}
	}

	protected Minecart replaceCart(Minecart m){
		EntityMinecartAbstract mhandle = ((CraftMinecart) m).getHandle();

		//check if already a mm entity.
		if(mhandle instanceof MMEntityMinecartTNT || 
				mhandle instanceof MMEntityMinecartHopper || 
				mhandle instanceof MMEntityMinecartSpawner || 
				mhandle instanceof MMEntityMinecartRideable ||
				mhandle instanceof MMEntityMinecartChest ||
				mhandle instanceof MMEntityMinecartFurnace)
			return m;

		//create new MM entity
		World nmsworld = ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) m.getWorld()).getHandle();

		EntityMinecartAbstract nmscart = null;

		if(mhandle instanceof EntityMinecartRideable){
			nmscart = new MMEntityMinecartRideable(nmsworld);
		}
		else if(mhandle instanceof net.minecraft.server.v1_7_R4.EntityMinecartChest){
			nmscart = new MMEntityMinecartChest(nmsworld);
		}
		else if(mhandle instanceof net.minecraft.server.v1_7_R4.EntityMinecartFurnace){
			nmscart = new MMEntityMinecartFurnace(nmsworld);
		}
		else if(mhandle instanceof net.minecraft.server.v1_7_R4.EntityMinecartHopper){
			nmscart = new MMEntityMinecartHopper(nmsworld);
		}
		else if(mhandle instanceof net.minecraft.server.v1_7_R4.EntityMinecartMobSpawner){
			nmscart = new MMEntityMinecartSpawner(nmsworld);
		}
		else if(mhandle instanceof net.minecraft.server.v1_7_R4.EntityMinecartTNT){
			nmscart = new MMEntityMinecartTNT(nmsworld);
		}
		else if(mhandle instanceof net.minecraft.server.v1_7_R4.EntityMinecartCommandBlock){
			nmscart = new MMEntityMinecartCommandBlock(nmsworld);
		}

		if (nmscart == null){
			Logger.severe("Unsupported minecart type " + mhandle.getClass().toString());
			return m;
		}

		nmscart.locX = m.getLocation().getX();
		nmscart.locY =  m.getLocation().getY();
		nmscart.locZ = m.getLocation().getZ();
		nmscart.yaw = mhandle.yaw;
		nmscart.pitch = mhandle.pitch;
		nmscart.motX = mhandle.motX;
		nmscart.motY = mhandle.motY;
		nmscart.motZ = mhandle.motZ;

		if (nmsworld.addEntity(nmscart)){

			Logger.debug("Replacing cart " + m.getEntityId() + " with " + nmscart.getId() + " " + nmscart.getName() + "owner " + getOwner());

			Minecart out = (Minecart) nmscart.getBukkitEntity();

			if (m instanceof StorageMinecart) {
				//copy over any inventory
				Inventory old = ((StorageMinecart)m).getInventory();
				Inventory newi = ((StorageMinecart)out).getInventory();
				for (ItemStack itemStack : old.getContents()) {
					if(itemStack!=null)  newi.addItem(itemStack);				
				}		
			}

			m.remove();	
			return out;

		}

		return m;


	}


	//	private MMMinecart replaceEntity(Minecart newMinecart) {
	//		this.minecart = replaceCart(newMinecart);
	//		return this;
	//	}

	public void reverse() {
		setMotionX(getMotionX() * -1);
		setMotionY(getMotionY() * -1);
		setMotionZ(getMotionZ() * -1);
	}

	public void setCooldown(){
		cooldown = 	 System.currentTimeMillis() +1000;
	}

	/**
	 * Creates a new data value if it does not already exists, or resets an existing value
	 * @param the string key the data value is associated with
	 * @param the value to store
	 */
	@ThreadSafe
	public final void setDataValue(String key, Object value) {
		if (value == null) {
			data.remove(key);
		}else {
			data.put(key, value);
		}
	}

	/**
	 * Frozen carts do not move, but retain their velocty. The do not raise update events.
	 * @param freeze
	 */
	public void setFrozen(boolean freeze){
		getHandle().setFrozen(freeze);
	}

	public void setMagnetic(boolean val) {
		getHandle().setMagnetic(val);

	}

	public void setMaxSpeed(double d) {
		minecart.setMaxSpeed(d);

	}
	public void setMotion(CompassDirection direction, double speed) {
		setMotionX(0);
		setMotionZ(0);

		switch(direction){
		case EAST:
			setMotionX(speed);
			break;
		case NORTH:
			setMotionZ(-speed);
			break;
		case NO_DIRECTION:
			break;
		case SOUTH:
			setMotionZ(speed);
			break;
		case WEST:
			setMotionX(-speed);
			break;
		default:
			launchCart(speed, false);
			break;
		}
	}

	public void setMotion(double motionX, double motionY, double motionZ) {
		setMotionX(motionX);
		setMotionY(motionY);
		setMotionZ(motionZ);
	}

	/**
	 * Set's the motion of this minecart
	 * @param motion to set
	 */
	public void setMotion(Vector motion) {
		setMotion(motion.getX(), motion.getY(), motion.getZ());
	}

	public void setMotionX(double motionX){
		getHandle().getEntity().motX = (motionX <= minecart.getMaxSpeed()) ?  motionX : minecart.getMaxSpeed();
	}

	public void setMotionY(double motionY){
		getHandle().getEntity().motY  = motionY;
	}

	public void setMotionZ(double motionZ){
		getHandle().getEntity().motZ  = (motionZ <= minecart.getMaxSpeed()) ?  motionZ : minecart.getMaxSpeed();
	}

	public void setPassenger(Entity entity) {

		CraftEntity e = (CraftEntity) entity;
		e.getHandle().mount((net.minecraft.server.v1_7_R4.Entity) this.getHandle());

	}

	/**
	 * Set's the previous direction of motion for this minecart
	 * @param previous direction
	 */
	public void setPreviousDirectionOfMotion(CompassDirection direction) {
		previousFacingDir = direction;
	}



	public void setWasMovingLastTick(boolean wasMovingLastTick) {
		this.wasMovingLastTick = wasMovingLastTick;
	}

	/**
	 * Stops this minecart
	 */
	public void stopCart() {
		setMotion(0D, 0D, 0D);
	}


	/**
	 * Teleports this minecart to the given location. Works with locations in other worlds
	 * @param location
	 * @return the new MinecartManiaMinecart at the end of the teleport, null if the teleport was unsuccessful
	 */
	public MMMinecart teleport(Location location) {
		if (!location.getWorld().equals(getWorld())) {

			//TODO: fix this with new entities.

			//			final Minecart newCart;
			//			location.getWorld().loadChunk(location.getBlock().getChunk());
			//			
			//			if (isStandardMinecart()) {
			//				newCart = (Minecart)location.getWorld().spawn(location, Minecart.class);
			//			}
			//			else if (isPoweredMinecart()) {
			//				newCart = (Minecart)location.getWorld().spawn(location, PoweredMinecart.class);
			//			}
			//			else {
			//				newCart = (Minecart)location.getWorld().spawn(location, StorageMinecart.class);
			//			}
			//			
			//			final Entity passenger = minecart.getPassenger();
			//			minecart.eject();
			//			if (passenger != null) {
			//				passenger.teleport(location);
			//			}
			//			Runnable update = new Runnable() {
			//				public void run() {
			//					if (passenger != null) {
			//						newCart.setPassenger(passenger);
			//					}
			//					newCart.setVelocity(minecart.getVelocity());
			//				}
			//			};
			//			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), update, 5);
			//
			//			MinecartManiaMinecart newMinecartManiaMinecart = this.replaceEntity(newCart);
			//			kill(false);
			//			return newMinecartManiaMinecart;
		}
		else{

			getHandle().getEntity().setLocation(location.getX(), location.getY(), location.getZ(), minecart.getLocation().getYaw(), minecart.getLocation().getPitch());
			return this;
		}

		//		if (minecart.teleport(location,org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN)) {
		//			return this;
		//		}
		return null;
	}

	public void updateCalendar() {
		Calendar current = Calendar.getInstance();
		if (cal.get(Calendar.SECOND) != current.get(Calendar.SECOND)) {
			MinecartTimeEvent e = new MinecartTimeEvent(this, cal, current);
			MinecartMania.callEvent(e);
			cal = current;
		}
	}

	public void updateChunks() {
		if (Settings.LoadChunksOnTrack) {
			//have to load 2 chunks in all directions or Minecraft will stop ticking the entity.
			MinecartManiaWorld.LoadChunksAround(getLocation(),2);
		}
	}

	/**
	 * Updates the minecart's previous location
	 */
	public void updateLocation() {
		previousLocation = minecart.getLocation().toVector().clone();
	}

	public boolean wasMovingLastTick() {
		return wasMovingLastTick;
	}
}
