package com.crowsofwar.avatar.bending.bending.air.powermods;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.bending.bending.air.AbilitySlipstream;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
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

		if (data.getLevel() >= 2) {

			double invisibilityChance = 0.3;
			int invisiblityDuration = 30;

			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				invisibilityChance = 0.4;
				invisiblityDuration = 40;
			}

			// Intermittently grant invisibility
			if (entity.ticksExisted % 20 == 0) {
				// 40% chance per second for invisibility
				if (Math.random() < invisibilityChance) {
					PotionEffect effect = new PotionEffect(MobEffects.INVISIBILITY, invisiblityDuration, 0, false, false);
					entity.addPotionEffect(effect);
				}
			}

		}

		World world = entity.world;
		if (world.isRemote) {
			AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
			double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
			double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
			double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
					world.rand.nextGaussian() / 60).time(12).clr(0.95F, 0.95F, 0.95F, 0.1F)
					.scale((1.5F + Math.min(data.getLevel(), 0) / 2F) * 2).element(new Airbending()).spawn(world);

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

