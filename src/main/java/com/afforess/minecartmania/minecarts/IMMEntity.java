package com.afforess.minecartmania.minecarts;

public interface IMMEntity {

    public boolean getUphill();

    public boolean getDownhill();

    public boolean getOnRails();

    public boolean getFrozen();

    public void setFrozen(boolean value);

    public net.minecraft.server.v1_7_R4.EntityMinecartAbstract getEntity();

    public void setCollisions(boolean value);

    public void setDerailedFriction(double value);

    public void setEmptyFriction(double value);

    public void setMaxPushSpeed(double value);

    public void setPassengerFriction(double value);

    public void setSlopeSpeed(double value);

    public void setMagnetic(boolean value);

    public void setGravity(double value);


}
