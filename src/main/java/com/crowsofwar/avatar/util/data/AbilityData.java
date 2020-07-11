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

package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents saveable data about an ability. These are not singletons; there is
 * as many instances as required for each player data.
 *
 * @author CrowsOfWar
 */
public class AbilityData {

	public static final int MAX_LEVEL = 3;

	private final BendingData data;
	private final String abilityName;
	private float lastXp;
	private float xp;
	//Whether to switch the path of the ability
	private final boolean switchPath;
	private int abilityCooldown;
	//How much exhaustion to add to the player (affects the hunger bar, like sprinting or fighting)
	private float exhaustion;
	/**
	 * Current burnout amount of the ability.
	 * <p>
	 *     Goes from 0 to 100, where 100 is basically half damage and effects.
	 *     Discourages move spamming.
	 *     Affects other stats like so:
	 *     <p>
	 *         Chi: 0 is base cost, 100 is 1.5x cost.
	 *         Cooldown: 0 is base cooldown, 100 is 1.5x cooldown.
	 *         Exhaustion: 0 is base exhaustion, 100 ix 2x exhaustion.
	 *     </p>
	 * </p>
	 */
	private float burnOut;
	/**
	 * The current level. -1 for locked
	 * <p>
	 * Note that it starts at 0, so 0 = Level I, 1 = Level II, etc.
	 */
	private int level;
	private AbilityTreePath path;

	public AbilityData(BendingData data, Ability ability) {
		this(data, ability.getName());
	}

	public AbilityData(BendingData data, String abilityName) {
		this.data = data;
		this.abilityName = abilityName;
		this.xp = 0;
		this.level = -1;
		this.path = AbilityTreePath.MAIN;
		this.switchPath = false;
		this.abilityCooldown = 0;
		this.burnOut = 0;
		this.exhaustion = 0;
	}

	public AbilityData(BendingData data, String abilityName, boolean switchPath) {
		this.data = data;
		this.abilityName = abilityName;
		this.xp = 0;
		this.level = -1;
		this.path = AbilityTreePath.MAIN;
		this.switchPath = switchPath;
		this.abilityCooldown = 0;
		this.burnOut = 0;
		this.exhaustion = 0;
	}

	/**
	 * Reads ability data from the network.
	 */
	public static AbilityData createFromBytes(ByteBuf buf, BendingData data) {
		String abilityName = GoreCoreByteBufUtil.readString(buf);
		AbilityData abilityData = new AbilityData(data, abilityName);
		abilityData.fromBytes(buf);
		return abilityData;
	}

	public static AbilityData get(EntityLivingBase entity, String abilityName) {
		return BendingData.get(entity).getAbilityData(abilityName);
	}

	public static AbilityData get(World world, UUID playerId, String abilityName) {
		return BendingData.get(world, playerId).getAbilityData(abilityName);
	}

	public static AbilityData get(World world, String playerName, String abilityName) {
		return BendingData.get(world, playerName).getAbilityData(abilityName);
	}


	public void setBurnOut(float burnOut) {
		this.burnOut = burnOut;
		//Burnout isn't greater than 100 or less than 0
		this.burnOut = MathHelper.clamp(this.burnOut, 0, 100);
	}

	public float getBurnOut() {
		return this.burnOut;
	}

	public void setAbilityCooldown(int cooldown) {
		abilityCooldown = cooldown;
	}

	public int getAbilityCooldown() {
		return abilityCooldown;
	}

	public void decrementCooldown() {
		abilityCooldown--;
	}

	@Nullable
	public Ability getAbility() {
		return Abilities.get(abilityName);
	}

	public String getAbilityName() {
		return abilityName;
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
	 * Gets a string representing both the level, and the path (if this is upgraded to level IV).
	 * For example, "lvl2" or "lvl4_1"; levels reported here start at 1.
	 */
	public String getLevelDesc() {
		String str = "lvl" + (level + 1);
		if (level == 3) {
			str += "_" + path.ordinal();
		}
		return str;
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
	 * Same as isMasterLevel(), but accounts for a dynamic change
	 */
	public boolean isDynamicMasterLevel(AbilityTreePath path) {
		return getLevel() == 3 && getDynamicPath() == path;
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


	/*
	 * Same as getPath(), but accounts for a dynamic change
	 */
	public AbilityTreePath getDynamicPath() {
		AbilityTreePath currentPath = getPath();
		if (switchPath) {
			if (currentPath == AbilityTreePath.FIRST) {
				return AbilityTreePath.SECOND;
			} else if (currentPath == AbilityTreePath.SECOND) {
				return AbilityTreePath.FIRST;
			} else {
				return AbilityTreePath.MAIN;
			}
		} else {
			return currentPath;
		}
	}

	/**
	 * Gets the total experience value from 0-100, taking into account the level
	 * AND current xp. Maximum value is 136.
	 */
	public float getTotalXp() {
		return level * 33 + xp * 33F / 100;
	}

	/**
	 *
	 * @return Returns a modifier based on the current xp, from 1 to 1.36.
	 */
	public float getXpModifier() {
		return getTotalXp() / 100 < 1 ? 1 : getTotalXp() / 100;
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
		this.lastXp = this.xp;

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

	/**
	 * Gets the previous XP value before the last time {@link #setXp(float)} or {@link #addXp(float)}
	 * was called. Not synced to client.
	 */
	public float getLastXp() {
		return lastXp;
	}

	/**
	 * Resets the previous XP value so it is the same as the current one.
	 */
	public void resetLastXp() {
		lastXp = xp;
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

	public int getCurrentTier() {
		int tier = 0;
		if (getAbility() != null) {
			tier += getAbility().getBaseTier();
			switch (level) {
				default:
					break;
				case 2:
					tier++;
					break;
				case 3:
					tier += 2;
					break;
			}
		}
		return tier;
	}

	public void readFromNbt(NBTTagCompound nbt) {
		xp = nbt.getFloat("Xp");
		lastXp = nbt.getFloat("lastXp");
		level = nbt.getInteger("Level");
		path = AbilityTreePath.get(nbt.getInteger("Path"));
		abilityCooldown = nbt.getInteger("AbilityCooldown");
	}

	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
		nbt.setFloat("LastXp", lastXp);
		nbt.setInteger("Level", level);
		nbt.setInteger("Path", path.id());
		nbt.setInteger("AbilityCooldown", abilityCooldown);
	}

	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeString(buf, abilityName);
		buf.writeFloat(xp);
		buf.writeInt(level);
		buf.writeInt(path.id());
		buf.writeInt(abilityCooldown);
	}

	private void fromBytes(ByteBuf buf) {
		xp = buf.readFloat();
		level = buf.readInt();
		path = AbilityTreePath.get(buf.readInt());
		abilityCooldown = buf.readInt();
	}

	/**
	 * Saves but does not sync
	 */
	private void save() {
		data.save(DataCategory.ABILITY_DATA);
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

		@Nullable
		public static AbilityTreePath get(int id) {
			if (id < 0 || id >= values().length) {
				return null;
			}
			return values()[id];
		}

		public int id() {
			return ordinal();
		}

	}

}
