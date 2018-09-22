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
import com.crowsofwar.avatar.common.entity.EntityShockwave;
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

	public static TickHandler SMASH_GROUND = new SmashGroundHandler();

	@Override
	public boolean tick(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		if (entity.isInWater() || entity.onGround || bender.isFlying()) {

			if (entity.onGround) {

				double range = getRange();

				AxisAlignedBB box = new AxisAlignedBB(entity.posX - range, entity.getEntityBoundingBox().minY,
						entity.posZ - range, entity.posX + range, entity.posY + entity.getEyeHeight(), entity.posZ + range);


				EntityShockwave shockwave = new EntityShockwave(world);
				shockwave.setDamage(getDamage());
				shockwave.setOwner(entity);
				shockwave.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
				shockwave.setParticle(getParticle());
				shockwave.setParticleSpeed(getParticleSpeed());
				shockwave.setParticleAmount(getNumberOfParticles());
				shockwave.setKnockbackHeight(getKnockbackHeight());
				shockwave.setSpeed(getSpeed()/10);
				shockwave.setRange(getRange());
				world.spawnEntity(shockwave);
				/*if (!world.isRemote) {
					WorldServer World = (WorldServer) world;
					for (double i = 0; i < range; ) {
						for (int j = 0; j < 90; j++) {
							Vector lookPos;
							if (i >= 1) {
								lookPos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
										j * 4), 0).times(i);
							} else {
								lookPos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
										j * 4), 0);
							}
							World.spawnParticle(getParticle(), lookPos.x() + entity.posX, entity.getEntityBoundingBox().minY,
									lookPos.z() + entity.posZ, getNumberOfParticles(), 0, 0, 0, getParticleSpeed() / 4);
						}
						i = i + range / 10;
					}
				}**/
				world.playSound(null, entity.posX, entity.posY, entity.posZ, getSound(), getSoundCategory(), 4F, 0.5F);


			/*	List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase target : nearby) {
					if (target != entity) {
						smashEntity(target, entity);
					}
				}**/
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
		double distance = Vector.getEntityPos(target).dist(Vector.getEntityPos(entity));
		double direction = (getRange() - distance) * (getSpeed() / 2) / getRange();
		velocity = velocity.times(direction).withY(getKnockbackHeight() / 4);
		target.addVelocity(velocity.x(), velocity.y(), velocity.z());
	}

	protected double getRange() {
		return 3;
	}

	/**
	 * The speed applied to hit entities, in m/s
	 */
	protected double getSpeed() {
		return 4;
	}

	protected float getKnockbackHeight() {
		return 0.75F;
	}

	protected EnumParticleTypes getParticle() {
		return EnumParticleTypes.CLOUD;
	}

	protected SoundEvent getSound() {
		return SoundEvents.BLOCK_FIRE_EXTINGUISH;
	}

	protected SoundCategory getSoundCategory() {
		return SoundCategory.BLOCKS;
	}

	/**
	 *
	 * @return The number of the particles. This is actually an inverse function; the bigger the number,
	 * the less particles there are.
	 */
	protected int getNumberOfParticles() {
		return 20;
	}

	protected float getParticleSpeed() {
		return 0F;
	}

	protected float getDamage() {
		return 3;
	}

}

