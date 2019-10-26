package com.crowsofwar.avatar.common.bending.air.tickhandlers;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.air.powermods.SlipstreamPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;


public class SlipstreamCooldownHandler extends TickHandler {

	public SlipstreamCooldownHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
		int coolDown = 140;
		AbilityData aD = data.getAbilityData("slipstream");

		if (aD.getLevel() == 1) {
			coolDown = 120;
		}
		if (aD.getLevel() == 2) {
			coolDown = 100;
		}
		if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 110;
		}
		if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 90;
		}

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		if (data.getPowerRatingManager(Airbending.ID).hasModifier(SlipstreamPowerModifier.class))
			if (!entity.getActivePotionEffects().contains(new PotionEffect(MobEffects.INVISIBILITY))) {
				World world = entity.world;
				if (world.isRemote) {
					AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
					double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
					double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
					double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
							world.rand.nextGaussian() / 60).time(12).clr(0.8F, 0.8F, 0.8F)
							.scale((1.5F + Math.min(aD.getLevel(), 0) / 2F) * 2).spawn(world);
				}
			}


		return duration >= coolDown;
	}
}
