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
package com.crowsofwar.avatar.common.bending.ice;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityIceShield;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.SHIELD_SHATTER;

/**
 * @author CrowsOfWar
 */
public class AbilityIceBurst extends Ability {

	public AbilityIceBurst() {
		super(Icebending.ID, "ice_burst");
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();

		if (!bender.consumeChi(STATS_CONFIG.chiIceShieldCreate)) {
			return;
		}

		// Don't allow 2 ice shields at once
		if (data.hasStatusControl(SHIELD_SHATTER)) {
			return;
		}

		EntityIceShield shield = new EntityIceShield(world);
		shield.copyLocationAndAnglesFrom(entity);
		shield.setOwner(entity);

		AbilityData abilityData = ctx.getAbilityData();
		double damageMult = abilityData.getLevel() >= 1 ? 1.25 : 1;
		damageMult *= ctx.getPowerRatingDamageMod();
		float[] shardPitchAngles = {-10, 10};
		boolean targetMobs = abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST);
		float health = abilityData.getLevel() >= 2 ? 12 : 8;
		health += (float) ctx.getPowerRating() / 15;

		if (abilityData.getLevel() >= 2) {
			shardPitchAngles = new float[]{-20, 0, 30};
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			health = 18;
		}

		shield.setDamageMult(damageMult);
		shield.setTargetMobs(targetMobs);
		shield.setPitchAngles(shardPitchAngles);
		shield.setHealth(health);
		shield.setMaxHealth(health);
		shield.setAbility(this);

		world.spawnEntity(shield);
		data.addStatusControl(SHIELD_SHATTER);

		abilityData.addXp(SKILLS_CONFIG.iceShieldCreated);

	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public int getBaseParentTier() {
		return 4;
	}
}
