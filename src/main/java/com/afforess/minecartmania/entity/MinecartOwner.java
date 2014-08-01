package com.afforess.minecartmania.entity;

import com.afforess.minecartmania.utils.StringUtils;
import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Chest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "minecartowners")
public class MinecartOwner {
    @Id
    private int id;
    @NotNull
    private String owner;
    @NotNull
    private String world;

    public MinecartOwner() {
        this.owner = "none";
    }

    public MinecartOwner(String owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean hasOwner() {
        return !owner.equals("none");
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public World getBukkitWorld() {
        return Bukkit.getServer().getWorld(world);
    }

    public Object getRealOwner() {
        if (owner.equals("none")) {
            return null;
        }
        if (owner.contains("[") && owner.contains("]")) {
            try {
                int x, y, z;
                String[] split = owner.split(":");
                x = Integer.valueOf(StringUtils.getNumber(split[0]));
                y = Integer.valueOf(StringUtils.getNumber(split[1]));
                z = Integer.valueOf(StringUtils.getNumber(split[2]));
                if (MinecartManiaWorld.getBlockAt(getBukkitWorld(), x, y, z).getState() instanceof Chest) {
                    return MinecartManiaWorld.getMinecartManiaChest((Chest) MinecartManiaWorld.getBlockAt(getBukkitWorld(), x, y, z).getState());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return Bukkit.getServer().getPlayer(owner);
    }


}
