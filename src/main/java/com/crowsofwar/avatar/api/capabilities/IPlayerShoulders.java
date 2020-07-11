package com.crowsofwar.avatar.api.capabilities;

import net.minecraft.entity.Entity;

import java.util.List;

public interface IPlayerShoulders {

    public boolean getRightShoulder();

    public boolean getLeftShoulder();

    public void setRightShoulder(boolean set);

    public void setLeftShoulder(boolean set);

    public List<Entity> getRiders();
    
    public void removeRiders(Entity passenger);
    
    public void removeAllRiders();
    
    public void addRiders(Entity passenger);
    
}
