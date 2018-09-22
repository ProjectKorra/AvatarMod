package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class EntityShockwave extends AvatarEntity {

	public EnumParticleTypes particle;
	//Particles to be spawned.This is actually an inverse amount; the bigger the number,
	// the less particles there are.
	public int particleAmount;
	//The amount of particles to be spawned
	public double particleSpeed;
	//Speed of the particles

	//As particles are called when the thing spawns in the render file, you can't set the particles to something and have
	//the render file spawn them. It has to be called in the world constructor, so you need multiple entity shockwaves


	private double range;
	//The range of the shockwave/how far it'll go before dissipating
	public double speed;
	//The speed of the shockwave and how fast entities will be knocked back
	private double knockbackHeight;
	//The amount entities will be knocked back
	public float damage;
	//The amount of damage the shockwave will do
	public boolean isFire;
	//Whether or not to set the target entities on fire
	public int fireTime;
	//How long to set the target entity on fire
	public boolean isSphere;
	//Whether or not to use a sphere of particles instead of a circular ring

	public void setRange(double range) {
		this.range = range;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setKnockbackHeight(double height) {
		this.knockbackHeight = height;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public void setFire(boolean fire) {
		this.isFire = fire;
	}

	public void setFireTime(int time) {
		this.fireTime = time;
	}

	public void setSphere(boolean sphere) {
		this.isSphere = sphere;
	}

	public EnumParticleTypes getParticle() {
		return particle;
	}

	public int getParticleAmount() {
		return particleAmount;
	}

	public double getParticleSpeed() {
		return particleSpeed;
	}

	public double getRange() {
		return range;
	}

	public double getSpeed() {
		return speed;
	}

	public double getKnockbackHeight() {
		return knockbackHeight;
	}

	public double getDamage() {
		return damage;
	}

	public boolean getSphere() {
		return isSphere;
	}






	public EntityShockwave(World world){
		super(world);
		this.damage = 1;
		this.particle = EnumParticleTypes.CLOUD;
		this.particleSpeed = 0;
		this.particleAmount = 10;
		this.range = 4;
		this.knockbackHeight = 0.2;
		this.speed = 0.8;
		this.isFire = false;
		this.fireTime = 0;
		this.setSize(1, 1);
	}

	@Override
	public void onUpdate() {

		this.setVelocity(Vector.ZERO);
		if (getOwner() != null) {
			this.setPosition(getOwner().posX, getOwner().getEntityBoundingBox().minY, getOwner().posZ);
		}

		if (!world.isRemote) {

				if (ticksExisted * speed > range) {
					this.setDead();
				}


			AxisAlignedBB box = new AxisAlignedBB(posX + (ticksExisted * speed), posY + 1.5, posZ + (ticksExisted * speed),
					posX - (ticksExisted * speed), posY - 1.5, posZ - (ticksExisted * speed));

			List<Entity> targets = world.getEntitiesWithinAABB(
					Entity.class, box);

			targets.remove(getOwner());

			for (Entity target : targets) {
				if (target != getOwner() && this.canCollideWith(target) && target != this) {

						if (this.canDamageEntity(target)) {
							target.attackEntityFrom(
									AvatarDamageSource.causeAirDamage(target, this.getOwner()),
									damage);
						}
						target.setFire(isFire ? fireTime : 0);
						target.motionX += Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).x() * (range - ticksExisted/20F * speed);
						target.motionY += knockbackHeight; // Throws target into the air.
						target.motionZ += Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).z() * (range - ticksExisted/20F * speed);

						AvatarUtils.afterVelocityAdded(target);
				}
			}
		}
	}
}
