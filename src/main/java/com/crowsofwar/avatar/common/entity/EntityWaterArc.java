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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.lwjgl.Sys;
import scala.Int;

import java.util.List;
import java.util.Random;

import static com.crowsofwar.avatar.common.bending.StatusControl.THROW_WATER;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

public class EntityWaterArc extends EntityArc<EntityWaterArc.WaterControlPoint> {

	private static final DataParameter<WaterArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWaterArc.class, WaterArcBehavior.DATA_SERIALIZER);
	private static final DataParameter<Integer> SYNC_COMBO_TIMER = EntityDataManager.createKey(EntityWaterArc.class,
			DataSerializers.VARINT);


	/**
	 * The amount of ticks since last played splash sound. -1 for splashable.
	 */
	private int lastPlayedSplash;

	private boolean isSpear;

	private float damageMult;

	private int comboTimer;

	private float Size;

	private float Gravity;

	public EntityWaterArc(World world) {
		super(world);
		setSize(Size, Size);
		this.lastPlayedSplash = -1;
		this.damageMult = 1;
		this.putsOutFires = true;
		this.comboTimer = 0;
		this.Size = 0.4F;
		this.Gravity = 9.81F;

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

	public void setSize(float size) {
		this.Size = size;
	}

	public int getComboTimer() {
		return dataManager.get(SYNC_COMBO_TIMER);
	}

	public void setComboTimer(int timer) {
		dataManager.set(SYNC_COMBO_TIMER, timer);
	}

	public float getGravity() {
		return this.Gravity;
	}

	public void setGravity(float gravity) {
		this.Gravity = gravity;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new WaterArcBehavior.Idle());
		dataManager.register(SYNC_COMBO_TIMER, comboTimer);
	}

	public void damageEntity(Entity entity) {
		DamageSource ds = AvatarDamageSource.causeWaterDamage(entity, getOwner());
		float damage = 3 * damageMult;
		entity.attackEntityFrom(ds, damage);
		if (entity.attackEntityFrom(ds, damage)) {
			if (getOwner() != null) {
				BendingData data1 = BendingData.get(getOwner());
				AbilityData abilityData1 = data1.getAbilityData("water_arc");
				abilityData1.addXp(SKILLS_CONFIG.waterHit);
				BattlePerformanceScore.addMediumScore(getOwner());

			}
		}
	}

	public void Splash() {
		AbilityData abilityData = BendingData.get(getOwner()).getAbilityData("water_arc");
		int lvl = abilityData.getLevel();
		if (world instanceof WorldServer) {
			WorldServer World = (WorldServer) this.world;
			World.spawnParticle(EnumParticleTypes.WATER_WAKE, posX, posY, posZ, 500, 0.2, 0.1, 0.2, 0.03);
			world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().expand(1, 1, 1),
					entity -> entity != getOwner());
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {

					this.damageMult = lvl >= 2 ? 2 : 0.5F;
					//If the player's water arc level is level III or greater the aoe will do 2+ damage.
					damageEntity(entity);

					double mult = -0.1;

					Vector vel = position().minus(getEntityPos(entity));
					vel = vel.normalize().times(mult).plusY(0.1f);

					entity.motionX = vel.x();
					entity.motionY = vel.y();
					entity.motionZ = vel.z();
					damageEntity(entity);

					if (entity instanceof AvatarEntity) {
						AvatarEntity avent = (AvatarEntity) entity;
						avent.setVelocity(vel);
					}
					entity.isAirBorne = true;
					AvatarUtils.afterVelocityAdded(entity);
				}
			}

		}

	}

	@Override
	public void setDead() {
		super.setDead();
		if (this.isDead && !world.isRemote) {
			Thread.dumpStack();
		}
	}

	@Override
	public boolean onCollideWithSolid() {

		if (!world.isRemote && getBehavior() instanceof WaterArcBehavior.Thrown) {
			Splash();
			setDead();
			cleanup();


			if (world.isRemote) {
				Random random = new Random();

				double xVel = 0, yVel = 0, zVel = 0;
				double offX = 0, offY = 0, offZ = 0;

				if (isCollidedVertically) {

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

		return false;

	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onMinorWaterContact();
			if (!isSpear) {
				Splash();
				this.setDead();
				cleanup();
			}
		}
		if (!isSpear) {
			Splash();
			this.setDead();
			cleanup();
		}

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


		if (inWater && behavior instanceof WaterArcBehavior.PlayerControlled) {
			// try to go upwards
			for (double i = 0.1; i <= 3; i += 0.05) {
				BlockPos pos = new Vector(this).plus(0, i, 0).toBlockPos();
				if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
					setPosition(posX, posY + i, posZ);
					inWater = false;
					break;
				}
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

	@Override
	protected double getControlPointTeleportDistanceSq() {
		return 10;
		//Lower makes it faster, higher makes it slower.
	}

	public void cleanup() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(THROW_WATER);
		}
	}

	public static class WaterControlPoint extends ControlPoint {

		public WaterControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}

	}

}
