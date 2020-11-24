package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.air.powermods.SlipstreamPowerModifier;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Chi;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Objects;

public class SlipstreamAirWalkHandler extends TickHandler {
	private ParticleSpawner p;

	public SlipstreamAirWalkHandler(int id) {
		super(id);
		this.p = new NetworkParticleSpawner();
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData bD = ctx.getData();
		Chi chi = bD.chi();
		World world = ctx.getWorld();
		boolean hasModifier = Objects.requireNonNull(bD.getPowerRatingManager(Airbending.ID)).hasModifier(SlipstreamPowerModifier.class);
		if (hasModifier) {
			boolean hasChi = chi.getTotalChi() > 0 && chi.getAvailableChi() > 0;
			boolean isCreative = (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative());
			if (hasChi || isCreative) {
				if (entity.motionY < 0)
					entity.motionY *= 0;
				if (entity.getActivePotionEffect(MobEffects.INVISIBILITY) == null)
					p.spawnParticles(world, EnumParticleTypes.EXPLOSION_NORMAL, 1, 2, entity.posX, entity.getEntityBoundingBox().minY - 0.05, entity.posZ,
							0, 0, 0, true);
				if (entity.ticksExisted % 5 == 0 && !isCreative) {
					chi.setAvailableChi(chi.getAvailableChi() - 1);
				}
			}

		}
		return !hasModifier;
	}
}
