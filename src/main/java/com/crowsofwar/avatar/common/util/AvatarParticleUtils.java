package com.crowsofwar.avatar.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AvatarParticleUtils {


	//Always points to the right
	/*public static Vec3d orthogonal(Vec3d axis, double length, double angle, float rotationYaw, float rotationPitch) throws IllegalArgumentException {
		Validate.isTrue(!axis.equals(Vec3d.ZERO), "Axis vector cannot be zero!");
​
		double yaw = Math.toRadians(rotationYaw);
		Vec3d other = new Vec3d(-Math.sin(yaw), -axis.y + 0.1, Math.cos(yaw));
		other.crossProduct(axis).normalize().scale(length);
​
		return rotate(other, axis, angle);
	}
​
	public static Vec3d rotate(Vec3d rotator, Vec3d axis, double angle) {
		double radians = Math.toRadians(angle);
​
		if (Math.abs(angle / (2 * Math.PI)) < Vec3d.getEpsilon()) {
			return rotator;
		} else if (Math.abs(angle / Math.PI) < Vec3d.getEpsilon()) {
			return rotator.scale(-1);
		}
​
		return rotateAroundAxis(axis, angle);
	}**/


	public static Vec3d rotateAroundAxisX(Vec3d v, double angle) {
		angle = Math.toRadians(angle);
		double y, z, cos, sin;
		cos = cos(angle);
		sin = sin(angle);
		y = v.y * cos - v.z * sin;
		z = v.y * sin + v.z * cos;
		return new Vec3d(v.x, y, z);
	}

	public static Vec3d rotateAroundAxisY(Vec3d v, double angle) {
		angle = -angle;
		angle = Math.toRadians(angle);
		double x, z, cos, sin;
		cos = cos(angle);
		sin = sin(angle);
		x = v.x * cos + v.z * sin;
		z = v.x * -sin + v.z * cos;
		return new Vec3d(x, v.y, z);
	}

	public static Vec3d rotateAroundAxisZ(Vec3d v, double angle) {
		angle = Math.toRadians(angle);
		double x, y, cos, sin;
		cos = cos(angle);
		sin = sin(angle);
		x = v.x * cos - v.y * sin;
		y = v.x * sin + v.y * cos;
		return new Vec3d(x, y, v.z);
	}


	//NOTE: ONLY USE ENUMPARTICLETYPE SPAWN METHODS IN RENDERING FILES. DUE TO VANILLA'S WEIRD PARTICLE SPAWNING SYSTEM,
	//YOU CANNOT SPAWN PARTICLES IN ENTITY CLASSES AND SUCH RELIABLY. CUSTOM PARTICLES IN THIS CASE ARE FINE, THOUGH.
	public static void spawnDirectionalVortex(World world, EntityLivingBase entity, Vec3d direction, int particleAmount, double vortexLength, double minRadius, double radiusScale, EnumParticleTypes particle, double posX, double posY, double posZ,
											  double velX, double velY, double velZ) {
		for (int angle = 0; angle < particleAmount; angle++) {
			double radius = minRadius + (angle / radiusScale);
			//Why isn't this in radians
			double x = radius * cos(angle);
			double y = angle / (particleAmount / vortexLength);
			double z = radius * sin(angle);
			Vec3d pos = new Vec3d(x, y, z);
			if (entity != null && direction != null) {
				pos = rotateAroundAxisX(pos, entity.rotationPitch + 90);
				pos = rotateAroundAxisY(pos, entity.rotationYaw);
				world.spawnParticle(particle, true, pos.x + posX + direction.x, pos.y + posY + direction.y,
						pos.z + posZ + direction.z, velX, velY, velZ);
			} else {
				world.spawnParticle(particle, false, x + posX, y + posY,
						z + posZ, velX, velY, velZ);
			}
		}
	}


	/**
	 * Spawns a directional vortex that has rotating particles.
	 *
	 * @param world         World the vortex spawns in.
	 * @param entity        Entity that's spawning the vortex.
	 * @param direction     The direction that the vortex is spawning in. Although it's just used for proper positioning, use entity.getLookVec()
	 *                      or some other directional Vec3d.
	 * @param maxAngle      The amount of particles/the maximum angle that the circle ticks to. 240 would mean there are 240 particles spiraling away.
	 * @param vortexLength  How long the vortex is. This is initially used at the height, before rotating the vortex.
	 * @param radiusScale   The maximum radius and how much the radius increases by. Always use your value for the maxAngle here-
	 *                      otherwise you can get some funky effects. Ex: maxAngle / 1.5 would give you a max radius of 1.5 times your adius.
	 *                      Note: It might only be a diamater of 1.5 blocks- if so, uhhh... My bad.
	 * @param particle      The wizardry particle type. I had to create two methods- for for normal particles, one for wizardry ones.
	 * @param position      The starting/reference position of the vortex. Used along with the direction position to determine the actual starting position.
	 * @param particleSpeed How fast the particles are spinning. You don't need to include complex maths here- that's all handled by this method.
	 * @param entitySpeed   The speed of the entity that is rendering these particles. If the player/entity spawns a tornado, this is the speed of the tornado. If there's no entity,
	 *                      do Vec3d.ZERO.
	 */
	public static void spawnSpinningDirectionalVortex(World world, EntityLivingBase entity, Vec3d direction, int maxAngle, double vortexLength, double minRadius, double radiusScale, EnumParticleTypes particle, Vec3d position,
													  Vec3d particleSpeed, Vec3d entitySpeed) {
		for (int angle = 0; angle < maxAngle; angle++) {
			double angle2 = world.rand.nextDouble() * Math.PI * 2;
			double radius = minRadius + (angle / radiusScale);
			double x = radius * cos(angle);
			double y = angle / (maxAngle / vortexLength);
			double z = radius * sin(angle);
			double speed = world.rand.nextDouble() * 2 + 1;
			double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
			angle2 += omega;
			Vec3d pos = new Vec3d(x, y, z);
			if (entity != null && direction != null) {
				Vec3d pVel = new Vec3d(particleSpeed.x * radius * omega * cos(angle2), particleSpeed.y, particleSpeed.z * radius * omega * sin(angle2));
				pVel = rotateAroundAxisX(pVel, entity.rotationPitch - 90);
				pVel = rotateAroundAxisY(pVel, entity.rotationYaw);
				pos = rotateAroundAxisX(pos, entity.rotationPitch + 90);
				pos = rotateAroundAxisY(pos, entity.rotationYaw);
				world.spawnParticle(particle, true, pos.x + position.x + direction.x, pos.y + position.y + direction.y,
						pos.z + position.z + direction.z, pVel.x + entitySpeed.x, pVel.y + entitySpeed.y, pVel.z + entitySpeed.z);
			}
		}
	}

	/**
	 * Spawns a directional vortex that has rotating particles.
	 *
	 * @param world         World the vortex spawns in.
	 * @param maxAngle      The amount of particles/the maximum angle that the circle ticks to. 240 would mean there are 240 particles spiraling away.
	 * @param vortexHeight  How tall the vortex is.
	 * @param radiusScale   The maximum radius and how much the radius increases by. Always use your value for the maxAngle here-
	 *                      otherwise you can get some funky effects. Ex: maxAngle/1.5 would give you a max radius of 1.5 blocks.
	 *                      Note: It might only be a diamater of 1.5 blocks- if so, uhhh... My bad.
	 * @param particle      The wizardry particle type. I had to create two methods- for for normal particles, one for wizardry ones.
	 * @param position      The starting/reference position of the vortex. Used along with the direction position to determine the actual starting position.
	 * @param particleSpeed How fast the particles are spinning. You don't need to include complex maths here- that's all handled by this method.
	 * @param entitySpeed   The speed of the entity that is spawning the particles. If this is used for a quickburst, just make this 0. This is so
	 *                      particles move with the entity that's directly spawning it.
	 */

	public static void spawnSpinningVortex(World world, int maxAngle, double vortexHeight, double minRadius, double radiusScale, EnumParticleTypes particle, Vec3d position,
										   Vec3d particleSpeed, Vec3d entitySpeed) {
		for (int angle = 0; angle < maxAngle; angle++) {
			double angle2 = world.rand.nextDouble() * Math.PI * 2;
			double radius = minRadius + (angle / radiusScale);
			double x = radius * cos(angle);
			double y = angle / (maxAngle / vortexHeight);
			double z = radius * sin(angle);
			double speed = world.rand.nextDouble() * 2 + 1;
			double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
			angle2 += omega;
			world.spawnParticle(particle, false, x + position.x, y + position.y, z + position.z,
					(particleSpeed.x * radius * omega * cos(angle2)) + entitySpeed.x, particleSpeed.y + entitySpeed.y, (particleSpeed.z * radius * omega * sin(angle2)) + entitySpeed.z);

		}
	}


	public static void spawnDirectionalHelix(World world, Entity entity, Vec3d direction, int maxAngle, double vortexLength, double radius, EnumParticleTypes particle, Vec3d position,
											 Vec3d particleSpeed) {
		for (int angle = 0; angle < maxAngle; angle++) {
			double x = radius * cos(angle);
			double y = angle / (maxAngle / vortexLength);
			double z = radius * sin(angle);
			Vec3d pos = new Vec3d(x, y, z);
			if (entity != null && direction != null) {
				pos = rotateAroundAxisX(pos, entity.rotationPitch + 90);
				pos = rotateAroundAxisY(pos, entity.rotationYaw);
				world.spawnParticle(particle, true, pos.x + position.x + direction.x, pos.y + position.z + direction.y,
						pos.z + position.z + direction.z, particleSpeed.z, particleSpeed.y, particleSpeed.z);
			} else {
				world.spawnParticle(particle, false, x + position.x, y + position.y,
						z + position.z, particleSpeed.z, particleSpeed.y, particleSpeed.z);
			}
		}
	}


	public static void spawnSpinningDirectionalHelix(World world, Entity entity, Vec3d direction, Vec3d entitySpeed, int maxAngle, double vortexLength, double radius, EnumParticleTypes particle, Vec3d position,
													 Vec3d particleSpeed, int maxAge, float r, float g, float b) {
		for (int angle = 0; angle < maxAngle; angle++) {
			double angle2 = world.rand.nextDouble() * Math.PI * 2;
			double x = radius * cos(angle);
			double y = angle / (maxAngle / vortexLength);
			double z = radius * sin(angle);
			double speed = world.rand.nextDouble() * 2 + 1;
			double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
			angle2 += omega;
			Vec3d pos = new Vec3d(x, y, z);
			if (entity != null && direction != null) {
				Vec3d pVel = new Vec3d(particleSpeed.x * radius * omega * cos(angle2), particleSpeed.y, particleSpeed.z * radius * omega * sin(angle2));
				pVel = rotateAroundAxisX(pVel, entity.rotationPitch - 90);
				pos = rotateAroundAxisX(pos, entity.rotationPitch + 90);
				pos = rotateAroundAxisY(pos, entity.rotationYaw);
				world.spawnParticle(particle, true, pos.x + position.x + direction.x, pos.y + position.y + direction.y,
						pos.z + position.z + direction.z, pVel.x + entitySpeed.x, pVel.y + entitySpeed.y, pVel.z + entitySpeed.z);
			}
		}
	}


	public static void spawnSpinningHelix(World world, int maxAngle, double vortexLength, double radius, EnumParticleTypes particle, Vec3d position,
										  Vec3d particleSpeed, Vec3d entitySpeed) {
		for (int angle = 0; angle < maxAngle; angle++) {
			double angle2 = world.rand.nextDouble() * Math.PI * 2;
			double x = radius * cos(angle);
			double y = angle / (maxAngle / vortexLength);
			double z = radius * sin(angle);
			double speed = world.rand.nextDouble() * 2 + 1;
			double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
			angle2 += omega;
			world.spawnParticle(particle, x + position.x, y + position.y,
					z + position.z, (particleSpeed.x * radius * omega * cos(angle2)) + entitySpeed.x, particleSpeed.y + entitySpeed.y, (particleSpeed.z * radius * omega * sin(angle2)) + entitySpeed.z);
		}
	}


	public static Vec3d getVectorForRotation(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d(f1 * f2, f3, f * f2);
	}

	public static Vec3d getDirectionalVortexEndPos(EntityLivingBase entity, Vec3d direction, int maxAngle, double vortexLength, double radiusScale, double posX, double posY, double posZ) {
		double radius = maxAngle / radiusScale;
		double x = radius * cos(maxAngle);
		double z = radius * sin(maxAngle);
		Vec3d pos = new Vec3d(x, vortexLength, z);
		if (entity != null && direction != null) {
			pos = rotateAroundAxisX(pos, entity.rotationPitch + 90);
			pos = rotateAroundAxisY(pos, entity.rotationYaw);
			return new Vec3d(pos.x + posX + direction.x, pos.y + posY + direction.y,
					pos.z + posZ + direction.z);
		} else {
			return new Vec3d(x + posX, vortexLength + posY, z + posZ);
		}
	}

}
