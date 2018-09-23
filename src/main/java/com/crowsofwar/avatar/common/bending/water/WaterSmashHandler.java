package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.SmashGroundHandler;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;

public class WaterSmashHandler extends SmashGroundHandler {

	public static TickHandler SMASH_GROUND_WATER = TickHandlerController.fromId(TickHandlerController.SMASH_GROUND_WATER_ID);

	public WaterSmashHandler(int id) {
		super(id);
	}

	@Override
	protected int getParticleAmount() {
		return 8;
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
		return 0.75F;
	}

	@Override
	protected float getDamage() {
		return 4;
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
