package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.SandstormMovementHandler;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntitySandstorm extends AvatarEntity {

	private final SandstormMovementHandler movementHandler;

	public EntitySandstorm(World world) {
		super(world);
		setSize(2.2f, 5.2f);
		movementHandler = new SandstormMovementHandler(this);
		noClip = true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
			movementHandler.update();
		}

		if (isCollided || ticksExisted >= 100) {
			setDead();
		}
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		return super.canCollideWith(entity) || entity instanceof EntityLivingBase;
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {

		// The overall acceleration for the target in m/s
		Vector acceleration = new Vector(0, 1, 0);

		/*
		Orbital mechanics:
		The sandstorm twirling targets in the air is basically orbital mechanics. The target has
		an existing velocity tangent to its orbit trajectory, and the only force acting on it is
		"gravity" towards the center of the sandstorm.

		Speed required to "orbit" is :: sqrt(2dg - g^2)
		where d = distance from sandstorm center, g = "gravity" towards center of sandstorm (m/s)
		 */

//		final double gravity = 1.5;
//		final double distance = entity.getDistanceToEntity(this);
//
//		double orbitalSpeed = Math.sqrt(2 * gravity * distance - gravity * gravity);
//
//		Vector gravityForce = this.position().minus(Vector.getEntityPos(entity)).times(orbitalSpeed);
//
//
//		entity.addVelocity(acceleration.x() / 20, acceleration.y() / 20, acceleration.z() / 20);

		double currentAngle = Vector.getRotationTo(position(), Vector.getEntityPos(entity)).y();
		double nextAngle = currentAngle + Math.toRadians(30);
//		System.out.println(currentAngle + " -> " + nextAngle);

		Vector nextPos = position().plus(Vector.toRectangular(nextAngle, 0).times
				(1)).plus(velocity().dividedBy(20));

//		Vector nextVelocity = nextPos.minus(Vector.getEntityPos(entity)).times(10);
//		entity.setVelocity(nextVelocity.x() / 20, nextVelocity.y() / 20 + 0.5, nextVelocity.z() /
//				20);
		entity.setPosition(nextPos.x(), nextPos.y(), nextPos.z());

entity.motionY += 0.1;

		AvatarUtils.afterVelocityAdded(entity);

//		setVelocity(Vector.ZERO);

	}

	public SandstormMovementHandler getMovementHandler() {
		return movementHandler;
	}


}
