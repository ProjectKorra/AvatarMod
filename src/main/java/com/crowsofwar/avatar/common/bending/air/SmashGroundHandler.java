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

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

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
				smashEntity(entity);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, getSound(), getSoundCategory(), 4F, 0.5F);

			}


			return true;
		}

		return false;
	}

	protected void smashEntity(EntityLivingBase entity) {
		World world = entity.world;
		EntityShockwave shockwave = new EntityShockwave(world);
		shockwave.setDamage(getDamage());
		shockwave.setOwner(entity);
		shockwave.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
		shockwave.setKnockbackHeight(getKnockbackHeight());
		shockwave.setSpeed(getSpeed()/5);
		shockwave.setRange(getRange());
		shockwave.setParticle(getParticle());
		shockwave.setParticleAmount(getParticleAmount());
		shockwave.setParticleSpeed(getParticleSpeed());
		world.spawnEntity(shockwave);
	}

	protected boolean isFire() {
		return false;
	}

	protected int fireTime() {
		return 0;
	}

	protected double getRange() {
		return 3;
	}

	protected EnumParticleTypes getParticle() {
		return EnumParticleTypes.CLOUD;
	}

	protected int getParticleAmount() {
		return 2;
	}

	protected double getParticleSpeed() {
		return 0.2F;
	}

	protected double getSpeed() {
		return 4;
	}

	protected float getKnockbackHeight() {
		return 0.225F;
	}


	protected SoundEvent getSound() {
		return SoundEvents.BLOCK_FIRE_EXTINGUISH;
	}

	protected SoundCategory getSoundCategory() {
		return SoundCategory.BLOCKS;
	}


	protected float getDamage() {
		return 3;
	}

}

