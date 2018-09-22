package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.air.SmashGroundHandler;
import com.crowsofwar.avatar.common.data.TickHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class WaterSmashHandler extends SmashGroundHandler {

	public static TickHandler SMASH_GROUND_WATER = new WaterSmashHandler();

	/*@Override
	protected int getNumberOfParticles() {
		return 10;
	}**/

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.ENTITY_GENERIC_SPLASH;
	}

	/*@Override
	protected EnumParticleTypes getParticle() {
		return EnumParticleTypes.WATER_WAKE;
	}**/

	/*@Override
	protected float getParticleSpeed() {
		return 0.2F;
	}**/

	@Override
	protected float getDamage() {
		return 4;
	}

	@Override
	protected float getKnockbackHeight() {
		return 0.5F;
	}

	@Override
	protected double getSpeed() {
		return 6;
	}
}
