package com.afforess.minecartmania;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import com.afforess.minecartmania.signs.actions.HoldSignData;
import com.afforess.minecartmania.signs.sensors.SensorDataTable;



public class MinecartManiaSignCommands extends org.bukkit.plugin.java.JavaPlugin{

	public static  MinecartManiaSignCommands instance;
	public static final int DATABASE_VERSION = 2;

	public void onEnable() {

		instance = this;
		setupDatabase();

		//load sensors
	}



	public static MinecartManiaSignCommands getInstance(){
		return instance;
	}

	public void removedb(){
		if(this.getDatabase()!=null) this.removeDDL();
	}

	private int getDatabaseVersion() {
		try {
			try {
				getDatabase().find(SensorDataTable.class).findRowCount();
			} catch (PersistenceException ex) {
				return 0;
			}
			try {
				getDatabase().find(HoldSignData.class).findRowCount();
			} catch (PersistenceException ex) {
				return 1;
			}
			return DATABASE_VERSION;
		} catch (Exception e) {
			return 0;
		}
	}

	//	protected void setupInitialDatabase() {
	//		try {
	//			getDatabase().find(SensorDataTable.class).findRowCount();
	//			getDatabase().find(HoldSignData.class).findRowCount();
	//		}
	//		catch (PersistenceException ex) {
	//	//		log.info("Installing database");
	//			installDDL();
	//		}
	//	}

	protected void setupDatabase() {
		int version = getDatabaseVersion();
		switch(version) {
		case 0: return;
		case 1: return;
		case 2: /*up to date database*/break;
		}
	}

	//	private void upgradeDatabase(int current) {
	//	//	log.info(String.format("Upgrading database from version %d to version %d", current, DATABASE_VERSION));
	//		if (current == 1) {
	//			List<SensorDataTable> sensorList = getDatabase().find(SensorDataTable.class).findList();
	//			try {
	//				this.removeDDL();
	//			}
	//			catch (Exception e) {
	//				//this will throw an error because not all the tables can be dropped, but ignore it
	//			}
	//			setupInitialDatabase();
	//	//		log.info("Recoved " + sensorList.size() + " from database");
	//			for (SensorDataTable sensor : sensorList) {
	//				SensorDataTable temp = new SensorDataTable();
	//				temp.setId(sensor.getId());
	//				temp.setMaster(sensor.isMaster());
	//				temp.setName(sensor.getName());
	//				temp.setState(sensor.isState());
	//				temp.setType(sensor.getType());
	//				temp.setWorld(sensor.getWorld());
	//				temp.setX(sensor.getX());
	//				temp.setY(sensor.getY());
	//				temp.setZ(sensor.getZ());
	//				getDatabase().save(temp);
	//			}
	//		}
	//		
	//		/*
	//		 * Add additional versions here
	//		 */
	//	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(SensorDataTable.class);
		list.add(HoldSignData.class);
		return list;
	}

}
