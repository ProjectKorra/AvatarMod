package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.SandstormMovementHandler;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntitySandstorm extends AvatarEntity {

	private final SandstormMovementHandler movementHandler;

	public EntitySandstorm(World world) {
		super(world);
		setSize(1.2f, 2.2f);
		movementHandler = new SandstormMovementHandler(this);
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

		Vector towardsSandstorm = this.position().minus(Vector.getEntityPos(entity));

		entity.addVelocity(acceleration.x() / 20, acceleration.y() / 20, acceleration.z() / 20);

	}

	public SandstormMovementHandler getMovementHandler() {
		return movementHandler;
	}


}
