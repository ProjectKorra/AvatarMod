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

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

/**
 * @author CrowsOfWar
 */
public class EntityFireball extends AvatarEntity {

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityFireball.class,
			DataSerializers.VARINT);
	private static final DataParameter<FireballBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireball.class, FireballBehavior.DATA_SERIALIZER);
	private AxisAlignedBB expandedHitbox;

	private float damage;
	private float explosionStrength;
	private BlockPos position;

	/**
	 * @param world
	 */
	public EntityFireball(World world) {
		super(world);
		setSize(.8f, .8f);
		this.explosionStrength = 0.75f;
		this.position = this.getPosition();
	}

	public void setExplosionStrength(float strength) {
		this.explosionStrength = strength;
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireballBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		if (getBehavior() == null) {
			this.setBehavior(new FireballBehavior.Thrown());
		}
		setBehavior((FireballBehavior) getBehavior().onUpdate(this));
		if (ticksExisted % 30 == 0) {
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 6, 0.8F);
		}

		// Add hook or something
		if (getOwner() == null) {
			setDead();
			removeStatCtrl();
		}

		if (getOwner() != null) {
			EntityFireball ball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (ball == null && bD.hasStatusControl(StatusControl.THROW_FIREBALL)) {
				bD.removeStatusControl(StatusControl.THROW_FIREBALL);
			}
			if (ball != null && ball.getBehavior() instanceof FireballBehavior.PlayerControlled && !(bD.hasStatusControl(StatusControl.THROW_FIREBALL))) {
				bD.addStatusControl(StatusControl.THROW_FIREBALL);
			}
			if (getBehavior() != null && getBehavior() instanceof FireballBehavior.PlayerControlled) {
				this.position = this.getPosition();
			}

		}
	}


	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		if (getBehavior() instanceof FireballBehavior.PlayerControlled) {
			removeStatCtrl();
		}
		setDead();
		removeStatCtrl();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		spawnExtinguishIndicators();
		return false;
	}

	public FireballBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(FireballBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FireballBehavior.PlayerControlled ? getOwner() : null;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(int size) {
		dataManager.set(SYNC_SIZE, size);
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onFireContact();
		}
		if (canCollideWith(entity) && entity != getOwner() && getBehavior() instanceof FireballBehavior.Thrown) {
			float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;

			explosionSize *= getSize() / 15f;
			explosionSize += getPowerRating() * 2.0 / 100;
			Explode(explosionSize);
		}
	}

	@Override
	public boolean onCollideWithSolid() {


		if (getBehavior() instanceof FireballBehavior.Thrown) {
			float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;

			explosionSize *= getSize() / 15f;
			explosionSize += getPowerRating() * 2.0 / 100;
			boolean destroyObsidian = false;

			if (getOwner() != null && !world.isRemote) {
				if (getAbility() instanceof AbilityFireball) {
					AbilityData abilityData = BendingData.get(getOwner())
							.getAbilityData("fireball");
					if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
						destroyObsidian = true;
					}
				}

			}

			Explode(explosionSize);

			if (destroyObsidian) {
				for (EnumFacing dir : EnumFacing.values()) {
					BlockPos pos = getPosition().offset(dir);
					if (world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
						world.destroyBlock(pos, true);
					}
				}
			}


			setDead();
			removeStatCtrl();

		}
		return true;

	}

	@Override
	public void setDead() {
		super.setDead();
		removeStatCtrl();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((FireballBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}

	@Override
	public int getBrightnessForRender() {
		return 150;
	}


	public AxisAlignedBB getExpandedHitbox() {
		return this.expandedHitbox;
	}

	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(0.35, 0.35, 0.35);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}
	//Mostly fixes a glitch where the entity turns invisible

	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			if (data != null) {
				data.removeStatusControl(StatusControl.THROW_FIREBALL);
			}
		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	public void Explode(float ExplosionSize) {
		if (world instanceof WorldServer) {
			float size = ExplosionSize;
			float speed = size / 20;
			float hitBox = size + 0.5F;
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData("fireball");
				if (abilityData.getLevel() == 1) {
					speed = size / 8;
					hitBox = size + 1.5F;
				}
				if (abilityData.getLevel() >= 2) {
					speed = size / 4;
					hitBox = size + 4;
				}

				this.setInvisible(true);
				WorldServer World = (WorldServer) this.world;
				World.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, getSize() * 15, 0, 0, 0, getSize() / 200F);
				World.spawnParticle(EnumParticleTypes.LAVA, posX, posY, posZ, 50, 0, 0, 0, speed);
				world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(hitBox, hitBox, hitBox),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							if (canCollideWith(entity) && entity != getOwner()) {

								damageEntity(entity);

								double mult = abilityData.getLevel() >= 2 ? -2 : -1;
								double distanceTravelled = entity.getDistance(this.position.getX(), this.position.getY(), this.position.getZ());

								Vector vel = position().minus(getEntityPos(entity));
								vel = vel.normalize().times(mult).plusY(0.15f);

								entity.motionX = vel.x() + 0.1 / distanceTravelled;
								entity.motionY = vel.y() > 0 ? vel.y() + 0.1 / distanceTravelled : 0.3F + 0.1 / distanceTravelled;
								entity.motionZ = vel.z() + 0.1 / distanceTravelled;

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
			}
		}
	}

	public void damageEntity(Entity entity) {
		if (getOwner() != null) {
			AbilityData abilityData = null;
			if (!world.isRemote && getAbility() instanceof AbilityFireball) {
				abilityData = AbilityData.get(getOwner(), getAbility().getName());
				DamageSource ds = AvatarDamageSource.causeFireballDamage(entity, getOwner());
				int lvl = abilityData.getLevel();
				float damage = 1.5F;
				if (lvl == 1) {
					damage = 2.5F;
				}
				if (lvl == 2) {
					damage = 3;
				}
				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
					damage = 3.5F;
				}
				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					damage = 3.5F;
				}
				if (entity.attackEntityFrom(ds, damage)) {
					abilityData.addXp(SKILLS_CONFIG.fireballHit);
					BattlePerformanceScore.addMediumScore(getOwner());

				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}
}
