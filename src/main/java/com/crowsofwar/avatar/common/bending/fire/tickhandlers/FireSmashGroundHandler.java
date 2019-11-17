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
package com.crowsofwar.avatar.common.bending.fire.tickhandlers;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.tickhandlers.SmashGroundHandler;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireJump;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;

/**
 * @author CrowsOfWar
 */
public class FireSmashGroundHandler extends SmashGroundHandler {

	public FireSmashGroundHandler(int id) {
		super(id);
	}

	@Override
	protected EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFire();
	}

	@Override
	protected int getParticleAmount() {
		return 6;
	}

	@Override
	protected int getPerformanceAmount() {
		return 7;
	}

	@Override
	protected double getRange() {
		return 3;
	}

	@Override
	protected double getParticleSpeed() {
		return 0.1125;
	}

	@Override
	protected DamageSource getDamageSource() {
		return AvatarDamageSource.FIRE;
	}

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.ITEM_FIRECHARGE_USE;
	}

	@Override
	protected float getKnockbackHeight() {
		return 0.075F;
	}

	@Override
	protected double getSpeed() {
		return 3.5;
	}

	@Override
	protected Ability getAbility() {
		return new AbilityFireJump();
	}

	@Override
	protected int fireTime() {
		return 5;
	}

	@Override
	protected BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	protected void smashEntity(EntityLivingBase entity) {
		Block currentBlock = entity.world.getBlockState(entity.getPosition()).getBlock();
		if (currentBlock != Blocks.WATER && currentBlock != Blocks.FLOWING_WATER) {
			super.smashEntity(entity);
		}
	}
}
