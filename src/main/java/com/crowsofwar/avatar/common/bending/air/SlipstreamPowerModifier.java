package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BuffPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SlipstreamPowerModifier extends BuffPowerModifier {
	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(new AbilitySlipstream());

		double modifier = 20 + 8 * abilityData.getLevel();
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 70;
		}

		return modifier;

	}

	@Override
	public boolean onUpdate(BendingContext ctx) {

		AbilityData data = ctx.getData().getAbilityData("slipstream");
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData bD = ctx.getData();
		Chi chi = bD.chi();

		if (data.getLevel() >= 2) {

			double invisibilityChance = 0.3;
			int invisiblityDuration = 30;

			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				invisibilityChance = 0.4;
				invisiblityDuration = 40;
			}

			// Intermittently grant invisibility
			if (ctx.getBenderEntity().ticksExisted % 20 == 0) {
				// 40% chance per second for invisibility
				if (Math.random() < invisibilityChance) {
					PotionEffect effect = new PotionEffect(MobEffects.INVISIBILITY, invisiblityDuration, 0, false, false);
					ctx.getBenderEntity().addPotionEffect(effect);
				}
			}

		}
		if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			if (chi.getTotalChi() > 0 && chi.getAvailableChi() > 0 || (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())) {
			//	Vec3d pos = entity.getLookVec();
				BlockPos pos1 = entity.getPosition().down();
				//BlockPos pos2 = entity.getPosition().down().add(pos.x, 0, pos.z);
				//BlockPos pos3 = entity.getPosition().down().add(-pos.x, 0, -pos.z);
				IBlockState state = entity.world.getBlockState(pos1);
				//IBlockState state2 = entity.world.getBlockState(pos2);
				//IBlockState state3 = entity.world.getBlockState(pos3);
				/*if (state3.getBlock() == Blocks.BARRIER) {
					entity.world.setBlockToAir(pos3);
					entity.world.getBlockState(pos1).getBlock().
				}
				if (state.getBlock() == Blocks.AIR) {
					entity.world.setBlockState(pos1, Blocks.BARRIER.getDefaultState());
				}
				if (state2.getBlock() == Blocks.AIR) {
					entity.world.setBlockState(pos2,  Blocks.BARRIER.getDefaultState());
				}**/
				entity.setNoGravity(state.getBlock() instanceof BlockLiquid);
				if (entity.ticksExisted % 5 == 0 && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())) {
					chi.setAvailableChi(chi.getAvailableChi() - 1);
					if (chi.getAvailableChi() == 0)
						entity.setNoGravity(false);
				}
			} else {
				entity.setNoGravity(false);
			}
		}
		return super.onUpdate(ctx);
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		ctx.getBenderEntity().setNoGravity(false);
		super.onRemoval(ctx);
	}

	@Override
	protected Vision[] getVisions() {
		return new Vision[]{Vision.SLIPSTREAM_WEAK, Vision.SLIPSTREAM_MEDIUM,
				Vision.SLIPSTREAM_POWERFUL};
	}

	@Override
	protected String getAbilityName() {
		return "slipstream";
	}

}

