package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.utils.DirectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public abstract class GenericSensor implements Sensor {
    public boolean state = false;
    public boolean master = true;
    public SensorDataTable data = null;
    protected Location sign;
    protected SensorType type;
    protected String name;
    protected ArrayList<GenericSensor> pairedSensors = null;
    protected GenericSensor masterSensor = null;

    public GenericSensor(SensorType type, Sign sign, String name) {
        this.type = type;
        this.sign = sign.getBlock().getLocation();
        this.name = name;

        ConcurrentHashMap<Block, Sensor> list = SensorManager.getSensorList();
        Iterator<Entry<Block, Sensor>> i = list.entrySet().iterator();
        while (i.hasNext()) {
            Entry<Block, Sensor> e = i.next();
            if (!equals(e.getKey())) {
                if (e.getValue().getName().equals(getName())) {
                    master = false;
                    ((GenericSensor) e.getValue()).clearCache();
                }
            }
        }
    }

    public boolean isState() {
        return state;
    }

    protected void setState(boolean state) {
        setState(state, isMaster());
    }

    private void setState(boolean state, boolean force) {
        if (!force && !isMaster()) {
            getMaster().setState(state, true);
        } else if (force) {
            if (isMaster()) {
                ArrayList<GenericSensor> slaves = getSlaves();
                for (GenericSensor sensor : slaves) {
                    if (sensor.master) {
                        Logger.severe("Duplicate Master sensors found! Attempting to correct...");
                        sensor.master = false; //should not happen
                        sensor.clearCache();
                    }
                    sensor.setState(state, true);
                }
            }

            if (state != this.state) {
                if (!state) {
                    delayedDisable();
                } else {
                    this.state = true;
                    update();
                }
            }
        }
    }

    private void delayedDisable() {
        Runnable task = new Runnable() {
            public void run() {
                if (getLocation().getBlock().getState() instanceof Sign) {
                    disable();
                    update();
                }
            }
        };
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), task, com.afforess.minecartmania.config.Settings.SensorDisableDelay);
    }

    private void disable() {
        this.state = false;
    }

    protected void update() {
        Block diode = getDiode();
        if (diode != null) {
            diode.getWorld().loadChunk(diode.getChunk());
            byte data = diode.getData();
            if (output()) {
                diode.setTypeId(Item.DIODE_BLOCK_ON.getId());
            } else {
                diode.setTypeId(Item.DIODE_BLOCK_OFF.getId());
            }
            diode.setData(data);
        }

    }

    public Block getDiode() {
        if (hasSign()) {
            Sign sign = getSign();
            BlockFace back = DirectionUtils.CompassDirectionToBlockFace(DirectionUtils.getOppositeDirection(DirectionUtils.getSignFacingDirection(sign)));
            Block diode = sign.getBlock().getRelative(back);
            if (diode.getTypeId() == Item.DIODE_BLOCK_OFF.getId() || diode.getTypeId() == Item.DIODE_BLOCK_ON.getId()) {
                return diode;
            }
        }
        return null;
    }

    public boolean isMaster() {
        return master || getName().isEmpty();
    }

    private GenericSensor getMaster() {
        checkCache();
        return masterSensor;
    }

    private ArrayList<GenericSensor> getSlaves() {
        if (isMaster() && !getName().isEmpty()) {
            checkCache();
            return pairedSensors;
        }
        return new ArrayList<GenericSensor>();
    }


    public String getName() {
        return name;
    }


    public boolean output() {
        return state;
    }


    public Sign getSign() {
        return (Sign) getLocation().getBlock().getState();
    }

    private boolean hasSign() {
        return getLocation().getBlock().getState() instanceof Sign;
    }


    public Location getLocation() {
        return this.sign;
    }


    public SensorType getType() {
        return type;
    }


    public boolean equals(Object other) {
        if (other instanceof Sensor) {
            return equals(((Sensor) other).getLocation());
        }
        if (other instanceof Location) {
            return equals((Location) other);
        }
        if (other instanceof Block) {
            return equals(((Block) other).getLocation());
        }
        return false;
    }


    public boolean equals(Location location) {
        Block diode = getDiode();
        if (diode != null) {
            if (diode.getLocation().equals(location)) {
                return true;
            }
        }
        return location.equals(getLocation());
    }


    public void kill() {

    }


    public SensorDataTable getDataTable() {
        if (data == null) {
            data = new SensorDataTable(sign, name, type, state, master);
        }
        return data;
    }

    public void clearCache() {
        pairedSensors = null;
        Logger.debug("Sensor Cache Cleared");
    }

    public void checkCache() {
        if (pairedSensors == null) {
            Logger.debug("Sensor Cache Re-Calculated");
            boolean masterFound = false;
            masterSensor = null;
            pairedSensors = new ArrayList<GenericSensor>();
            ConcurrentHashMap<Block, Sensor> list = SensorManager.getSensorList();
            Iterator<Entry<Block, Sensor>> i = list.entrySet().iterator();
            while (i.hasNext()) {
                Entry<Block, Sensor> e = i.next();
                if (!equals(e.getKey())) {
                    if (e.getValue().getName().equals(getName()) && e.getValue().getType() == getType()) {
                        pairedSensors.add((GenericSensor) e.getValue());
                        if (((GenericSensor) e.getValue()).isMaster()) {
                            if (!masterFound) {
                                masterFound = true;
                                masterSensor = (GenericSensor) e.getValue();
                            } else {
                                Logger.severe("Duplicate Master sensors found! Attempting to correct...");
                                ((GenericSensor) e.getValue()).master = false;
                            }
                        }
                    }
                }
            }
            if (!masterFound) {
                master = true;
                masterSensor = this;
            }
        }
    }
}
