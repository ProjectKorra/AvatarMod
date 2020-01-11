package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.bending.air.powermods.SlipstreamPowerModifier;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.init.MobEffects;

public class RenderSlipstreamInvisibility extends RenderPlayer {

	public RenderSlipstreamInvisibility(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (Bender.isBenderSupported(entity)) {
			BendingData data = BendingData.getFromEntity(entity);
			if (data != null) {
				if (data.getPowerRatingManagers() != null && data.getPowerRatingManagers().contains(SlipstreamPowerModifier.class)) {
					if (!entity.isPotionActive(MobEffects.INVISIBILITY)) {
						super.doRender(entity, x, y, z, entityYaw, partialTicks);
					}
				}
				else super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
		}
	}
}
