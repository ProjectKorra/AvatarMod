package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityFlameArc extends EntityArc<EntityFlameArc.FlameControlPoint> implements IGlowingEntity {

	private static final DataParameter<Float> SYNC_MAX_SIZE = EntityDataManager.createKey(EntityFlameArc.class, DataSerializers.FLOAT);

	public EntityFlameArc(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
		this.lightTnt = true;
		this.noClip = false;
		this.setsFires = false;
	}

	public float getMaxSize() {
		return dataManager.get(SYNC_MAX_SIZE);
	}

	public void setMaxSize(float size) {
		dataManager.set(SYNC_MAX_SIZE, size);
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
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {

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
	public boolean onCollideWithSolid() {
		if (collided && setsFires)
			setFires();
		return super.onCollideWithSolid();
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
	protected void updateCpBehavior() {
		getLeader().setPosition(position().plusY(height / 2));
		getLeader().setVelocity(velocity());

		// Move control points to follow leader

		for (int i = 1; i < points.size(); i++) {

			ControlPoint leader = points.get(i - 1);
			ControlPoint p = points.get(i);
			Vector leadPos = leader.position();
			double sqrDist = p.position().sqrDist(leadPos);

			if (sqrDist > getControlPointTeleportDistanceSq() && getControlPointTeleportDistanceSq() != -1) {

				Vector toFollowerDir = p.position().minus(leader.position()).normalize();

				double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
				if (idealDist > 1) idealDist -= 1; // Make sure there is some room

				Vector revisedOffset = leader.position().plus(toFollowerDir.times(idealDist));
				p.setPosition(revisedOffset);
				leader.setPosition(revisedOffset);
				p.setVelocity(Vector.ZERO);

			} else if (sqrDist > getControlPointMaxDistanceSq() && getControlPointMaxDistanceSq() != -1) {

				Vector diff = leader.position().minus(p.position());
				diff = diff.normalize().times(getVelocityMultiplier());
				p.setVelocity(p.velocity().plus(diff));

			}

		}
	}

	@Override
	public boolean onMinorWaterContact() {
		if (getTier() < 5) {
			spawnExtinguishIndicators();
			setDead();
			return true;
		}
		spawnExtinguishIndicators();
		return true;

	}

	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		setDead();
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (world.isRemote && getOwner() != null && ticksExisted % 2 == 0) {
			Vec3d[] points = new Vec3d[getAmountOfControlPoints()];
			for (int i = 0; i < points.length; i++)
				points[i] = getControlPoint(i).position().toMinecraft();
			//Particles! Let's do this.
			//First, we need a bezier curve. Joy.
			//Iterate through all of the control points.
			//0 is the leader/front one
			for (int i = 0; i < getAmountOfControlPoints(); i++) {
				Vec3d pos = getControlPoint(points.length - i - 1).position().toMinecraft();
				Vec3d pos2 = i < points.length - 1 ? getControlPoint(Math.max(points.length - i - 2, 0)).position().toMinecraft() : Vec3d.ZERO;

				if (i < points.length - 1) {
					for (int h = 0; h < 6; h++) {
						pos = pos.add(AvatarUtils.bezierCurve(((points.length - i - 1D / (h + 1)) / points.length), points));

						//Flow animation
						pos2 = pos2.add(AvatarUtils.bezierCurve(Math.min((((i + 1) / (h + 1D)) / points.length), 1), points));
						Vec3d circlePos = Vector.getOrthogonalVector(getLookVec(), (ticksExisted % 360) * 20 + h * 60, getAvgSize() / 2F).toMinecraft().add(pos);
						Vec3d targetPos = i < points.length - 1 ? Vector.getOrthogonalVector(getLookVec(),
								(ticksExisted % 360) * 20 + h * 60 + 20, getAvgSize()).toMinecraft().add(pos2)
								: Vec3d.ZERO;
						Vec3d vel = new Vec3d(world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240);

						if (targetPos != circlePos)
							vel = targetPos == Vec3d.ZERO ? vel : targetPos.subtract(circlePos).normalize().scale(0.2).add(vel);
						ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(circlePos).spawnEntity(this).vel(vel)
								.clr(0, 102, 255, 175).scale(0.675F).target(targetPos == Vec3d.ZERO ? pos : targetPos)
								.time(8 + AvatarUtils.getRandomNumberInRange(0, 5)).collide(true).element(new Firebending()).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(circlePos).spawnEntity(this).vel(vel)
								.clr(0, 102, 255, 175).scale(0.675F).target(targetPos == Vec3d.ZERO ? pos : targetPos)
								.time(8 + AvatarUtils.getRandomNumberInRange(0, 5)).collide(true).element(new Firebending()).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(circlePos).spawnEntity(this).vel(vel)
								.clr(255, 15, 5, 140).scale(0.675F).target(targetPos == Vec3d.ZERO ? pos : targetPos)
								.time(8 + AvatarUtils.getRandomNumberInRange(0, 5)).collide(true).element(new Firebending()).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(circlePos).spawnEntity(this).vel(vel)
								.clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10, 180).scale(0.675F).target(targetPos == Vec3d.ZERO ? pos : targetPos)
								.time(8 + AvatarUtils.getRandomNumberInRange(0, 5)).collide(true).element(new Firebending()).spawn(world);

					}

					//Particles along the line
					for (int h = 0; h < 1; h++) {
						pos = pos.add(AvatarUtils.bezierCurve(((points.length - i - 1D / (h + 1)) / points.length), points));
						pos2 = pos2.add(AvatarUtils.bezierCurve(Math.min((((i + 1) / (h + 1D)) / points.length), 1), points));

						Vec3d vel = new Vec3d(world.rand.nextGaussian() / 120, world.rand.nextGaussian() / 120, world.rand.nextGaussian() / 120);
						vel = vel.add(pos2.subtract(pos).normalize().scale(0.2));

						ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(pos).spawnEntity(this).vel(vel).clr(0, 102, 255, 255)
								.time(14 + AvatarUtils.getRandomNumberInRange(0, 5)).target(pos).collide(true).element(new Firebending()).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(pos).spawnEntity(this).vel(vel).clr(0, 102, 255, 255)
								.time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).target(pos).collide(true).element(new Firebending()).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).spawnEntity(this).vel(vel).clr(255, 15, 5, 180)
								.time(14 + AvatarUtils.getRandomNumberInRange(0, 5)).target(pos).collide(true).element(new Firebending()).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).spawnEntity(this).vel(vel).clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10,
								180).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).target(pos).collide(true).element(new Firebending()).spawn(world);

					}
				}
			}

		}
	}


	@Override
	public double getExpandedHitboxWidth() {
		return getAvgSize() / 2;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return getAvgSize() / 2;
	}


	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFlamethrowerDamage(target, getOwner());
	}

	public void shouldLightFires(boolean lightFires) {
		this.setsFires = lightFires;
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

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_MAX_SIZE, 2.0F);
	}

	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(1f, 0f, 0f, 1f).radius(10f).build();
	}

	static class FlameControlPoint extends ControlPoint {

		private FlameControlPoint(EntityFlameArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}

	}
}
