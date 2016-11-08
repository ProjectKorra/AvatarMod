package com.crowsofwar.avatar.common.data;

import java.util.function.Consumer;

import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents saveable data about an ability. These are not singletons; there is
 * as many instances as required for each player data.
 * 
 * @author CrowsOfWar
 */
public class AbilityData {
	
	private final BendingAbility ability;
	private final Consumer<Float> changeListener;
	private float xp;
	
	public AbilityData(BendingAbility ability, Consumer<Float> onChange) {
		this.ability = ability;
		this.xp = 0;
		this.changeListener = onChange;
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
	public float getXp() {
		return xp;
	}
	
	public void setXp(float xp) {
		if (xp < 0) xp = 0;
		if (xp > 100) xp = 100;
		this.xp = xp;
		changeListener.accept(xp);
	}
	
	public void addXp(float xp) {
		setXp(this.xp + xp);
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		setXp(nbt.getFloat("Xp"));
	}
	
}
