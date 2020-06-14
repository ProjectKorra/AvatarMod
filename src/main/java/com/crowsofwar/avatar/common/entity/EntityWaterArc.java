/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleWater;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterArc;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControlController;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityWaterArc extends EntityArc<EntityWaterArc.WaterControlPoint> implements IShieldEntity {

	private static final DataParameter<WaterArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWaterArc.class, WaterArcBehavior.DATA_SERIALIZER);

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager
			.createKey(EntityWaterArc.class, DataSerializers.FLOAT);

	private static final DataParameter<Float> SYNC_GRAVITY = EntityDataManager
			.createKey(EntityWaterArc.class, DataSerializers.FLOAT);
	private final float Size;
	/**
	 * The amount of ticks since last played splash sound. -1 for splashable.
	 */
	private int lastPlayedSplash;
	private boolean isSpear;
	private float damageMult;
	private float velocityMultiplier;


	public EntityWaterArc(World world) {
		super(world);
		this.Size = 0.4F;
		setSize(Size, Size);
		this.lastPlayedSplash = -1;
		this.damageMult = 1;
		this.putsOutFires = true;
	}

	public float getDamageMult() {
		return damageMult;
	}

	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}

	public void isSpear(boolean isSpear) {
		this.isSpear = isSpear;
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public float getGravity() {
		return dataManager.get(SYNC_GRAVITY);
	}

	public void setGravity(float gravity) {
		dataManager.set(SYNC_GRAVITY, gravity);
	}


	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new WaterArcBehavior.Idle());
		dataManager.register(SYNC_SIZE, Size);
		dataManager.register(SYNC_GRAVITY, 9.82F);
	}


	@Override
	public void spawnExplosionParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {

	}

	@Nullable
	@Override
	public SoundEvent[] getSounds() {
		SoundEvent[] events = new SoundEvent[1];
		events[0] = SoundEvents.ENTITY_GENERIC_SPLASH;
		return events;
	}

	@Override
	public boolean onCollideWithSolid() {
		if (!world.getCollisionBoxes(this, getExpandedHitbox()).isEmpty() && collided) {
			if (!world.isRemote && getBehavior() instanceof WaterArcBehavior.Thrown && !isSpear) {
				if (world.isRemote) {
					Random random = new Random();

					double xVel, yVel, zVel;
					double offX, offY, offZ;

					if (collidedVertically) {

						xVel = 5;
						yVel = 3.5;
						zVel = 5;
						offX = 0;
						offY = 0.6;
						offZ = 0;

					} else {

						xVel = 7;
						yVel = 2;
						zVel = 7;
						offX = 0.6;
						offY = 0.2;
						offZ = 0.6;

					}

					xVel *= 0.0;
					yVel *= 0.0;
					zVel *= 0.0;

					int particles = random.nextInt(3) + 4;
					for (int i = 0; i < particles; i++) {

						world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX + random.nextGaussian() * offX,
								posY + random.nextGaussian() * offY + 0.2, posZ + random.nextGaussian() * offZ,
								random.nextGaussian() * xVel, random.nextGaussian() * yVel,
								random.nextGaussian() * zVel);

					}

				}
			}

			return true;
		}
		return false;

	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		if (entity.getTier() <= getTier())
			entity.onMajorWaterContact();
		else entity.onMinorWaterContact();
	}

	@Override
	public void setDead() {
		super.setDead();
		cleanup();
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		if (lastPlayedSplash > -1) {
			lastPlayedSplash++;
			if (lastPlayedSplash > 20) lastPlayedSplash = -1;
		}

		WaterArcBehavior behavior = getBehavior();
		WaterArcBehavior next = (WaterArcBehavior) behavior.onUpdate(this);
		if (next != behavior) {
			setBehavior(next);
		}

		if (getOwner() == null) {
			this.setDead();
		}
		setEntitySize(getSize() / 2);

		if (getBehavior() != null && getBehavior() instanceof WaterArcBehavior.PlayerControlled) {
			this.velocityMultiplier = 4;
		}

		if (getAbility() instanceof AbilityWaterArc && !world.isRemote && getOwner() != null) {
			if (getBehavior() != null && getBehavior() instanceof WaterArcBehavior.Thrown) {
				AbilityData aD = AbilityData.get(getOwner(), "water_arc");
				int lvl = aD.getLevel();
				this.velocityMultiplier = lvl >= 1 ? 8 + (2 * lvl) : 8;
			}
		} else if (getBehavior() != null && getBehavior() instanceof WaterArcBehavior.Thrown) {
			this.velocityMultiplier = 8;
		}


		if (getOwner() != null) {
			EntityWaterArc arc = AvatarEntity.lookupControlledEntity(world, EntityWaterArc.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (arc == null && bD.hasStatusControl(StatusControlController.THROW_WATER)) {
				bD.removeStatusControl(StatusControlController.THROW_WATER);
			}
			if (arc != null && arc.getBehavior() instanceof WaterArcBehavior.PlayerControlled && !(bD.hasStatusControl(StatusControlController.THROW_WATER))) {
				bD.addStatusControl(StatusControlController.THROW_WATER);
			}
		}

		if (world.isRemote && getOwner() != null) {
			Vec3d[] points = new Vec3d[getAmountOfControlPoints()];
			for (int i = 0; i < getAmountOfControlPoints(); i++)
				points[i] = getControlPoint(i).position().toMinecraft();
			//Particles! Let's do this.
			//First, we need a bezier curve. Joy.
			//Iterate through all of the control points.
			for (int i = 0; i < getAmountOfControlPoints(); i++) {
				Vec3d pos = AvatarUtils.bezierCurve(((double) (getAmountOfControlPoints() - i) / (double) getAmountOfControlPoints()), points);
				Vec3d pos2 = i < getAmountOfControlPoints() - 1 ? AvatarUtils.bezierCurve(
						Math.min(((double) (i + 1) / getAmountOfControlPoints()), 1), points):
						Vec3d.ZERO;
				pos = pos.add(getControlPoint(getAmountOfControlPoints() - i - 1).position().toMinecraft());
				pos2 = i < getAmountOfControlPoints() - 1 ? pos2.add(getControlPoint(Math.max(getAmountOfControlPoints() - i - 2, 0)).position().toMinecraft())
				: Vec3d.ZERO;
				for (int h = 0; h < 4; h++) {
					Vec3d circlePos = Vector.getOrthogonalVector(getLookVec(), (ticksExisted % 360) * 20 + h * 90, getAvgSize()).toMinecraft().add(pos);
					Vec3d targetPos = i < getAmountOfControlPoints() - 1 ? Vector.getOrthogonalVector(getLookVec(),
							(ticksExisted % 360) * 20 + h * 90 + 20, getAvgSize()).toMinecraft().add(pos2)
							: Vec3d.ZERO;
					Vec3d vel = new Vec3d(world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240);
					vel = targetPos == Vec3d.ZERO ? vel : targetPos.subtract(circlePos).normalize().scale(0.05).add(vel);
					ParticleBuilder.create(ParticleBuilder.Type.WATER).pos(circlePos).spawnEntity(this).vel(vel)
							.clr(0, 102,255, 175).scale(0.75F).target(targetPos == Vec3d.ZERO ? pos : targetPos)
							.time(10 + AvatarUtils.getRandomNumberInRange(0, 5)).collide(true).spawn(world);
					//0, 102, 255
				}
				for (int h = 0; h < 2; h++)
					ParticleBuilder.create(ParticleBuilder.Type.WATER).pos(pos).spawnEntity(this).vel(world.rand.nextGaussian() / 120,
						world.rand.nextGaussian() / 120, world.rand.nextGaussian() / 120).clr(0, 102, 255, 255)
						.time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).target(pos).collide(true).spawn(world);
				//Dripping water blocks
				for (int h = 0; h < 2; h++)
					ParticleBuilder.create(ParticleBuilder.Type.WATER).pos(pos).spawnEntity(this).vel(world.rand.nextGaussian() / 20,
							world.rand.nextDouble() / 12, world.rand.nextGaussian() / 20).clr(0, 102, 255, 255)
							.time(6 + AvatarUtils.getRandomNumberInRange(0, 3)).target(pos).scale(0.625F).gravity(true).collide(true).spawn(world);
			}
		}
	}


	@Override
	protected WaterControlPoint createControlPoint(float size, int index) {
		return new WaterControlPoint(this, size, 0, 0, 0);
	}

	public boolean canPlaySplash() {
		return lastPlayedSplash == -1;
	}

	public void playSplash() {
		world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.PLAYERS, 0.3f,
				1.5f, false);
		lastPlayedSplash = 0;
	}

	public WaterArcBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(WaterArcBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof WaterArcBehavior.PlayerControlled ? getOwner() : null;
	}

	public void cleanup() {
		if (getOwner() != null) {
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			if (data != null) {
				data.removeStatusControl(StatusControlController.THROW_WATER);
			}
		}
	}

	private void breakCollidingBlocks() {
		// Hitbox expansion (in each direction) to destroy blocks before the
		// waterarc collides with them
		double expansion = 0.1;
		AxisAlignedBB hitbox = getEntityBoundingBox().grow(expansion, expansion, expansion);

		for (int ix = 0; ix <= 1; ix++) {
			for (int iz = 0; iz <= 1; iz++) {

				double x = ix == 0 ? hitbox.minX : hitbox.maxX;
				double y = hitbox.minY;
				double z = iz == 0 ? hitbox.minZ : hitbox.maxZ;
				BlockPos pos = new BlockPos(x, y, z);

				tryBreakBlock(world.getBlockState(pos), pos);

			}
		}
	}

	/**
	 * Assuming the waterarc can break blocks, tries to break the block.
	 */
	private void tryBreakBlock(IBlockState state, BlockPos pos) {
		if (state.getBlock() == Blocks.AIR || !STATS_CONFIG.waterArcBreakableBlocks.contains(state.getBlock())) {
			return;
		}

		float hardness = state.getBlockHardness(world, pos);
		if (hardness <= 4) {
			breakBlock(pos);
			setVelocity(velocity().times(0.75));
		}
	}

	@Override
	protected double getVelocityMultiplier() {
		return velocityMultiplier;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public void setHealth(float health) {

	}

	@Override
	public boolean shouldExplode() {
		return getBehavior() instanceof WaterArcBehavior.Thrown;
	}

	static class WaterControlPoint extends ControlPoint {

		private WaterControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}

	}
}
