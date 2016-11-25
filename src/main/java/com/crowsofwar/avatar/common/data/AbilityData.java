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
		if (xp == this.xp) return;
		if (xp < 0) xp = 0;
		if (xp > 100) xp = 100;
		this.xp = xp;
		data.saveChanges();
		data.getNetworker().markChanged(AvatarPlayerData.KEY_ABILITY_DATA, data.abilityData());
	}
	
	/**
	 * Add XP to this ability data. However, the added experience will be
	 * multiplied by a number to add exponential progression.
	 */
	public void addXp(float xp) {
		xp *= 1 - 0.95 * Math.sqrt(this.xp / 100);
		if (xp == 0) return;
		setXp(this.xp + xp);
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		xp = nbt.getFloat("Xp");
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("Xp", xp);
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ability.getId()); // ability ID read from createFromBytes
		buf.writeFloat(xp);
	}
	
	private void fromBytes(ByteBuf buf) {
		xp = buf.readFloat();
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
