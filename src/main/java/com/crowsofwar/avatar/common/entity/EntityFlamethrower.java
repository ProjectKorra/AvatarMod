package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityFlamethrower extends EntityOffensive {

	private Vec3d knockback = new Vec3d(0, 0, 0);
	//Used for particle maths.
	private Vec3d spawnPos = Vec3d.ZERO;
	private double range = 0;
	private float hitboxWidth, hitboxHeight;

	public EntityFlamethrower(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
		this.lightTnt = true;
		this.noClip = true;
	}

	@Override
	public void setPosition(Vec3d position) {
		super.setPosition(position);
		this.spawnPos = position;
	}

	@Override
	public void setPosition(Vector position) {
		super.setPosition(position);
		this.spawnPos = position.toMinecraft();
	}

	@Override
	public boolean isPiercing() {
		return true;
	}

	@Override
	public boolean shouldDissipate() {
		return true;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {
		if (world.isRemote) {
			for (double i = 0; i < hitboxWidth; i += 0.25) {
				Random random = new Random();
				//We want the previous pos in order to provide proper collision
				double prevposx = posX - motionX / 2, prevposy = posY - motionY / 2, prevposz = posZ - motionZ / 2;
				double xPos = prevposx - hitboxWidth, yPos = prevposy - hitboxHeight, zPos = prevposz - hitboxWidth;
				double xPos1 = prevposx + hitboxWidth, yPos1 = prevposy + hitboxHeight, zPos1 = prevposz + hitboxWidth;
				AxisAlignedBB boundingBox = new AxisAlignedBB(xPos, yPos, zPos, xPos1, yPos1, zPos1);
				double spawnX = boundingBox.minX + random.nextDouble() / 30 * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() / 30 * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() / 30 * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(motionX, motionY, motionZ).time(5 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(255, 10, 5)
						.scale(hitboxWidth).element(getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(motionX, motionY, motionZ).time(5 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 30), 10)
						.scale(hitboxWidth).element(getElement()).collide(true).spawn(world);
			}
		}
	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {
		//We don't need to spawn any
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onFireContact();
		if (entity instanceof EntityOffensive) {
			if (entity.getTier() < getTier()) {
				if (entity.getElement() instanceof Firebending) {
					((EntityOffensive) entity).Dissipate();
				}
			} else if (entity.getTier() == getTier()) {
				if (entity.velocity().magnitude() < velocity().magnitude()) {
					((EntityOffensive) entity).Dissipate();
				}
			}
		}
	}

	@Override
	public void applyPiercingCollision() {
		super.applyPiercingCollision();
	}

	//We don't want sounds playing
	@Nullable
	@Override
	public SoundEvent[] getSounds() {
		return null;
	}

	@Override
	public void playExplosionSounds(Entity entity) {

	}

	@Override
	public void playPiercingSounds(Entity entity) {

	}

	@Override
	public void playDissipateSounds(Entity entity) {

	}


	@Override
	public void onUpdate() {
		super.onUpdate();
		motionX *= 0.99;
		motionY *= 0.99;
		motionZ *= 0.99;

		if (velocity().sqrMagnitude() <= 0.5 * 0.5)
			Dissipate();

		hitboxWidth *= 1.055;
		hitboxHeight *= 1.055;

		if (world.isRemote) {
			for (double i = 0; i < hitboxWidth; i += 0.3) {
				Random random = new Random();
				AxisAlignedBB boundingBox = getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(10 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(255, 10, 5)
						.scale(hitboxWidth / 1.5F).element(getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(10 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 30), 10)
						.scale(hitboxWidth / 1.5F).element(getElement()).collide(true).spawn(world);
			}
		}
	}


	@Override
	public double getExpandedHitboxWidth() {
		return hitboxWidth * 0.95;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return hitboxHeight * 0.95;
	}


	@Override
	public Vec3d getKnockback() {
		return knockback;
	}

	public void setKnockback(Vec3d knockback) {
		this.knockback = knockback;
	}

	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFlamethrowerDamage(target, getOwner());
	}

	public void shouldLightFires(boolean lightFires) {
		this.setsFires = lightFires;
	}

	public void setExpandedHitbox(float width, float height) {
		this.hitboxHeight = height;
		this.hitboxWidth = width;
	}

	public void setRange(float range) {
		this.range = range;
	}


	@Override
	public boolean isProjectile() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}
}
