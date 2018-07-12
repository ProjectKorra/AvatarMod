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


				if (!world.isRemote) {
					WorldServer World = (WorldServer) world;
					World.spawnParticle(getParticle(), entity.posX, entity.posY, entity.posZ, getNumberOfParticles(), 0, 0, 0, getParticleSpeed());
					//World.spawnParticle(getParticle(), entity.posX - 3, y, entity.posZ - 3, 100, 0, 0, 0, 0.1);
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, getSound(), getSoundCategory(), 4F, 0.5F);

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
		if (target.attackEntityFrom(AvatarDamageSource.causeSmashDamage(target, entity), getDamage())) {
			BattlePerformanceScore.addLargeScore(entity);
		}

		Vector velocity = Vector.getEntityPos(target).minus(Vector.getEntityPos(entity));
		velocity = velocity.withY(getKnockbackHeight()).times(getSpeed() / 20);
		target.addVelocity(velocity.x(), velocity.y(), velocity.z());
	}

	protected double getRange() {
		return 3;
	}

	/**
	 * The speed applied to hit entities, in m/s
	 */
	protected double getSpeed() {
		return 5;
	}

	protected float getKnockbackHeight() {
		return 0.75F;
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

	protected float getParticleSpeed() {
		return 0.2F;
	}

	protected float getDamage() {
		return 3;
	}

	protected int getNumberOfParticles() {
		return 200;
	}
}

