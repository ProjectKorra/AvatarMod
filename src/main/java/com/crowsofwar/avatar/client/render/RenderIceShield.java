package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityIceShield;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class RenderIceShield extends Render<EntityIceShield> {

	protected RenderIceShield(RenderManager renderManager) {
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityIceShield entity) {
		return null;
	}

}
