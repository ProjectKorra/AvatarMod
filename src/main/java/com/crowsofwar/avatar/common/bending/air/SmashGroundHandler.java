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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
		int ticks = 0;

		if (entity.isInWater() || entity.onGround || bender.isFlying()) {

			if (entity.onGround) {

				ticks++;
				double range = getRange();

				World world = entity.world;
				AxisAlignedBB box = new AxisAlignedBB(entity.posX - range, entity.posY - range,
						entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range);

				float speed = 0.4F;

				//For radial particle spawning; all credit for this part goes to Electroblob, as his earthquake code
				//was invaluable for this process.
				for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (ticks * 1.5)) {
					float x = (float) (entity.posX < 0 ? (entity.posX + ((ticks * speed)) * Math.sin(angle) - 1)
							: (float) (entity.posX + ((ticks * speed)) * Math.sin(angle)));
					float y = (float) (entity.posY);
					float z = entity.posZ < 0 ? (float) (entity.posZ + ((ticks * speed)) * Math.cos(angle) - 1)
							: (float) (entity.posZ + ((ticks * speed)) * Math.cos(angle));

					double distance = entity.getDistance(x, y, z);

					if (distance > 3) {
						x = (float) (entity.posX + getRange());
						y = (float) (entity.posY);
						z = (float) (entity.posZ + getRange());
					}
					if (!world.isRemote) {
						world.spawnParticle(getParticle(), false, x, y, z, 0.01, 0.01, 0.01);
						world.spawnParticle(getParticle(), false, -x, y, -z, 0.01, 0.01, 0.01);
						WorldServer World = (WorldServer) world;
						World.spawnParticle(getParticle(), x, y, z, 100, 0, 0, 0, 0.1);
						World.spawnParticle(getParticle(), -x, y, -z, 100, 0, 0, 0, 0.1);
						entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, getSound(), SoundCategory.BLOCKS, 4F, 0.5F);

					}


					List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase target : nearby) {
					if (target != entity) {
						smashEntity(target, entity);
					}
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

	protected EnumParticleTypes getParticle() {
		return EnumParticleTypes.CLOUD;
	}

	protected SoundEvent getSound() {
		return SoundEvents.ENTITY_FIREWORK_LAUNCH;
	}

	protected SoundCategory getSoundCategory() {
		return SoundCategory.BLOCKS;
	}


}
