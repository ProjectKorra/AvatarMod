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
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityIcePrison;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityIcePrison extends Ability {

	public AbilityIcePrison() {
		super(Icebending.ID, "ice_prison");
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();

		if (bender.consumeChi(ConfigStats.STATS_CONFIG.chiPrison)) {

			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			Vector start = Vector.getEyePos(entity);
			Vector direction = Vector.getLookRectangular(entity);

			Predicate<Entity> filter = candidate -> candidate != entity && candidate instanceof EntityLivingBase;
			List<Entity> hit = Raytrace.entityRaytrace(world, start, direction, 10, filter);

			if (!hit.isEmpty()) {
				EntityLivingBase prisoner = (EntityLivingBase) hit.get(0);
				EntityIcePrison.imprison(prisoner, entity, this);

				world.playSound(null, prisoner.posX, prisoner.posY, prisoner.posZ,
						SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 2, 2);

				world.playSound(null, prisoner.posX, prisoner.posY, prisoner.posZ,
						SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1, 1);

				ctx.getAbilityData().addXp(SKILLS_CONFIG.icePrisoned);

			}

		}

	}

	@Override
	public int getBaseParentTier() {
		return 4;
	}

}
