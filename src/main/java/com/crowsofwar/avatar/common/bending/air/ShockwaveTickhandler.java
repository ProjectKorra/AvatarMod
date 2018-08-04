package com.crowsofwar.avatar.common.bending.air;

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

	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = ctx.getData().getAbilityData("air_shockwave");
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		double powerRating = ctx.getBender().calcPowerRating(Airbending.ID);
		int duration = data.getTickHandlerDuration(this);
		double speed = abilityData.getLevel() >= 1 ? 20 : 30;
		float damage = 0.5F;
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		float size;
		float ticks = 50;
		int durationToFire = 40;

		return false;
	}
}
