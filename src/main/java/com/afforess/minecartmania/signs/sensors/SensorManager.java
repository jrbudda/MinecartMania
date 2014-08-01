package com.afforess.minecartmania.signs.sensors;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.afforess.minecartmania.debug.Logger;

public class SensorManager {
	//Maintain a list of active sensors. Saved on server shutdown.
	private static final ConcurrentHashMap<Block, Sensor> sensors = new ConcurrentHashMap<Block, Sensor>();
	private static Lock sensorLock = new ReentrantLock();

	public static com.avaje.ebean.EbeanServer database = com.afforess.minecartmania.MinecartMania.getInstance().getDatabase();
	
	public static Sensor getSensor(Location loc) {
		return getSensor(loc.getBlock(), false);
	}

	public static boolean isSign(Block block) {
		return block.getTypeId() == 63 || block.getTypeId() == 68;
	}

	public static Sensor getSensor(Block loc) {
		return getSensor(loc, false);
	}

	public static Sensor getSensor(Block loc, boolean checkDatabase) {
		//First check and see if this is an active sensor
		Sensor s = sensors.get(loc);
		if (s != null) {
			if (!isSign(loc)) {
				sensors.remove(loc);
				deleteSensor(s);
				s = null;
			}
			else if (!verifySensor((Sign)loc.getState(), s)) {
				sensors.remove(loc);
				deleteSensor(s);
				s = null;
			}
		}
		else if (checkDatabase) {
						if (sensorLock.tryLock()) {
							try {
								SensorDataTable data = getDatabase().find(SensorDataTable.class).where()
								.ieq("x", Integer.toString(loc.getX())).ieq("y", Integer.toString(loc.getY()))
								.ieq("z", Integer.toString(loc.getZ())).ieq("world", loc.getWorld().getName()).findUnique();
								if (data != null) {
									s = data.toSensor();
									if (s != null) {
										if (!isSign(loc)) {
											Logger.severe("Removing Invalid Sensor at " + loc.toString());
											sensors.remove(loc);
											deleteSensor(s);
											s = null;
										}
										else if (!verifySensor((Sign)loc.getState(), s)) {
											Logger.severe("Removing Invalid Sensor at " + loc.toString());
											sensors.remove(loc);
											deleteSensor(s);
											s = null;
										}
										Logger.debug("Loading sensor from db " + s.getName());
										sensors.put(loc, s);
									}
								}
							}
							finally {
								sensorLock.unlock();
							}
						}
		}
		return s;
	}

	public static void saveSensor(Sensor sensor) {
		getDatabase().save(sensor.getDataTable());
	}

	private static com.avaje.ebean.EbeanServer getDatabase(){
		return database;
	}

	public static void deleteSensor(final Sensor sensor) {
		getDatabase().delete(sensor.getDataTable());
	}

	public static Sensor addSensor(Location loc, Sensor s) {
		saveSensor(s);
		return sensors.put(loc.getBlock(), s);
	}

	public static ConcurrentHashMap<Block, Sensor> getSensorList() {
		return sensors;
	}

	public static int getCount(){
		return sensors.size();
	}

	public static void loadsensors(){
		int maxId = 0;
		List<SensorDataTable> data = database.find(SensorDataTable.class).findList();
		for (SensorDataTable temp : data) {
			if (temp.hasValidLocation()) {
				Block block = temp.getLocation().getBlock();
				if (isSign(block)) {
					getSensor(block, true); //force load of sensor
					if (temp.getId() > maxId) {
						maxId = temp.getId();
					}
				}
			}
		}

		SensorDataTable.lastId = maxId;
	}
	
	public static boolean delSensor(Location loc) {
		return sensors.remove(loc) != null;
	}

	public static boolean verifySensor(Sign sign, Sensor sensor) {
		if (sign.getLine(0).split(":").length != 2) {
			return false;
		}
		if (!sign.getLine(0).split(":")[1].trim().equals(sensor.getType().getType())) {
			return false;
		}
		return sign.getLine(1).equals(sensor.getName());
	}
}
