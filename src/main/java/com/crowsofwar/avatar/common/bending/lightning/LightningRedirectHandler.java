package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BenderInfo;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.joml.SimplexNoise;

import javax.annotation.Nullable;

/**
 * Same as LightningCreateHandler, but handles lightning redirection
 *
 * @author CrowsOfWar
 */
public class LightningRedirectHandler extends LightningChargeHandler {

	public LightningRedirectHandler(int id) {
		super(id);
	}

	@Override
	@Nullable
	protected AbilityData getLightningData(BendingContext ctx) {

		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		BenderInfo originalShooter = data.getMiscData().getRedirectionSource();

		if (originalShooter.find(world) == null) {
			return null;
		}

		// No nullable warning here needed because if the originalShooter entity is present (as
		// guaranteed above), BendingData#get won't return null
		//noinspection ConstantConditions
		return BendingData.get(world, originalShooter).getAbilityData("lightning_arc");

	}

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
