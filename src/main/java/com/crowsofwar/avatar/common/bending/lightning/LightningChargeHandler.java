package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class LightningChargeHandler extends TickHandler {

	@Override
	public boolean tick(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();

		int duration = data.getTickHandlerDuration(this);

		if (duration >= 40) {
			fireLightning(world, entity);
			return true;
		}

		return false;

	}

	private void fireLightning(World world, EntityLivingBase entity) {
		float[] turbulenceValues = {0.6f, 1.2f};

		for (float turbulence : turbulenceValues) {

			EntityLightningArc lightning = new EntityLightningArc(world);
			lightning.setOwner(entity);
			lightning.setTurbulence(turbulence);

			lightning.setPosition(Vector.getEyePos(entity));
			lightning.setEndPos(Vector.getEyePos(entity));

			Vector velocity = Vector.getLookRectangular(entity);
			velocity = velocity.normalize().times(30);
			lightning.setVelocity(velocity);

			world.spawnEntity(lightning);

		}
	}

}
