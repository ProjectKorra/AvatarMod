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

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents saveable data about an ability. These are not singletons; there is
 * as many instances as required for each player data.
 * 
 * @author CrowsOfWar
 */
public class AbilityData {
	
	public static final int MAX_LEVEL = 3;
	public static final int MAX_TRAINABLE_LEVEL = MAX_LEVEL - 1;
	
	private final AvatarPlayerData data;
	private final BendingAbility ability;
	private float xp;
	private int level;
	
	public AbilityData(AvatarPlayerData data, BendingAbility ability) {
		this.data = data;
		this.ability = ability;
		this.xp = 0;
		this.level = 0;
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		if (level < 0) level = 0;
		if (level > MAX_LEVEL) level = MAX_LEVEL;
		this.level = level;
	}
	
	public void addLevel() {
		setLevel(level + 1);
	}
	
	public float getTotalXp() {
		return level * 33 + xp / 33f;
	}
	
	/**
	 * Sets the XP level to the given amount, clamping from 0-100. If more than
	 * 100, goes to next level. Will also save the AvatarPlayerData. Does not
	 * sync new XP.
	 */
	public void setXp(float xp) {
		if (xp == this.xp) return;
		if (xp < 0) xp = 0;
		if (xp > 100) {
			xp = 0;
			addLevel();
		}
		if (xp > this.xp && xp > getMaxXp()) xp = getMaxXp();
		
		this.xp = xp;
		data.saveChanges();
		data.getNetworker().markChanged(AvatarPlayerData.KEY_ABILITY_DATA, data.abilityData());
	}
	
	/**
	 * Add XP to this ability data. However, the added experience will be
	 * multiplied by a number to add exponential progression. Also syncs the new
	 * XP.
	 */
	public void addXp(float xp) {
		xp *= 1 - 0.95 * Math.sqrt(this.xp / 100);
		if (xp == 0) return;
		setXp(this.xp + xp);
		data.sync();
	}
	
	public int getRoadblockLevel() {
		return roadblock;
	}
	
	public void setRoadblock(int level) {
		if (level < 0) level = 0;
		if (level > 3) level = 3;
		this.roadblock = level;
		data.saveChanges();
		data.getNetworker().markChanged(AvatarPlayerData.KEY_ABILITY_DATA, data.abilityData());
	}
	
	public void incrementRoadblock() {
		setRoadblock(roadblock + 1);
		data.sync();
	}
	
	public boolean isMaxLevel() {
		return level >= 3;
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		xp = nbt.getFloat("Xp");
		roadblock = nbt.getInteger("Roadblock");
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
		nbt.setInteger("Roadblock", roadblock);
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ability.getId()); // ability ID read from createFromBytes
		buf.writeFloat(xp);
		buf.writeInt(roadblock);
	}
	
	private void fromBytes(ByteBuf buf) {
		xp = buf.readFloat();
		roadblock = buf.readInt();
	}
	
	/**
	 * Reads ability data from the network.
	 * 
	 * @return The ability data with correct ability and XP, but null if invalid
	 *         ability ID (does not log errors)
	 */
	public static AbilityData createFromBytes(ByteBuf buf, AvatarPlayerData data) {
		int abilityId = buf.readInt();
		BendingAbility ability = BendingManager.getAbility(abilityId);
		if (ability == null) {
			return null;
		} else {
			AbilityData abilityData = new AbilityData(data, ability);
			abilityData.fromBytes(buf);
			return abilityData;
		}
	}
	
}
