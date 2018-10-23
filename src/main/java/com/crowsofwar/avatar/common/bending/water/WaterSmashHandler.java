package com.crowsofwar.avatar.common.bending.water;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.SmashGroundHandler;

public class WaterSmashHandler extends SmashGroundHandler {

	public WaterSmashHandler(int id) {
		super(id);
	}

	@Override
	protected int getParticleAmount() {
		return 10;
	}

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.ENTITY_GENERIC_SPLASH;
	}

	@Override
	protected EnumParticleTypes getParticle() {
		return EnumParticleTypes.WATER_WAKE;
	}

	@Override
	protected double getParticleSpeed() {
		return 3;
	}

	@Override
	protected float getDamage() {
		return 3.75F;
	}

	@Override
	protected float getKnockbackHeight() {
		return 0.1F;
	}

	@Override
	protected double getSpeed() {
		return 6;
	}

	@Override
	protected Ability getAbility() {
		return new AbilityWaterSkate();
	}
}
