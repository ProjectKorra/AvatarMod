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
	
	private final AvatarPlayerData data;
	private final BendingAbility ability;
	private float xp;
	/**
	 * The current level.
	 * <p>
	 * Note that it starts at 0, so 0 = Level I, 1 = Level II, etc.
	 */
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
		
		this.xp = xp;
		
		data.saveChanges();
		data.getNetworker().markChanged(AvatarPlayerData.KEY_ABILITY_DATA, data.abilityData());
	}
	
	/**
	 * Add XP to this ability data. Will be {@link #getXpMultiplier()
	 * multiplied} for exponential decay. Also syncs the new XP.
	 */
	public void addXp(float xp) {
		
		xp *= getXpMultiplier();
		if (xp == 0) return;
		
		setXp(this.xp + xp);
		data.sync();
		
	}
	
	public boolean isMaxLevel() {
		return level >= 3;
	}
	
	/**
	 * Gets the multiplier applied to any XP gains. Tends to lower faster near
	 * ends of levels, and also is lower as the level increases. The minimum
	 * value is .5 (unless on level 3).
	 */
	public float getXpMultiplier() {
		if (level == 0) {
			return 1 - .2f * xp * xp;
		}
		if (level == 1) {
			return .8f - .2f * (xp - 1) * (xp - 1);
		}
		if (level == 2) {
			return .6f - .1f * (xp - 2) * (xp - 2);
		}
		return 0;
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		xp = nbt.getFloat("Xp");
		level = nbt.getInteger("Level");
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
		nbt.setInteger("Level", level);
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ability.getId()); // ability ID read from createFromBytes
		buf.writeFloat(xp);
		buf.writeInt(level);
	}
	
	private void fromBytes(ByteBuf buf) {
		xp = buf.readFloat();
		level = buf.readInt();
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
