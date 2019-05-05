package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class WaterShootHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("6fdbbf2b-b7a8-4332-bd1f-6344687724bf");

	public WaterShootHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = ctx.getData().getAbilityData("water_cannon");
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		double powerRating = ctx.getBender().calcPowerRating(Waterbending.ID);
		int duration = data.getTickHandlerDuration(this);
		double speed = abilityData.getLevel() >= 1 ? 20 : 30;
		float damage;
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		float size;
		//Multiply by 1.5 to get water cannon size
		float ticks = 50;
		return false;
	}
}
