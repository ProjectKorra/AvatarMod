package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class AbilityWaterCannon extends Ability{
	public AbilityWaterCannon() {
		super(Waterbending.ID, "water_cannon");
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();
		BendingData data = ctx.getData();

		boolean hasChi = bender.consumeChi(STATS_CONFIG.chiLightning);
		boolean hasWaterCharge = data.hasTickHandler(TickHandler.WATER_CHARGE);

		if (ctx.consumeWater(3)) {
			if (hasChi && !hasWaterCharge) {
				ctx.getData().addTickHandler(TickHandler.WATER_CHARGE);
			}
		}
	}

}

