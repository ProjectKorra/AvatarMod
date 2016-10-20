package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.ControlPoint;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class RenderFireArc extends RenderArc {
	
	private static final ResourceLocation fire = new ResourceLocation("avatarmod",
			"textures/entity/fire-ribbon.png");
	
	public RenderFireArc(RenderManager renderManager) {
		super(renderManager);
		enableFullBrightness();
	}
	
	@Override
	protected ResourceLocation getTexture() {
		return fire;
	}
	
	@Override
	protected void onDrawSegment(EntityArc arc, ControlPoint first, ControlPoint second) {
		// Parametric equation
		// For parameters, they will be same as linear equation: y = mx+b
		
		Vector m = second.position().minus(first.position());
		Vector b = first.position();
		double x = Math.random(); // 0-1
		Vector spawnAt = m.times(x).plus(b);
		Vector velocity = new Vector(0, 0, 0);
		
		// TODO [1.10] Re-introduce flame particle
		arc.worldObj.spawnParticle(EnumParticleTypes.FLAME, spawnAt.x(), spawnAt.y(), spawnAt.z(),
				velocity.x() / 20, 0.05, velocity.z() / 20);
		// AvatarParticles.createParticle(arc.worldObj, spawnAt.xCoord, spawnAt.yCoord,
		// spawnAt.zCoord,
		// velocity.xCoord / 20, 0.05, velocity.zCoord / 20);
		
	}
	
}
