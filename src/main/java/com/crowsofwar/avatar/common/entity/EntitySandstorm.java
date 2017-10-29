package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.SandstormMovementHandler;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySandstorm extends AvatarEntity {

	private final SandstormMovementHandler movementHandler;

	private boolean damageFlungTargets;
	private boolean damageContactingTargets;

	public EntitySandstorm(World world) {
		super(world);
		setSize(2.2f, 5.2f);
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

		// Number of blocks that the target "floats" above the ground
		final double floatingDistance = 2;
		// The maximum distance between a sandstorm and an orbiting mob before the mob is thrown
		final double maxPickupRange = 1.3;

		if (entity == getOwner()) {
			return;
		}

		// Rotates the entity around this sandstorm
		// First: calculates current angle, and the next angle
		// Then, calculates position with that next angle
		// Finally, finds a velocity which will move towards that point

		double currentAngle = Vector.getRotationTo(position(), Vector.getEntityPos(entity)).y();
		double nextAngle = currentAngle + Math.toRadians(360 / 20);

		double currentDistance = entity.getDistance(this.posX, this.posY + floatingDistance, this
				.posZ);
		double nextDistance = currentDistance + 0.01;

		// Prevent entities from orbiting too closely
		if (nextDistance < 0.8) {
			nextDistance = 0.8;
		}

		// Below conditions handle cases when entity was just picked up or needs to be flung off

		if (nextDistance > 2) {
			// Entities recently picked up typically have very large distances, over maxPickupRange
			// Bring them close to the center
			nextDistance = 0.8;
			onPickupEntity();
		} else if (nextDistance > maxPickupRange) {
			// If the distance is large, but not very large(>2), it has probably just been here
			// for a while
			// Fling entity to be far away quickly
			nextDistance = 3;
			// Fling in the current direction
			nextAngle = Vector.getRotationTo(Vector.ZERO, velocity()).y();
			onFlingEntity(entity);
		}

		Vector nextPos = position().plus(Vector.toRectangular(nextAngle, 0).times(nextDistance))
				.plusY(floatingDistance);
		Vector delta = nextPos.minus(Vector.getEntityPos(entity));

		Vector nextVelocity = velocity().plus(delta.times(20));
		entity.setVelocity(nextVelocity.x() / 20, nextVelocity.y() / 20, nextVelocity.z() / 20);

		AvatarUtils.afterVelocityAdded(entity);
		onContact(entity);

	}

	public SandstormMovementHandler getMovementHandler() {
		return movementHandler;
	}

	/**
	 * Called when an entity is picked up by the sandstorm
	 */
	private void onPickupEntity() {
		if (getOwner() != null) {
			AbilityData.get(getOwner(), "sandstorm").addXp(ConfigSkills.SKILLS_CONFIG.sandstormPickedUp);
		}
	}

	/**
	 * Called when the sandstorm "flings" an entity away after it's been orbiting for a while
	 */
	private void onFlingEntity(Entity entity) {
		if (!world.isRemote && damageFlungTargets) {
			// TODO Custom sandstorm DamageSource
			entity.attackEntityFrom(DamageSource.ANVIL, 5);
		}
	}

	/**
	 * Called when another entity is picked up and orbits the sandstorm
	 */
	private void onContact(Entity entity) {
		if (!world.isRemote && damageContactingTargets) {
			// TODO Custom sandstorm DamageSource
			entity.attackEntityFrom(DamageSource.ANVIL, 1);
		}
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	public boolean isDamageFlungTargets() {
		return damageFlungTargets;
	}

	public void setDamageFlungTargets(boolean damageFlungTargets) {
		this.damageFlungTargets = damageFlungTargets;
	}

	public boolean isDamageContactingTargets() {
		return damageContactingTargets;
	}

	public void setDamageContactingTargets(boolean damageContactingTargets) {
		this.damageContactingTargets = damageContactingTargets;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamageFlungTargets(nbt.getBoolean("DamageFlungTargets"));
		setDamageContactingTargets(nbt.getBoolean("DamageContactingTargets"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("DamageFlungTargets", isDamageFlungTargets());
		nbt.setBoolean("DamageContactingTargets", isDamageContactingTargets());
	}

}
