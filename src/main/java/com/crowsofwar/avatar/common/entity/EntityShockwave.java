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

	private EnumParticleTypes particle;
	//Particles to be spawned
	private int particleAmount;
	//The amount of particles to be spawned
	private double particleSpeed;
	//Speed of the particles
	private double range;
	//The range of the shockwave/how far it'll go before dissipating
	private double speed;
	//The speed of the shockwave and how fast entities will be knocked back
	private double knockbackHeight;
	//The amount entities will be knocked back
	private float damage;
	//The amount of damage the shockwave will do

	public void setParticle(EnumParticleTypes particle) {
		this.particle = particle;
	}

	public void setParticleAmount(int particleAmount) {
		this.particleAmount = particleAmount;
	}

	public void setParticleSpeed(double particleSpeed) {
		this.particleSpeed = particleSpeed;
	}

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






	public EntityShockwave(World world){
		super(world);
		this.damage = 1;
		this.particleSpeed = 0;
		this.particle = EnumParticleTypes.CLOUD;
		this.particleAmount = 1;
		this.range = 4;
		this.knockbackHeight = 0.4;
		this.speed = 0.8;
		this.setSize(1, 1);
	}

	@Override
	public void onUpdate() {

		this.setVelocity(Vector.ZERO);

		if (!world.isRemote) {
			// The further the shockwave is going to spread, the finer the angle increments.
			/*for (double angle = 0; angle < 4 * Math.PI; angle += Math.PI / (40 * 1.5)) {
				double x = this.posX < 0 ? (this.posX + ((this.ticksExisted * speed)) * Math.sin(angle))
						: 	(this.posX + ((this.ticksExisted * speed)) * Math.sin(angle));
				double y = (this.posY);
				double z = this.posZ < 0 ? (this.posZ + ((this.ticksExisted * speed)) * Math.cos(angle))
						: (this.posZ + ((this.ticksExisted * speed)) * Math.cos(angle));
				if (world instanceof WorldServer) {
					WorldServer World = (WorldServer) world;
					World.spawnParticle(particle, x, y, z, particleAmount, 0, 0, 0, particleSpeed);
				}


				}**/

				if (ticksExisted * speed > range) {
					this.setDead();
				}


			AxisAlignedBB box = new AxisAlignedBB(posX + (ticksExisted * speed), posY + (ticksExisted * speed), posZ + (ticksExisted * speed),
					posX - (ticksExisted * speed), posY - (ticksExisted * speed), posZ - (ticksExisted * speed));

			List<Entity> targets = world.getEntitiesWithinAABB(
					Entity.class, box);

			// In this particular instance, the caster is completely unaffected because they will always be in the
			// centre.
			targets.remove(getOwner());

			for (Entity target : targets) {
				if (target != getOwner() && this.canCollideWith(target) && target != this) {

					// Searches in a 1 wide ring.
					//if (this.getDistance(target) > (this.ticksExisted * speed) + 0.5 && target.posY < this.posY + 1
					//		&& target.posY > this.posY - 1) {


						if (this.canDamageEntity(target)) {
							target.attackEntityFrom(
									AvatarDamageSource.causeAirDamage(target, this.getOwner()),
									damage);
						}

						// All targets are thrown,
						target.motionX = Vector.getEntityPos(this).minus(Vector.getEntityPos(getOwner())).magnitude() * (range - ticksExisted * speed);
						target.motionY = knockbackHeight; // Throws target into the air.
						target.motionZ = Vector.getEntityPos(this).minus(Vector.getEntityPos(getOwner())).magnitude() * (range - ticksExisted * speed);

						AvatarUtils.afterVelocityAdded(target);
					//}
				}
			}
		}
	}
}
