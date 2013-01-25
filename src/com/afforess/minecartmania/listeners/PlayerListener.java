package com.afforess.minecartmania.listeners;

import javax.persistence.OptimisticLockException;

import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;
import com.afforess.minecartmaniacore.entity.Item;
import com.afforess.minecartmaniacore.entity.MinecartManiaMinecartDataTable;
import com.afforess.minecartmaniacore.entity.MinecartManiaPlayer;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class PlayerListener implements Listener{

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (Settings.isDisappearOnDisconnect()) {
			MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer());
			if (event.getPlayer().getVehicle() instanceof Minecart) {
				final MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)player.getPlayer().getVehicle());
				try {
					MinecartManiaMinecartDataTable data = new MinecartManiaMinecartDataTable(minecart, player.getName());
					MinecartManiaMinecartDataTable.save(data);
					minecart.kill(false);
				}
				catch (Exception e) {
					MinecartManiaLogger.getInstance().severe("Failed to remove the minecart when " + player.getName() + " disconnected");
					MinecartManiaLogger.getInstance().logCore(e.getMessage(), false);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Settings.isDisappearOnDisconnect()) {
			MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer());
			final MinecartManiaMinecartDataTable data = MinecartManiaMinecartDataTable.getDataTable(player.getName());
			if (data != null) {
				MinecartManiaMinecart minecart = data.toMinecartManiaMinecart();
				minecart.setPassenger(player.getPlayer());
				try {
					MinecartManiaMinecartDataTable.delete(data);
				}
				//Make every effort to delete the entry
				catch (OptimisticLockException ole) {
					final String name = event.getPlayer().getName();
					Thread deleteEntry = new Thread() {
						public void run() {
							try {
								sleep(5000);
								MinecartManiaMinecartDataTable.delete(data);
							}
							catch (Exception e) {
								MinecartManiaLogger.getInstance().severe("Failed to remove the minecart data entry when " + name + " connected");
								MinecartManiaLogger.getInstance().logCore(e.getMessage(), false);
							}
						}
					};
					deleteEntry.start();
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if ((Item)MinecartManiaWorld.getConfigurationValue("MinecartTrackAdjuster") == null) {
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
		if (holding.equals((Item)MinecartManiaWorld.getConfigurationValue("MinecartTrackAdjuster"))) {
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
