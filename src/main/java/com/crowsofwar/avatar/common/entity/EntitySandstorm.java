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

		// Rotates the entity around this sandstorm
		// First: calculates current angle, and the next angle
		// Then, calculates position with that next angle
		// Finally, finds a velocity which will move towards that point

		double currentAngle = Vector.getRotationTo(position(), Vector.getEntityPos(entity)).y();
		double nextAngle = currentAngle + Math.toRadians(360 / 20);

		Vector nextPos = position().plus(Vector.toRectangular(nextAngle, 0)).plusY(2);
		Vector delta = nextPos.minus(Vector.getEntityPos(entity));

		Vector nextVelocity = velocity().plus(delta.times(20));
		entity.setVelocity(nextVelocity.x() / 20, nextVelocity.y() / 20, nextVelocity.z() / 20);

		AvatarUtils.afterVelocityAdded(entity);

	}

	public SandstormMovementHandler getMovementHandler() {
		return movementHandler;
	}


}
