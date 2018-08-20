package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.CloudburstPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

public class EntityCloudBall extends AvatarEntity {
	/**
	 * @param world
	 */
	public static final DataParameter<CloudburstBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityCloudBall.class, CloudburstBehavior.DATA_SERIALIZER);

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityCloudBall.class,
			DataSerializers.VARINT);

	private AxisAlignedBB expandedHitbox;

	private float damage;
	private boolean absorbtion;
	private boolean chismash;
	private BlockPos position;

	/**
	 * @param world
	 */
	public EntityCloudBall(World world) {
		super(world);
		setSize(0.8f, 0.8f);

	}

	public void canAbsorb(boolean canAbsorb) {
		this.absorbtion = canAbsorb;
	}

	public void canchiSmash(boolean canchiSmash) {
		this.chismash = canchiSmash;
	}

	public void setStartingPosition(BlockPos position) {
		this.position = position;
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new CloudburstBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}


	@Override
	public void onUpdate() {
		super.onUpdate();
		int ticks = 0;
		setBehavior((CloudburstBehavior) getBehavior().onUpdate(this));
		if (this.getBehavior() instanceof CloudburstBehavior.Thrown) {
			ticks++;
			if (ticks >= 200) {
				cloudBurst();
				this.setDead();
			}
		}

		if (ticksExisted % 2 == 0) {
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, (0.05F), (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
		}

		// Add hook or something
		if (getOwner() == null) {
			setDead();
			removeStatCtrl();
		}

		if (getOwner() != null) {
			EntityCloudBall ball = AvatarEntity.lookupControlledEntity(world, EntityCloudBall.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (ball == null && bD.hasStatusControl(StatusControl.THROW_CLOUDBURST)) {
				bD.removeStatusControl(StatusControl.THROW_CLOUDBURST);
			}
			if (ball != null && ball.getBehavior() instanceof CloudburstBehavior.PlayerControlled && !(bD.hasStatusControl(StatusControl.THROW_CLOUDBURST))) {
				bD.addStatusControl(StatusControl.THROW_CLOUDBURST);
			}

		}

	}

	public CloudburstBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(CloudburstBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof CloudburstBehavior.PlayerControlled ? getOwner() : null;
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
	public boolean onCollideWithSolid() {

		if (getOwner() != null) {
			AbilityData abilityData = BendingData.get(getOwner()).getAbilityData("cloudburst");
			abilityData.addXp(3);
		}

		if (getBehavior() instanceof CloudburstBehavior.Thrown) {
			cloudBurst();
			setDead();
		}
		return true;

	}

	@Override
	public boolean canCollideWith(Entity entity) {
		if (getOwner() != null) {

			if (absorbtion) {
				if (entity instanceof AvatarEntity) {
					((AvatarEntity) entity).isProjectile();
					entity.setDead();
					damage += 3F;
					return false;
				}

				if (entity instanceof EntityArrow) {
					entity.setDead();
					damage += 2F;
					return false;
				}
				if (entity instanceof EntityThrowable) {
					entity.setDead();
					damage += 1F;
					return false;
				}
			}
			if (chismash) {
				if (entity instanceof EntityLivingBase) {
					if (Bender.isBenderSupported((EntityLivingBase) entity)) {
						BendingData data = BendingData.get((EntityLivingBase) entity);
						for (UUID uuid : data.getAllBendingIds()) {
							CloudburstPowerModifier cloudModifier = new CloudburstPowerModifier();
							cloudModifier.setTicks(100);
							data.getPowerRatingManager(uuid).addModifier(cloudModifier, new
									BendingContext(data, (EntityLivingBase) entity, new
									Raytrace.Result()));
						}

					}
				}
			}

		}
		if (getBehavior() instanceof CloudburstBehavior.Thrown) {
			cloudBurst();
		}
		return super.canCollideWith(entity);

	}

	/**
	 * Prevents the cloudburst from colliding with arrows and other projectiles and deflecting them,
	 * which messes up the absorption mechanic.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((CloudburstBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}

	public AxisAlignedBB getExpandedHitbox() {
		return this.expandedHitbox;
	}

	public void cloudBurst() {
		if (world instanceof WorldServer) {
			float speed = 0.05F;
			float hitBox = 1F;
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData("cloudburst");
				if (abilityData.getLevel() == 1) {
					speed = 0.1F;
					hitBox = 2;
				}
				if (abilityData.getLevel() >= 2) {
					speed = 0.2F;
					hitBox = 4;
				}

				this.setInvisible(true);
				WorldServer World = (WorldServer) this.world;
				World.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 50, 0, 0, 0, speed);
				world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(hitBox, hitBox, hitBox),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {

						damageEntity(entity);

						double distanceTravelled = entity.getDistance(this.position.getX(), this.position.getY(), this.position.getZ());

						//Divide the result of the position difference to make entities fly
						//further the closer they are to the player.
						Vector velocity = Vector.getEntityPos(entity).minus(Vector.getEntityPos(this));
						double distance = Vector.getEntityPos(entity).dist(Vector.getEntityPos(this));
						double direction = (hitBox - distance) * (speed * 5) / hitBox;
						velocity = velocity.times(direction).times(-1 + (-1 * hitBox/2)).withY(speed/2);

						double x = (velocity.x()) + distanceTravelled / 50;
						double y = (velocity.y()) > 0 ? velocity.y() + distanceTravelled / 100 : 0.3F + distanceTravelled / 100;
						double z = (velocity.z()) + distanceTravelled / 50;

						if (!entity.world.isRemote) {
							entity.addVelocity(x, y, z);

							if (collided instanceof AvatarEntity) {
								if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
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

	public void damageEntity(Entity entity) {
		if (getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			AbilityData abilityData = data.getAbilityData("cloudburst");
			DamageSource ds = AvatarDamageSource.causeCloudburstDamage(entity, getOwner());
			int lvl = abilityData.getLevel();
			float damage = 0.5F;
			if (lvl == 1) {
				damage = 1;
			}
			if (lvl == 2) {
				damage = 1.5F;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = 2;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage = 2.5F;
			}
			entity.attackEntityFrom(ds, damage);
			if (entity.attackEntityFrom(ds, damage)) {
				abilityData.addXp(SKILLS_CONFIG.cloudburstHit);
				BattlePerformanceScore.addMediumScore(getOwner());

			}
		}
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
	//Fixes a glitch where the entity turns invisible


	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_CLOUDBURST);
		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}




