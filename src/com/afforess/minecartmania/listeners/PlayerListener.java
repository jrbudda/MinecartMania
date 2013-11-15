package com.afforess.minecartmania.listeners;

import javax.persistence.OptimisticLockException;

import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaPlayer;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMDataTable;

public class PlayerListener implements Listener{

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (Settings.PreserveMinecartsonRiderLogout) {
			MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer());
			if (event.getPlayer().getVehicle() instanceof Minecart) {
				final MMMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)player.getPlayer().getVehicle(), null);
				try {
					MMDataTable data = new MMDataTable(minecart, player.getName());
					Logger.debug("Saving and removing minecart for " + player.getName());
					MMDataTable.save(data);
					minecart.killNoReturn();
				}
				catch (Exception e) {
					Logger.severe("Failed to remove the minecart when " + player.getName() + " disconnected " + e.getMessage());
					e.printStackTrace();
					Logger.logCore(e.getMessage(), false);
				}
			}
		}
	}

	//	@EventHandler
	//	public void onPlayerWarp(org.bukkit.event.player.PlayerTeleportEvent event) {
	//	//	if(event.getPlayer().isInsideVehicle() && event.getPlayer().getVehicle() instanceof Minecart) event.setCancelled(true);
	//	}


	public static void spawnCartForRider(final MinecartManiaPlayer player, final MMDataTable data){
		if (data != null && player !=null) {

			//delaying this allows the chunks to load and player entity to be created.
			MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(),new Runnable(){
				@Override
				public void run() {
					Logger.debug("Loading saved minecart for " + player.getName());

					final MMMinecart minecart = data.toMinecartManiaMinecart();

					if (minecart == null) Logger.debug("Could not create saved minecart for " + player.getName());
					else {
						//delay this because... i dont know, but you have to or it doesnt set the passenger right.
						MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(),new Runnable(){
							@Override
							public void run() {
								minecart.setPassenger(player.getPlayer());
							}},2);
					}
					
					try {
						MMDataTable.delete(data);
					}
					catch (OptimisticLockException ole){
						//Make every effort to delete the entry
						Logger.severe("wat wat!!");
						final String name = player.getName();
						Thread deleteEntry = new Thread() {
							public void run() {
								try {
									sleep(5000);
									MMDataTable.delete(data);
								}
								catch (Exception e) {
									Logger.severe("Failed to remove the minecart data entry when " + name + " connected");
									Logger.logCore(e.getMessage(), false);
								}
							}
						};
						deleteEntry.start();
					}
				}
			});
		}
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (Settings.PreserveMinecartsonRiderLogout) {
			MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer());
			MMDataTable data = MMDataTable.getDataTable(player.getName());
			if(data==null) Logger.debug("No data found for " + player.getName());
			spawnCartForRider(player, data);
		}
	}



	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {	
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (event.getClickedBlock().getState() instanceof Sign) {
			BlockListener.checkSensor(event.getClickedBlock().getLocation(), event.getPlayer());
		}
		
		if (Settings.RailAdjusterTool== null) {
			return;
		}
		if (event.getItem() == null) {
			return;
		}

		int id = event.getItem().getTypeId();
		int data = Item.getItem(id).size() == 1 ? 0 : event.getItem().getDurability();
		Item holding = Item.getItem(id, data);
		if (holding == null) {
			return;
		}
		if (holding.equals(Settings.RailAdjusterTool)) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getTypeId() == Item.RAILS.getId()) {
				int oldData = event.getClickedBlock().getData();
				data = oldData + 1;
				if (data > 9) data = 0;
				MinecartManiaWorld.setBlockData(event.getPlayer().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ(), data);
				event.setCancelled(true);
			}
		}
	}

}
