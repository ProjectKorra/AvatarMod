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
package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

/**
 * @author CrowsOfWar
 */
public class SmashGroundHandler extends TickHandler {

	@Override
	public boolean tick(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();

		if (entity.isInWater() || entity.onGround || bender.isFlying()) {

			if (entity.onGround) {

				double range = getRange();

				World world = entity.world;
				AxisAlignedBB box = new AxisAlignedBB(entity.posX - range, entity.posY - range,
						entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range);

				if (world instanceof WorldServer) {
					WorldServer World = (WorldServer) world;
					World.spawnParticle(EnumParticleTypes.CLOUD, box.maxX, box.maxY, box.maxZ,10, 0.2, 0, 0.2, 0.01);
				}
				if (!world.isRemote) {
					world.spawnParticle(EnumParticleTypes.CLOUD, true, box.maxX, box.maxY, box.maxZ, 0.01, 0.01, 0.01);
				}
				List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase target : nearby) {
					if (target != entity) {
						smashEntity(target, entity);
					}
				}

			}

			return true;
		}

		return false;
	}

	protected void smashEntity(EntityLivingBase target, EntityLivingBase entity) {
		if (target.attackEntityFrom(AvatarDamageSource.causeSmashDamage(target, entity), 5)) {
			BattlePerformanceScore.addLargeScore(entity);
		}

		Vector velocity = Vector.getEntityPos(target).minus(Vector.getEntityPos(entity));
		velocity = velocity.withY(1).times(getSpeed() / 20);
		target.addVelocity(velocity.x(), velocity.y(), velocity.z());

	}

	protected double getRange() {
		return 3;
	}

	/**
	 * The speed applied to hit entities, in m/s
	 */
	protected double getSpeed() {
		return 3;
	}

}
