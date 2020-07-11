package com.crowsofwar.avatar.common.capabilities;

import com.crowsofwar.avatar.api.capabilities.IPlayerShoulders;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;

import java.util.Collections;
import java.util.List;

public class PlayerShouldersHandler implements IPlayerShoulders {
	
	private boolean rightshoulder;
	private boolean leftshoulder;
	private boolean isriding = false;
	
    private final List<Entity> riders;
	
    public PlayerShouldersHandler() {
        this.riders = Lists.<Entity>newArrayList();	
	}

	@Override
	public List<Entity> getRiders() {
		 return (List<Entity>)(this.riders.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.riders));
	}

	@Override
	public void removeRiders(Entity passenger) {
	      this.riders.remove(passenger);
	}

	@Override
	public void addRiders(Entity passenger) {
		   this.riders.add(passenger);
	}

	@Override
	public boolean getRightShoulder() {
		return this.rightshoulder;
	}

	@Override
	public boolean getLeftShoulder() {

		return this.leftshoulder;
	}

	@Override
	public void setRightShoulder(boolean set) {
		this.rightshoulder = set;
	}

	@Override
	public void setLeftShoulder(boolean set) {
		this.leftshoulder = set;
	}

	@Override
	public void removeAllRiders() {
		this.riders.clear();
		
	}

}
