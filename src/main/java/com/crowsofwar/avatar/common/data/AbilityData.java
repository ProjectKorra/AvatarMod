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

import com.crowsofwar.avatar.common.bending.Ability;
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
	
	private final BendingData data;
	private final Ability ability;
	private float xp;
	/**
	 * The current level. -1 for locked
	 * <p>
	 * Note that it starts at 0, so 0 = Level I, 1 = Level II, etc.
	 */
	private int level;
	private AbilityTreePath path;
	
	public AbilityData(BendingData data, Ability ability) {
		this.data = data;
		this.ability = ability;
		this.xp = 0;
		this.level = -1;
		this.path = AbilityTreePath.MAIN;
	}
	
	public Ability getAbility() {
		return ability;
	}
	
	/**
	 * Get the current level of this ability. A value of -1 indicates this
	 * ability is {@link #isLocked() locked}.
	 * <p>
	 * Starts at 0, so 0 is level I, 1 is level II, etc.
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Sets the current level of the ability to the given amount. Clamps between
	 * 0 and MAX_LEVEL. Marks dirty, but does not sync.
	 */
	public void setLevel(int level) {
		if (level < -1) level = -1;
		if (level > MAX_LEVEL) level = MAX_LEVEL;
		this.level = level;
		checkPath();
		save();
	}
	
	/**
	 * Increments the level by 1 and syncs the level. If the ability is locked,
	 * unlocks the ability. Does not allow going past the maximum level.
	 */
	public void addLevel() {
		setLevel(level + 1);
	}
	
	/**
	 * Get the current path we are on. Note that while path other than MAIN only
	 * matters in level IV, it can be other than MAIN on levels I,II, or III,
	 * but will be ignored. On level IV however, path cannot be MAIN and will
	 * either be FIRST or SECOND.
	 */
	public AbilityTreePath getPath() {
		return path;
	}
	
	/**
	 * Set the current path. For details about valid values, see
	 * {@link #getPath()}.
	 */
	public void setPath(AbilityTreePath path) {
		this.path = path;
		checkPath();
		save();
	}
	
	/**
	 * Checks whether is at level IV and has chosen the given path.
	 */
	public boolean isMasterPath(AbilityTreePath path) {
		return isMaxLevel() && this.path == path;
	}
	
	/**
	 * Ensures ability path is correct - on level 4, if still on MAIN path, will
	 * switch to FIRST automatically
	 */
	private void checkPath() {
		if (level == 3 && path == AbilityTreePath.MAIN) {
			setPath(AbilityTreePath.FIRST);
		}
	}
	
	/**
	 * Gets the total experience value from 0-100, taking into account the level
	 * AND current xp.
	 */
	public float getTotalXp() {
		return level * 33 + xp * 33f / 100;
	}
	
	public float getXp() {
		return xp;
	}
	
	/**
	 * Sets the XP level to the given amount, clamping from 0-100. If more than
	 * 100, won't to next level. Will also save the AvatarPlayerData.
	 */
	public void setXp(float xp) {
		if (xp == this.xp) return;
		
		if (xp < 0) xp = 0;
		if (xp > 100) {
			xp = 100;
		}
		
		this.xp = xp;
		save();
		
	}
	
	/**
	 * Add XP to this ability data. Will be {@link #getXpMultiplier()
	 * multiplied} for exponential decay.
	 */
	public void addXp(float xp) {
		
		xp *= getXpMultiplier();
		if (xp == 0) return;
		
		setXp(this.xp + xp);
		
	}
	
	public boolean isMaxLevel() {
		return level >= MAX_LEVEL;
	}
	
	/**
	 * Gets the multiplier applied to any XP gains. Tends to lower faster near
	 * ends of levels, and also is lower as the level increases. The minimum
	 * value is .5 (unless on level 3).
	 */
	public float getXpMultiplier() {
		float x = xp / 100;
		if (level == 0) {
			return 1 - .2f * x * x;
		}
		if (level == 1) {
			return .8f - .2f * (x - 1) * (x - 1);
		}
		if (level == 2) {
			return .6f - .1f * (x - 2) * (x - 2);
		}
		return 0;
	}
	
	/**
	 * Returns whether this ability is locked and the player cannot use it.
	 */
	public boolean isLocked() {
		return level == -1;
	}
	
	/**
	 * If this ability is {@link #isLocked() locked}, unlocks the ability.
	 */
	public void unlockAbility() {
		if (isLocked()) {
			level = 0;
		}
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		xp = nbt.getFloat("Xp");
		level = nbt.getInteger("Level");
		path = AbilityTreePath.fromId(nbt.getInteger("Path"));
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
		nbt.setInteger("Level", level);
		nbt.setInteger("Path", path.id());
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ability.getId()); // ability ID read from createFromBytes
		buf.writeFloat(xp);
		buf.writeInt(level);
		buf.writeInt(path.id());
	}
	
	private void fromBytes(ByteBuf buf) {
		xp = buf.readFloat();
		level = buf.readInt();
		path = AbilityTreePath.fromId(buf.readInt());
	}
	
	/**
	 * Saves but does not sync
	 */
	private void save() {
		data.save(DataCategory.ABILITY_DATA);
	}
	
	/**
	 * Reads ability data from the network.
	 * 
	 * @return The ability data with correct ability and XP, but null if invalid
	 *         ability ID (does not log errors)
	 */
	public static AbilityData createFromBytes(ByteBuf buf, BendingData data) {
		int abilityId = buf.readInt();
		Ability ability = BendingManager.getAbility(abilityId);
		if (ability == null) {
			return null;
		} else {
			AbilityData abilityData = new AbilityData(data, ability);
			abilityData.fromBytes(buf);
			return abilityData;
		}
	}
	
	/**
	 * Describes which path the abilityData is currently taking. Main path is
	 * for levels 1, 2, and 3. For level 4, either FIRST or SECOND path can be
	 * chosen.
	 */
	public enum AbilityTreePath {
		
		MAIN,
		FIRST,
		SECOND;
		
		public int id() {
			return ordinal();
		}
		
		public static AbilityTreePath fromId(int id) {
			if (id < 0 || id >= values().length)
				throw new IllegalArgumentException("No AbilityTreePath for id: " + id);
			
			return values()[id];
		}
		
	}
	
}
