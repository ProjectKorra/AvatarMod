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

//		final double gravity = 2.5;
//		double distance = entity.getDistanceToEntity(this);
//
//		if (2 * gravity * distance - gravity * gravity <= 0) {
//			distance = 2 / gravity + 1;
//		}
//
//		double orbitalSpeed = Math.sqrt(2 * gravity * distance - gravity * gravity) * 2;
//
//		Vector gravityForce = this.position().minus(Vector.getEntityPos(entity)).times(orbitalSpeed);
//
//		acceleration = acceleration.plus(gravityForce);
//
//		entity.addVelocity(acceleration.x() / 20, acceleration.y() / 20, acceleration.z() / 20);
//
//		if (acceleration.sqrMagnitude() < 0.8) {
//			entity.setPosition(entity.posX + 1, entity.posY, entity.posZ);
//			entity.addVelocity(0, )
//		}

		double currentAngle = Vector.getRotationTo(position(), Vector.getEntityPos(entity)).y();
		double nextAngle = currentAngle + Math.toRadians(360 / 20);
//		System.out.println(currentAngle + " -> " + nextAngle);

		Vector myNextPos = position();
		Vector theirNextPos = myNextPos.plus(Vector.toRectangular(nextAngle, 0)).plusY(2);
		Vector delta = theirNextPos.minus(Vector.getEntityPos(entity));

		Vector theirNextVelocity = velocity();
		theirNextVelocity = theirNextVelocity.plus(delta.times(20));
		entity.setVelocity(theirNextVelocity.x() / 20, theirNextVelocity.y() / 20,
				theirNextVelocity.z
				() / 20);
//		entity.setPosition(nextPos.x(), nextPos.y(), nextPos.z());
//
//entity.motionY += 0.1;
//
		AvatarUtils.afterVelocityAdded(entity);
//
//		setVelocity(Vector.NORTH.times(3));
//		setVelocity(Vector.ZERO);

//		setDead();
	}

	public SandstormMovementHandler getMovementHandler() {
		return movementHandler;
	}


}
