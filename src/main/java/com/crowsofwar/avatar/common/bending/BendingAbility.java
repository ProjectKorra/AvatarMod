package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace;

/**
 * Encapsulates all logic required for a bending ability. There is 1 instance of a bending ability
 * for each ability present - similar to BendingController.
 * 
 * @param <STATE>
 *            The IBendingState this ability uses
 * 
 * @author CrowsOfWar
 */
public abstract class BendingAbility<STATE extends IBendingState> {
	
	protected final BendingController<STATE> controller;
	
	public BendingAbility(BendingController<STATE> controller) {
		this.controller = controller;
	}
	
	/**
	 * Returns whether this bending ability should be subscribed to an update tick event.
	 */
	public abstract boolean requiresUpdateTick();
	
	/**
	 * TODO actually call this
	 * <p>
	 * Tick this bending ability. Only called if {@link #requiresUpdateTick()} is true.
	 */
	public void update(AvatarPlayerData data) {}
	
	/**
	 * Execute this ability.
	 * 
	 * @param data
	 *            Player data to use.
	 */
	public abstract void execute(AvatarPlayerData data);
	
	/**
	 * Get the Id of this ability.
	 */
	public abstract int getId();
	
	/**
	 * Get the texture index of this bending ability. -1 for no texture.
	 */
	public abstract int getIconIndex();
	
	/**
	 * Returns whether this bending ability has an icon.
	 */
	public boolean hasTexture() {
		return getIconIndex() > -1;
	}
	
	/**
	 * Get a request for a raytrace.
	 */
	public abstract Raytrace.Info getRaytrace();
	
}
