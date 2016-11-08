package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents saveable data about an ability. These are not singletons; there is
 * as many instances as required for each player data.
 * 
 * @author CrowsOfWar
 */
public class AbilityData {
	
	private final AvatarPlayerData data;
	private final BendingAbility ability;
	private float xp;
	
	public AbilityData(AvatarPlayerData data, BendingAbility ability) {
		this.data = data;
		this.ability = ability;
		this.xp = 0;
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
	public float getXp() {
		return xp;
	}
	
	/**
	 * Sets the XP level to the given amount, clamping from 0-100. Will also
	 * save the AvatarPlayerData.
	 */
	public void setXp(float xp) {
		if (xp < 0) xp = 0;
		if (xp > 100) xp = 100;
		this.xp = xp;
		data.saveChanges();
	}
	
	public void addXp(float xp) {
		setXp(this.xp + xp);
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		xp = nbt.getFloat("Xp");
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
	}
	
}
