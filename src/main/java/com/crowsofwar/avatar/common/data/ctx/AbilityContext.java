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
package com.crowsofwar.avatar.common.data.ctx;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityContext extends BendingContext {
	
	private final Ability ability;
	private final double powerRating;

	public AbilityContext(BendingData data, Result raytrace, Ability ability, EntityLivingBase
			entity, double powerRating) {
		super(data, entity, raytrace);
		this.ability = ability;
		this.powerRating = powerRating;
	}
	
	public AbilityContext(BendingData data, EntityLivingBase entity, Bender bender, Result raytrace,
						  Ability ability, double powerRating) {
		super(data, entity, bender, raytrace);
		this.ability = ability;
		this.powerRating = powerRating;
	}
	
	public AbilityData getAbilityData() {
		return getData().getAbilityData(ability);
	}
	
	public int getLevel() {
		return getAbilityData().getLevel();
	}
	
	public AbilityTreePath getPath() {
		return getAbilityData().getPath();
	}
	
	/**
	 * Returns true if ability is on level 4 and has selected that path.
	 */
	public boolean isMasterLevel(AbilityTreePath path) {
		return getLevel() == 3 && getPath() == path;
	}

	/**
	 * Gets the current power rating, from -100 to +100.
	 */
	public double getPowerRating() {
		return powerRating;
	}

	/**
	 * Gets the power rating, but in the range 0.25 to 2.0 for convenience in damage calculations.
	 * <ul>
	 *     <li>-100 power rating gives 0.25; damage would be 1/4 of normal</li>
	 *     <li>0 power rating gives 1; damage would be the same as normal</li>
	 *     <li>100 power rating gives 2; damage would be twice as much as usual</li>
	 */
	public double getPowerRatingDamageMod() {
		return getBender().getDamageMult(ability.getBendingId());
	}

}
