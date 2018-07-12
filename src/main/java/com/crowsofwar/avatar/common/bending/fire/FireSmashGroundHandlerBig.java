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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * @author CrowsOfWar
 */
public class FireSmashGroundHandlerBig extends SmashGroundHandler {

	@Override
	protected void smashEntity(EntityLivingBase target, EntityLivingBase entity) {
		super.smashEntity(target, entity);
		target.setFire(4);
		entity.world.playSound(null, target.posX, target.posY, target.posZ,
				SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 1);
	}

	@Override
	protected double getRange() {
		return 4;
	}

	@Override
	protected double getSpeed() {
		return 8;
	}

	@Override
	protected EnumParticleTypes getParticle() {
		return EnumParticleTypes.FLAME;
	}

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.ITEM_FIRECHARGE_USE;
	}

	@Override
	protected SoundCategory getSoundCategory() {
		return SoundCategory.PLAYERS;
	}

	@Override
	protected float getParticleSpeed() {
		return 0.2F;
	}

	@Override
	protected float getDamage() {
		return 5;
	}

	@Override
	protected float getKnockbackHeight() {
		return 0.75F;
	}

}
