package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class ShockwaveTickhandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("f82d325c-9828-11e8-9eb6-529269fb1459");

	private Ability ability;
	private Bender bender;

	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = null;
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		bender = ctx.getBender();

		if (getAbility() != null && !world.isRemote) {
			abilityData = ctx.getData().getAbilityData(getAbility().getName());
		}

		double powerRating = getPowerRating();
		int duration = data.getTickHandlerDuration(this);
		float damage = getDamage();
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		float size = getStartingSize();
		float ticks = getTicks();
		int durationToFire = getDurationToFire();

		return false;
	}

	protected Ability getAbility() {
		return ability;
	}

	protected float getDamage() {
		return 1;
	}

	protected int getTicks() {
		return 30;
	}

	protected float getStartingSize() {
		return 0.25F;
	}

	protected double getPowerRating() {
		return bender.calcPowerRating(getBendingID());
	}

	protected UUID getBendingID() {
		return Airbending.ID;
		//Default
	}

	protected int getDurationToFire() {
		return 40;
	}

}
