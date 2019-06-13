package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Objects;

import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.toRadians;

public class SlipstreamAirWalkHandler extends TickHandler {
	public SlipstreamAirWalkHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData bD = ctx.getData();
		Chi chi = bD.chi();
		boolean hasModifier = Objects.requireNonNull(bD.getPowerRatingManager(Airbending.ID)).hasModifier(SlipstreamPowerModifier.class);
		if (hasModifier) {
			boolean hasChi = chi.getTotalChi() > 0 && chi.getAvailableChi() > 0;
			boolean isCreative = (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative());
			if (hasChi || isCreative) {
				double targetSpeed = 1.2;
				targetSpeed += ctx.getBender().calcPowerRating(Airbending.ID) / 400;
				if (entity.moveForward != 0) {
					if (entity.moveForward < 0) {
						targetSpeed /= 2;
					} else {
						targetSpeed *= 1.3;
					}
				}
				entity.setPosition(entity.posX, entity.posY, entity.posZ);
				Vector currentVelocity = new Vector(entity.motionX, entity.motionY, entity.motionZ);
				Vector targetVelocity = toRectangular(toRadians(entity.rotationYaw), 0).times(targetSpeed);

				double targetWeight = 0.1;
				currentVelocity = currentVelocity.times(1 - targetWeight);
				targetVelocity = targetVelocity.times(targetWeight);

				double targetSpeedWeight = 0.2;
				double speed = currentVelocity.magnitude() * (1 - targetSpeedWeight)
						+ targetSpeed * targetSpeedWeight;

				Vector newVelocity = currentVelocity.plus(targetVelocity).normalize().times(speed);

				Vector playerMovement = toRectangular(toRadians(entity.rotationYaw - 90),
						toRadians(entity.rotationPitch)).times(entity.moveStrafing * 0.02);

				newVelocity = newVelocity.plus(playerMovement);

				entity.motionX = newVelocity.x();
				entity.motionY = 0;
				entity.motionZ = newVelocity.z();
				if (entity.ticksExisted % 5 == 0 && !isCreative) {
					chi.setAvailableChi(chi.getAvailableChi() - 1);
				}
			}

		}
		return hasModifier;
	}
}
