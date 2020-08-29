package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_AIR_BURST;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_AIR_BURST;

public class AbilityAirBurst extends Ability {

	public static final String
			PULL_ENEMIES = "pullsEnemies",
			BLINDNESS_LEVEL = "blindnessLevel",
			BLINDNESS_DURATION = "blindnessDuration",
			SLOWNESS_LEVEL = "slownessLevel",
			SLOWNESS_DURATION = "slownessDuration",
			WEAKNESS_LEVEL = "weaknessLevel",
			WEAKNESS_DURATION = "weaknessDuration",
			BLAST_LEVEL = "blastLevel",
			SLOW_MULT = "slowMult";

	public AbilityAirBurst() {
		super(Airbending.ID, "air_burst");
	}

	@Override
	public void init() {
		super.init();
		addProperties(BLINDNESS_LEVEL, BLINDNESS_DURATION, SLOWNESS_LEVEL, SLOWNESS_DURATION, WEAKNESS_LEVEL, WEAKNESS_DURATION,
				BLAST_LEVEL, EFFECT_RADIUS, EFFECT_DAMAGE, SLOW_MULT);
		addBooleanProperties(PULL_ENEMIES, POTION_EFFECTS);
	}

	@Override
	public void execute(AbilityContext ctx) {
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();

		//The charge status control adds the release status control, but the release status control doesn't activate until the right click button is released.

		boolean hasAirCharge = data.hasStatusControl(RELEASE_AIR_BURST);



		if (bender.consumeChi(getChiCost(ctx) / 4) && !hasAirCharge) {
			data.addStatusControl(CHARGE_AIR_BURST);
		} else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			if (!hasAirCharge) {
				data.addStatusControl(CHARGE_AIR_BURST);
			}
		}
		super.execute(ctx);
	}

	@Override
	public int getBaseTier() {
		return 4;
	}

	@Override
	public boolean isChargeable() {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getBurnOut(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getExhaustion(AbilityContext ctx) {
		return 0;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirBurst(this, entity, bender);
	}
}
