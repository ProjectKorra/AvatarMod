package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import org.joml.SimplexNoise;

/**
 * Same as LightningChargeHandler, but handles lightning redirection
 *
 * @author CrowsOfWar
 */
public class LightningRedirectHandler extends LightningTickHandler {

	@Override
	public boolean tick(BendingContext ctx) {

		applyShakiness(ctx.getBenderEntity());
		return super.tick(ctx);

	}

	private void applyShakiness(EntityLivingBase entity) {

		float ticks = entity.ticksExisted;
		float modPitch = SimplexNoise.noise(ticks / 25f, 0);
		float modYaw = SimplexNoise.noise(ticks / 25f, 1000);

		entity.rotationYaw += modYaw * 4;
		entity.rotationPitch += modPitch * 4;

	}

}
