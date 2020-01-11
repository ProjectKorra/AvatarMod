package com.crowsofwar.avatar.common.bending.water.tickhandlers;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.tickhandlers.SmashGroundHandler;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterSkate;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;

public class WaterSmashHandler extends SmashGroundHandler {

	public WaterSmashHandler(int id) {
		super(id);
	}

	@Override
	protected int getParticleAmount() {
		return 14;
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
		return 0.025F;
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
		return 5;
	}

	@Override
	protected DamageSource getDamageSource() {
		return AvatarDamageSource.WATER;
	}

	@Override
	protected Ability getAbility() {
		return new AbilityWaterSkate();
	}

	@Override
	protected BendingStyle getElement() {
		return new Waterbending();
	}
}
