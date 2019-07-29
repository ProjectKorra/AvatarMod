package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public interface IOffensiveEntity {
	/*public static void Explode(World world, AvatarEntity entity, EntityLivingBase owner) {
		if (world instanceof WorldServer) {
			if (owner != null) {
				WorldServer World = (WorldServer) world;
				World.spawnParticle(getParticle(), posX, posY, posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
				world.playSound(null, posX, posY, posZ, getSound(), getSoundCategory(), getVolume(),
						getPitch());
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(getExplosionHitboxGrowth(),
						getExplosionHitboxGrowth(), getExplosionHitboxGrowth()),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							attackEntity(entity, false);
							//Divide the result of the position difference to make entities fly
							//further the closer they are to the player.
							double dist = (getExplosionHitboxGrowth() - entity.getDistance(entity)) > 1 ? (getExplosionHitboxGrowth() - entity.getDistance(entity)) : 1;
							Vec3d velocity = entity.getPositionVector().subtract(this.getPositionVector());
							velocity = velocity.scale((1 / 40F)).scale(dist).add(0, getExplosionHitboxGrowth() / 50, 0);

							double x = velocity.x;
							double y = velocity.y > 0 ? velocity.z : 0.15F;
							double z = velocity.z;
							x *= getKnockbackMult().x;
							y *= getKnockbackMult().y;
							z *= getKnockbackMult().z;

							attackEntity(entity, true);

							if (!entity.world.isRemote) {
								entity.motionX += x;
								entity.motionY += y;
								entity.motionZ += z;
								entity.setFire(getFireTime());

								if (collided instanceof AvatarEntity) {
									if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment)
											&& !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
										AvatarEntity avent = (AvatarEntity) collided;
										avent.addVelocity(x, y, z);
									}
									entity.isAirBorne = true;
									AvatarUtils.afterVelocityAdded(entity);
								}
							}
						}
					}
				}

			}
		}
	}**/

}
