package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.ControlPoint;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
		renderArc(cannon, partialTicks, 3f, 1.5f * cannon.getAvgSize());

		World world = entity.world;
		Vector position = cannon.position().plusY(cannon.height / 2);
		double radius = 1.5f * cannon.getAvgSize();
		/*if (cannon.getOwner() != null) {
			Vector eyePos = getEyePos(cannon.getOwner()).minusY(0.3);
			Vector directionToEnd = cannon.position().minus(eyePos).normalize();
			AvatarEntityUtils.setRotationFromPosition(cannon, eyePos.plus(directionToEnd.times(0.075)).toMinecraft());
			double pitch = cannon.rotationPitch;
			double yaw = cannon.rotationYaw;
			double dist = cannon.getDistance(cannon.getOwner());
			int maxAngle = 120 + (6 * (int) dist * (int) cannon.getSizeMultiplier());
			for (int angle = 0; angle < maxAngle; angle++) {
				double angle2 = world.rand.nextDouble() * Math.PI * 2;
				double x = radius * cos(angle);
				double y = angle / (maxAngle / dist);
				double z = radius * sin(angle);
				double speed = world.rand.nextDouble() * 2 + 1;
				double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
				angle2 += omega;
				Vector pos = new Vector(x, y, z);
				pos = Vector.rotateAroundAxisX(pos, pitch + 90);
				pos = Vector.rotateAroundAxisY(pos, yaw);
				world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, pos.x() + position.x(), pos.y() + position.y(),
						pos.z() + position.z(), 0.05 * radius * omega * Math.cos(angle2), 0.1, 0.05 * radius * omega * Math.sin(angle2));                //	World.spawnParticle(particle, pos.x() + position.x() + direction.x(), pos.y() + position.y() + direction.y(),
			}
		}**/

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


