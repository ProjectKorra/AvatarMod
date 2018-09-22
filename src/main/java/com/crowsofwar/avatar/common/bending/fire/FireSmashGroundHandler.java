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
package com.crowsofwar.avatar.common.bending.fire;


import com.crowsofwar.avatar.common.bending.air.SmashGroundHandler;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.entity.EntityFireShockwave;
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
public class FireSmashGroundHandler extends SmashGroundHandler {

	public static TickHandler SMASH_GROUND_FIRE = new FireSmashGroundHandler();

	@Override
	protected void smashEntity(EntityLivingBase entity) {
		World world = entity.world;
		EntityFireShockwave shockwave = new EntityFireShockwave(world);
		shockwave.setDamage(getDamage());
		shockwave.setOwner(entity);
		shockwave.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
		shockwave.setKnockbackHeight(getKnockbackHeight());
		shockwave.setSpeed(getSpeed()/5);
		shockwave.setRange(getRange());
		shockwave.setFire(isFire());
		shockwave.setFireTime(fireTime());
		world.spawnEntity(shockwave);
	}

	@Override
	protected double getRange() {
		return 3;
	}

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.ITEM_FIRECHARGE_USE;
	}

	@Override
	protected float getKnockbackHeight() {
		return 0.2F;
	}

	@Override
	protected double getSpeed() {
		return 2;
	}

	@Override
	protected boolean isFire() {
		return true;
	}

	@Override
	protected int fireTime() {
		return 5;
	}
}
