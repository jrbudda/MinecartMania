package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.utils.ItemUtils;
import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;

@Entity()
@Table(name = "sensors")
public class SensorDataTable {
    public static int lastId = 0;
    @Id
    private int id;
    @NotNull
    private boolean state = false;
    @NotNull
    private int x;
    @NotNull
    private int y;
    @NotNull
    private int z;
    @NotNull
    private String world;
    @NotNull
    private SensorType type;
    @NotNull
    private String name;
    @NotNull
    private boolean master = true;

    public SensorDataTable() {
        id = ++lastId;
    }

    public SensorDataTable(Location location, String name, SensorType type, boolean state, boolean master) {
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        world = location.getWorld().getName();
        this.name = name;
        this.type = type;
        this.state = state;
        this.master = master;
        id = ++lastId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the state
     */
    public boolean isState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x coordinate to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y coordinate to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the z coordinate
     */
    public int getZ() {
        return z;
    }

    /**
     * @param z the z coordinate to set
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return the world
     */
    public String getWorld() {
        return world;
    }

    /**
     * @param world the world to set
     */
    public void setWorld(String world) {
        this.world = world;
    }

    public boolean hasValidLocation() {
        return Bukkit.getServer().getWorld(world) != null;
    }

    public Location getLocation() {
        return new Location(Bukkit.getServer().getWorld(world), x, y, z);
    }

    /**
     * @return the type
     */
    public SensorType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(SensorType type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the master
     */
    public boolean isMaster() {
        return master;
    }

    /**
     * @param master the master to set
     */
    public void setMaster(boolean master) {
        this.master = master;
    }

    public Sensor toSensor() {
        if (!(getLocation().getBlock().getState() instanceof Sign)) {
            return null;
        }
        Sensor sensor = null;
        Sign sign = (Sign) getLocation().getBlock().getState();
        switch (type) {
            case DETECT_ALL:
                sensor = new SensorAll(type, sign, name);
                break;
            case DETECT_ENTITY:
                sensor = new SensorEntity(type, sign, name);
                break;
            case DETECT_EMPTY:
                sensor = new SensorEmpty(type, sign, name);
                break;
            case DETECT_MOB:
                sensor = new SensorMob(type, sign, name);
                break;
            case DETECT_ANIMAL:
                sensor = new SensorAnimal(type, sign, name);
                break;
            case DETECT_PLAYER:
                sensor = new SensorPlayer(type, sign, name);
                break;
            case DETECT_STORAGE:
                sensor = new SensorStorage(type, sign, name);
                break;
            case DETECT_POWERED:
                sensor = new SensorPowered(type, sign, name);
                break;
            case DETECT_ITEM_AND:
                sensor = new SensorItem(type, sign, name, Arrays.asList(ItemUtils.getItemStringToMaterial(sign.getLine(2))));
                break;
            case DETECT_ITEM_OR:
                sensor = new SensorItemOr(type, sign, name, Arrays.asList(ItemUtils.getItemStringToMaterial(sign.getLine(2))));
                break;
            case DETECT_PLYR_NAME:
                sensor = new SensorPlayerName(type, sign, name, sign.getLine(2).trim());
                break;
            case DETECT_ZOMBIE:
                sensor = new SensorZombie(type, sign, name);
                break;
            case DETECT_SKELETON:
                sensor = new SensorSkeleton(type, sign, name);
                break;
            case DETECT_CREEPER:
                sensor = new SensorCreeper(type, sign, name);
                break;
            case DETECT_PIG:
                sensor = new SensorPig(type, sign, name);
                break;
            case DETECT_SHEEP:
                sensor = new SensorSheep(type, sign, name);
                break;
            case DETECT_COW:
                sensor = new SensorCow(type, sign, name);
                break;
            case DETECT_CHICKEN:
                sensor = new SensorChicken(type, sign, name);
                break;
            case DETECT_ZOMBIEPIGMAN:
                sensor = new SensorZombiePigman(type, sign, name);
                break;
            case DETECT_STATION:
                sensor = new SensorStation(type, sign, name);
                break;
            case DETECT_ITEMHELD:
                sensor = new SensorItemHeld(type, sign, name);
                break;
        }
        ((GenericSensor) sensor).master = master;
        ((GenericSensor) sensor).state = state;
        ((GenericSensor) sensor).data = this;
        return sensor;
    }

}
