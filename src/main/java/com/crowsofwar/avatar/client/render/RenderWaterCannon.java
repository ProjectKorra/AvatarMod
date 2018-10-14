package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.ControlPoint;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class RenderWaterCannon extends RenderArc {
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft",
			"textures/blocks/water_overlay.png");


	public RenderWaterCannon(RenderManager renderManager) {
		super(renderManager, true);
	}

	@Override
	public void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
						 float partialTicks) {

		EntityWaterCannon cannon = (EntityWaterCannon) entity;
		renderArc(cannon, partialTicks, 3f, 3f * cannon.getSizeMultiplier());

	}

	@Override
	protected void renderArc(EntityArc<?> arc, float partialTicks, float alpha, float scale) {
		super.renderArc(arc, partialTicks, alpha, scale);
	}

	@Override
	protected void onDrawSegment(EntityArc arc, ControlPoint first, ControlPoint second) {
		// Parametric equation

		Vector from = new Vector(0, 0, 0);
		Vector to = second.position().minus(first.position());
		Vector diff = to.minus(from);
		Vector offset = first.position();
		Vector direction = diff.normalize();
		Vector spawnAt = offset.plus(direction.times(Math.random()));
		Vector velocity = first.velocity();
		arc.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, spawnAt.x(), spawnAt.y(), spawnAt.z(),
				velocity.x(), velocity.y(), velocity.z());
	}

	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}

}


