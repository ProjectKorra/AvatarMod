/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.common.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class MiscData {
	
	private final Runnable save;
	
	private float fallAbsorption;
	private int timeInAir;
	private int abilityCooldown;
	private boolean wallJumping;
	private int petSummonCooldown;
	private boolean bisonFollowMode;
	private boolean canUseAbilities;
	private List<LightningRedirectionData> lightningRedirectionData;
	
	public MiscData(Runnable save) {
		this.save = save;
		this.bisonFollowMode = true;
		this.canUseAbilities = true;
		this.lightningRedirectionData = new ArrayList<>();
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(fallAbsorption);
		buf.writeInt(timeInAir);
		buf.writeInt(abilityCooldown);
		buf.writeBoolean(wallJumping);
		buf.writeInt(petSummonCooldown);
		buf.writeBoolean(bisonFollowMode);
		buf.writeBoolean(canUseAbilities);
	}
	
	public void fromBytes(ByteBuf buf) {
		fallAbsorption = buf.readFloat();
		timeInAir = buf.readInt();
		abilityCooldown = buf.readInt();
		wallJumping = buf.readBoolean();
		petSummonCooldown = buf.readInt();
		bisonFollowMode = buf.readBoolean();
		canUseAbilities = buf.readBoolean();
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		fallAbsorption = nbt.getFloat("FallAbsorption");
		timeInAir = nbt.getInteger("TimeInAir");
		abilityCooldown = nbt.getInteger("AbilityCooldown");
		wallJumping = nbt.getBoolean("WallJumping");
		petSummonCooldown = nbt.getInteger("PetSummonCooldown");
		bisonFollowMode = nbt.getBoolean("BisonFollowMode");
		// Use key ending with "a4.6" because there was a bug in a4.5 where canUseAbilities
		// wasn't correctly flagged to false.
		// This new key forces glitched players from a4.5 to reload their CanUseAbilities flag
		canUseAbilities = nbt.getBoolean("CanUseAbilitiesA4.6");
		if (!nbt.hasKey("CanUseAbilitiesA4.6")) {
			canUseAbilities = true;
		}
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("FallAbsorption", fallAbsorption);
		nbt.setInteger("TimeInAir", timeInAir);
		nbt.setInteger("AbilityCooldown", abilityCooldown);
		nbt.setBoolean("WallJumping", wallJumping);
		nbt.setInteger("PetSummonCooldown", petSummonCooldown);
		nbt.setBoolean("BisonFollowMode", bisonFollowMode);
		nbt.setBoolean("CanUseAbilitiesA4.6", canUseAbilities);
	}
	
	public float getFallAbsorption() {
		return fallAbsorption;
	}
	
	public void setFallAbsorption(float fallAbsorption) {
		if (fallAbsorption == 0 || fallAbsorption > this.fallAbsorption) this.fallAbsorption = fallAbsorption;
	}
	
	public int getTimeInAir() {
		return timeInAir;
	}
	
	public void setTimeInAir(int time) {
		this.timeInAir = time;
	}
	
	public int getAbilityCooldown() {
		return abilityCooldown;
	}
	
	public void setAbilityCooldown(int cooldown) {
		if (cooldown < 0) cooldown = 0;
		this.abilityCooldown = cooldown;
		save.run();
	}
	
	public void decrementCooldown() {
		if (abilityCooldown > 0) {
			abilityCooldown--;
			save.run();
		}
	}
	
	public boolean isWallJumping() {
		return wallJumping;
	}
	
	public void setWallJumping(boolean wallJumping) {
		this.wallJumping = wallJumping;
	}
	
	public int getPetSummonCooldown() {
		return petSummonCooldown;
	}
	
	public void setPetSummonCooldown(int petSummonCooldown) {
		this.petSummonCooldown = petSummonCooldown;
	}

	public boolean getBisonFollowMode() {
		return bisonFollowMode;
	}

	public void setBisonFollowMode(boolean followMode) {
		bisonFollowMode = followMode;
	}

	public boolean getCanUseAbilities() {
		return canUseAbilities;
	}

	public void setCanUseAbilities(boolean canUseAbilities) {
		this.canUseAbilities = canUseAbilities;
	}

	public List<LightningRedirectionData> getLightningRedirectionData() {
		return lightningRedirectionData;
	}

}
