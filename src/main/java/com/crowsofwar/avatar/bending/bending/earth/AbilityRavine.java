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

package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.EntityRavine;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityRavine extends Ability {

	public AbilityRavine() {
		super(Earthbending.ID, "ravine");
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();

		float chi = STATS_CONFIG.chiRavine;
		if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
			chi = STATS_CONFIG.chiRavineLvl4_1;
		}

		if (bender.consumeChi(chi)) {

			AbilityData abilityData = ctx.getData().getAbilityData(this);
			float xp = abilityData.getTotalXp();
			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();

			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			Vector position = Vector.getLookRectangular(entity).times(1.1);

			double speed = ctx.getLevel() >= 1 ? 14 : 8;
			speed += ctx.getPowerRating() / 25;

			float damage = 0.75f + xp / 100;
			damage *= ctx.getPowerRatingDamageMod();

			EntityRavine ravine = new EntityRavine(world);
			ravine.setOwner(entity);
			ravine.setDamageMult(damage);
			ravine.setPosition(Vector.getEntityPos(entity).plus(Vector.getLookRectangular(entity).withY(0)));
			ravine.setVelocity(look.times(speed));
			ravine.setDamage(damage * STATS_CONFIG.ravineSettings.damage);
			ravine.setAbility(this);
			ravine.setElement(new Earthbending());
			ravine.setLifeTime(80);
			ravine.setEntitySize(0.125F);
			ravine.setXp(SKILLS_CONFIG.ravineHit);
			ravine.setDistance(ctx.getLevel() >= 2 ? 16 : 10);
			ravine.setBreakBlocks(ctx.isMasterLevel(AbilityTreePath.FIRST));
			ravine.setDropEquipment(ctx.isMasterLevel(AbilityTreePath.SECOND));
			if (!world.isRemote) {
				world.spawnEntity(ravine);
			}

		}
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public int getBaseTier() {
		return 2;
	}
}
