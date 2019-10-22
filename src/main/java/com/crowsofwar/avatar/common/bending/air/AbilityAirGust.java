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

package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;

/**
 * @author CrowsOfWar
 */
public class AbilityAirGust extends Ability {

	public AbilityAirGust() {
		super(Airbending.ID, "air_gust");
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		if (!bender.consumeChi(STATS_CONFIG.chiAirGust)) return;

		Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
				Math.toRadians(entity.rotationPitch));
		Vector pos = Vector.getEyePos(entity);
		if (bender.consumeChi(STATS_CONFIG.chiAirGust)) {

			float speed = 35;
			float size = 1.0F;
			int lifetime = 20;
			if (ctx.getLevel() == 1) {
				speed = 45;
				size = 1.25F;
				lifetime += 10;
			}
			if (ctx.getLevel() >= 2) {
				speed = 50;
				size = 1.5F;
				lifetime += 20;
			}
			if (ctx.isDynamicMasterLevel(FIRST)) {
				size = 2.0F;
				lifetime += 10;
			}
			EntityAirGust gust = new EntityAirGust(world);
			gust.setVelocity(look.times(speed));
			gust.setPosition(pos.minusY(0.5));
			gust.setOwner(entity);
			gust.setEntitySize(size);
			gust.setLifeTime(lifetime);
			gust.rotationPitch = entity.rotationPitch;
			gust.rotationYaw = entity.rotationYaw;
			gust.setPushStone(ctx.getLevel() >= 1);
			gust.setPushIronDoor(ctx.getLevel() >= 2);
			gust.setPushIronTrapDoor(ctx.getLevel() >= 2);
			gust.setDestroyProjectiles(ctx.isDynamicMasterLevel(FIRST));
			//gust.setAirGrab(ctx.isDynamicMasterLevel(SECOND));
			gust.setAbility(this);
			world.spawnEntity(gust);
		}
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirGust(this, entity, bender);
	}

}
