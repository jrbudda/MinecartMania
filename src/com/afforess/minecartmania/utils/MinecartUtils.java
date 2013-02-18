package com.afforess.minecartmania.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class MinecartUtils {

	public static boolean isTrack(Block block) {
		return isTrack(block.getTypeId());
	}

	public static boolean isTrack(Item item) {
		return isTrack(item.getId());
	}

	public static boolean isTrack(int id) {
		return id == Item.RAILS.getId() || id == Item.POWERED_RAIL.getId() || id == Item.DETECTOR_RAIL.getId();
	}

	public static boolean isTrack(Location location) {
		return isTrack(location.getBlock().getTypeId());
	}

	public static Set<CompassDirection> getValidDirections(Block b) {
		Set<CompassDirection> out = new HashSet<com.afforess.minecartmania.utils.DirectionUtils.CompassDirection>();

		if (!isTrack(b.getLocation())) return out;

		if (isTrack(b.getLocation().add(-1, 0, 0))) out.add(CompassDirection.WEST);
		if (isTrack(b.getLocation().add(1, 0, 0))) out.add(CompassDirection.EAST);
		if (isTrack(b.getLocation().add(0, 0, 1))) out.add(CompassDirection.SOUTH);
		if (isTrack(b.getLocation().add(0, 0, -1))) out.add(CompassDirection.NORTH);
		return out;
	}

	public static boolean validMinecartTrackAnyDirection(World w, int x, int y, int z, int range) {
		return validMinecartTrack(w, x, y, z, range, DirectionUtils.CompassDirection.NORTH) ||
				validMinecartTrack(w, x, y, z, range, DirectionUtils.CompassDirection.EAST) || 
				validMinecartTrack(w, x, y, z, range, DirectionUtils.CompassDirection.SOUTH) ||
				validMinecartTrack(w, x, y, z, range, DirectionUtils.CompassDirection.WEST);
	}


	public static void toggleBlockFromEntering(Player player) {
		if (isBlockedFromEntering(player)) {
			MinecartManiaWorld.getMinecartManiaPlayer(player).setDataValue("Blocked From Entering Minecarts", null);
		}
		else {
			MinecartManiaWorld.getMinecartManiaPlayer(player).setDataValue("Blocked From Entering Minecarts", Boolean.TRUE);
		}
	}

	public static boolean isBlockedFromEntering(Player player) {
		return MinecartManiaWorld.getMinecartManiaPlayer(player).getDataValue("Blocked From Entering Minecarts") != null;
	}

	public static MMMinecart getNearestMinecartInRange(Location loc, int range){

		ArrayList<MMMinecart> carts = MinecartManiaWorld.getMinecartManiaMinecartList();

		MMMinecart closest = null;
		double closestdist = 0;
		for (MMMinecart cart : carts){
			if(loc.getWorld() != cart.getLocation().getWorld()) continue;
			double dist =	loc.distance(cart.getLocation());
			if(dist <= range){
				if (dist < closestdist || closest == null){
					closest = cart;
				}
			}
		}

		return closest;

	}

	public static boolean isSlopedTrack(Block rail) {
		return isSlopedTrack(rail.getWorld(), rail.getX(), rail.getY(), rail.getZ());
	}

	public static boolean isSlopedTrack(World w, int x, int y, int z) {
		int data = MinecartManiaWorld.getBlockData(w, x, y, z);
		return data >= 0x2 && data <= 0x5;
	}

	public static boolean isCurvedTrack(Block rail) {
		return rail.getTypeId() == Item.RAILS.getId() && rail.getData() > 5 && rail.getData() < 10;
	}

	public static boolean validMinecartTrack(Location loc, CompassDirection direction) {
		return validMinecartTrack(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 2, direction);
	}

	//TODO this method is not a perfect detection of track. It will give false positives for having 2 sets of parallel track, and when double curves are used
	private static boolean validMinecartTrack(World w, int x, int y, int z, int range, CompassDirection direction) {

		if (!isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y, z))) {
			y--;
			if (!isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y, z))) {
				return false;
			}
		}
		range--;

		while (range > 0) {

			if (direction == CompassDirection.WEST) {
				x--;
			} else if (direction == CompassDirection.NORTH) {
				z--;
			} else if (direction == CompassDirection.EAST) {
				x++;
			} else if (direction == CompassDirection.SOUTH) {
				z++;
			}

			if (isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y - 1, z))) {
				y--;
			} else if (isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y + 1, z))) {
				y++;
			}

			if (!isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y, z))){
				return false;
			}

			//			//dont need thing unless range > 2
			//			if (isTrack(MinecartManiaWorld.getBlockIdAt(w, x - 1, y, z))) {
			//				direction = CompassDirection.WEST;
			//			} else if (isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y, z - 1))) {
			//				direction = CompassDirection.NORTH;
			//			} else if (isTrack(MinecartManiaWorld.getBlockIdAt(w, x + 1, y, z))) {
			//				direction = CompassDirection.EAST;
			//			} else if (isTrack(MinecartManiaWorld.getBlockIdAt(w, x, y, z + 1))) {
			//				direction = CompassDirection.SOUTH;
			//			}

			range--;
		}

		return true;
	}

	public static boolean isAtIntersection(World w, int x, int y, int z) {
		int paths = 0;


		if(hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH)) paths +=1;
		if(hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH)) paths +=1;
		if(hasTrackConnectedOn(w, x, y, z, BlockFace.EAST)) paths +=1;
		if(hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) paths +=1;
		
		//		if(MinecartManiaWorld.getBlockIdAt(w, x, y, z) != Item.RAILS.getId()) return false; //this is only valid for non-powered rails.
		//
		//		int data = MinecartManiaWorld.getBlockData(w, x, y, z);
		//
		//		switch (data) {
		//		case 0: // north-south straight
		//			if (hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH) && hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH)) {
		//				paths = 2;
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) {
		//					paths++;
		//				}
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.EAST)) {
		//					paths++;
		//				}
		//			}
		//			break;
		//		case 1: // west-east straight
		//			if (hasTrackConnectedOn(w, x, y, z, BlockFace.EAST) && hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) {
		//				paths = 2;
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH)) {
		//					paths++;
		//				}
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH)) {
		//					paths++;
		//				}
		//			}
		//			break;
		//		case 6: // east-south corner
		//			if (hasTrackConnectedOn(w, x, y, z, BlockFace.EAST) && hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH)) {
		//				paths = 2;
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH)) {
		//					paths++;
		//				}
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) {
		//					paths++;
		//				}
		//			}
		//			break;
		//		case 7: // west-south corner
		//			if (hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH) && hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) {
		//				paths = 2;
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH)) {
		//					paths++;
		//				}
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.EAST)) {
		//					paths++;
		//				}
		//			}
		//			break;
		//		case 8: // west-north corner
		//			if (hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH) && hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) {
		//				paths = 2;
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.EAST)) {
		//					paths++;
		//				}
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH)) {
		//					paths++;
		//				}
		//			}
		//			break;
		//		case 9: // north-east corner
		//			if (hasTrackConnectedOn(w, x, y, z, BlockFace.NORTH) && hasTrackConnectedOn(w, x, y, z, BlockFace.EAST)) {
		//				paths = 2;
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.SOUTH)) {
		//					paths++;
		//				}
		//				if (hasTrackConnectedOn(w, x, y, z, BlockFace.WEST)) {
		//					paths++;
		//				}
		//			}
		//			break;
		//		}

		return paths > 2;
	}

	/**
	 * Checks whether a track piece at the given coordinate has another
	 * track piece logically connect to it in the given direction.
	 *
	 * valid:   =7  L=  F=  ==   etc
	 * invalid: 7=  =L  =F  =|   etc
	 *
	 * Valid track data values for the given directions:
	 * NORTH: 1, 6, 9  (3)
	 * EAST:  0, 6, 7  (4)
	 * SOUTH: 1, 7, 8  (2)
	 * WEST:  0, 8, 9  (5)
	 *  values in braces are for the slanted up track.
	 *  -- can add a check for the lower level too, but these will probably cause issues anyway
	 *  -- so just keep the requirement of having flat track
	 *  
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 * @return
	 */
	public static boolean hasTrackConnectedOn(World w, int x, int y, int z, BlockFace direction) {
		Block base = MinecartManiaWorld.getBlockAt(w, x, y, z);

		Block next = base.getRelative(direction);

		if ( isTrack(next) ) {
			byte nextData = next.getData();
			switch (direction) {
			case WEST:
				return (nextData == 1) || (nextData == 6) || (nextData == 9);
			case NORTH:
				return (nextData == 0) || (nextData == 6) || (nextData == 7);
			case EAST:
				return (nextData == 1) || (nextData == 7) || (nextData == 8);
			case SOUTH:
				return (nextData == 0) || (nextData == 8) || (nextData == 9);
			default:
				break;
			}

		}
		return false;
	}



	private static void doMinecartItemCollection(final MMMinecart minecart, List<Entity> entities) {

		for (Entity e : entities) {

			if (e.isDead()) {
				continue;
			}		

			if (minecart.isStorageMinecart() && e instanceof org.bukkit.entity.Item	&& ((MinecartManiaStorageCart)minecart).addItem(((org.bukkit.entity.Item)e).getItemStack())) {
				e.remove();
			}


		}

	}


	private static void doMinecartEntitiesOnRail(final MMMinecart minecart, List<Entity> entities) {
		//Have to handle all entites here, collisions are non-deterministic and not necessarily cancelable.

		for (Entity e : entities) {
			if (e instanceof LivingEntity && 	minecart.isApproaching(e.getLocation())){

				LivingEntity victim = (LivingEntity)(e);

				if (!(victim instanceof Player) && !(victim instanceof Wolf) && !victim.hasMetadata("NPC")) {					
					if (Settings.isMinecartsKillMobs()) {
						victim.remove();
					}
				}
				else if (victim instanceof Player){
					if(Settings.KillPlayersOnTrack && (minecart.getMotion().length() > (Settings.KillPlayersOnTrackMinnimumSpeed / 100) * .4) ){
						//die
						victim.setHealth(0);
					}
					else{			
						//get out the way
						bump(victim, minecart);
					}	
				}
				else{			
					//get out the way
					bump(victim, minecart);
				}	
			}

			else clearedItemFromRails(e, minecart);
		}


	}


	private static void bump(LivingEntity victim, MMMinecart minecart){

		Vector victor;
		double spd = Math.sqrt(minecart.getMotionX() * minecart.getMotionX() + minecart.getMotionZ()*minecart.getMotionZ());
		int mod=1;
		switch (minecart.getDirection()){
		case NORTH: 
			mod =-1;
		case SOUTH:
			if (victim.getLocation().getX() > minecart.getLocation().getX()){
				//west
				victor = new Vector(spd, .1 ,spd*mod);
			}
			else {
				//east
				victor = new Vector(-spd, .1 ,spd*mod);
			}
			break;
		case WEST: 
			mod =-1;
		case EAST:
			if (victim.getLocation().getZ() > minecart.getLocation().getZ()){
				//south
				victor = new Vector(spd*mod, .1 ,spd);
			}
			else {
				//north
				victor = new Vector(spd*mod, .1 ,-spd);
			}
			break;	
		default:
			victor = new Vector(spd, .1 ,spd);
		}
		//	victim.teleport(victim.getLocation().add(0,5,0));
		victor = victor.multiply(2);
		com.afforess.minecartmania.debug.Logger.debug("Set v on " + victim.toString() +" " + victor.toString() );
		victim.setVelocity(victor);
	}

	private static boolean clearedItemFromRails(Entity e, MMMinecart minecart) {

		if ( e instanceof LivingEntity) {
			return false;
		}

		if (e.getEntityId() == minecart.getEntityId()) {
			return false;
		}

		if (e instanceof Vehicle) {
			return false;
		}	


		//move it off to the side.
		if (minecart.isApproaching(e.getLocation())) {
			Location current = e.getLocation();
			if (minecart.getMotionX() != 0.0D) {
				Location test = current.clone();
				test.setZ(current.getZ()-3);
				Location loc = EntityUtils.getValidLocation(test.getBlock(), 1);
				if (loc != null) {
					e.teleport(loc);
					return true;
				}
				test.setZ(current.getZ()+3);
				loc = EntityUtils.getValidLocation(test.getBlock(), 1);
				if (loc != null) {
					e.teleport(loc);
					return true;
				}
			}
			if (minecart.getMotionZ() != 0.0D) {
				Location test = current.clone();
				test.setX(current.getX()-3);
				Location loc = EntityUtils.getValidLocation(test.getBlock(), 1);
				if (loc != null) {
					e.teleport(loc);
					return true;
				}
				test.setX(current.getX()+3);
				loc = EntityUtils.getValidLocation(test.getBlock(), 1);
				if (loc != null) {
					e.teleport(loc);
					return true;
				}
			}
		}

		return false;
	}

	public static void updateNearbyItems(final MMMinecart minecart) {

		List<Entity> list;	

		if(minecart instanceof MinecartManiaStorageCart){
			switch (minecart.getDirectionOfMotion()) {
			case NORTH: case SOUTH:
				list	= minecart.getBukkitEntity().getNearbyEntities(((MinecartManiaStorageCart)minecart).getItemRange() ,((MinecartManiaStorageCart)minecart).getItemCollectionRangeY() , .5);
				break;
			case EAST: case WEST:
				list	= minecart.getBukkitEntity().getNearbyEntities(.5 ,((MinecartManiaStorageCart)minecart).getItemCollectionRangeY() , ((MinecartManiaStorageCart)minecart).getItemRange());
				break;
			default:
				list	= minecart.getBukkitEntity().getNearbyEntities(((MinecartManiaStorageCart)minecart).getItemRange() ,((MinecartManiaStorageCart)minecart).getItemCollectionRangeY() , ((MinecartManiaStorageCart)minecart).getItemRange());
				break;
			}		
			doMinecartItemCollection(minecart, list);
		}

		//		switch (minecart.getDirectionOfMotion()) {
		//		case EAST: case WEST:
		//			list	= minecart.getBukkitEntity().getNearbyEntities(1.5 ,0, .75);
		//			break;
		//		case NORTH: case SOUTH:
		//			list	= minecart.getBukkitEntity().getNearbyEntities(.75 ,0, 1.5);
		//			break;
		//		default:
		//			list	= minecart.getBukkitEntity().getNearbyEntities(.75 ,0, .75);
		//			break;
		//		}

		list	= minecart.getBukkitEntity().getNearbyEntities(.25 ,0, .25);
		doMinecartEntitiesOnRail(minecart, list);

		//yea this is a bad idea
		//		Thread update = new Thread() {
		//			public void run() {
		//			
		//			}
		//		};
		//		update.start();
	}	
}
