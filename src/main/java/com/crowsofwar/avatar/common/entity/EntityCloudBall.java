package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.AIR_STATCTRL_HANDLER;

public class EntityCloudBall extends AvatarEntity {

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityCloudBall.class, DataSerializers.VARINT);
	private static final DataParameter<CloudburstBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityCloudBall.class, CloudburstBehavior.DATA_SERIALIZER);
	private AxisAlignedBB expandedHitbox;

	private float damage;
	private boolean absorbtion, chismash, pushStone, pushIronTrapDoor, pushIronDoor;

	/**
	 * @param world The world the cloudburst is spawned in.
	 */
	public EntityCloudBall(World world) {
		super(world);
		setSize(0.8f, 0.8f);
		this.putsOutFires = true;
		this.pushStoneButton = pushStone;
		this.pushDoor = pushIronDoor;
		this.pushTrapDoor = pushIronTrapDoor;

	}

	public void setAbsorb(boolean canAbsorb) {
		absorbtion = canAbsorb;
	}

	public void setChiSmash(boolean canchiSmash) {
		chismash = canchiSmash;
	}

	public void setPushStoneButton(boolean pushStone) {
		this.pushStone = pushStone;
	}

	public void setPushIronDoor(boolean pushDoor) {
		this.pushIronDoor = pushDoor;
	}

	public void setPushIronTrapDoor(boolean trapDoor) {
		this.pushIronTrapDoor = trapDoor;
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
		if (ticksExisted <= 2) {
			this.pushStoneButton = pushStone;
			this.pushDoor = pushIronDoor;
			this.pushTrapDoor = pushIronTrapDoor;
		}
		int ticks = 0;
		if (getBehavior() == null) {
			this.setBehavior(new CloudburstBehavior.PlayerControlled());
		}
		setBehavior((CloudburstBehavior) getBehavior().onUpdate(this));
		if (getBehavior() instanceof CloudburstBehavior.Thrown) {
			ticks++;
			if (ticks >= 200) {
				cloudBurst();
				setDead();
			}
		}

		if (ticksExisted % 2 == 0) {
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, (0.05F),
					(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
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
			if (ball != null && ball.getBehavior() instanceof CloudburstBehavior.PlayerControlled && !(bD
					.hasStatusControl(StatusControl.THROW_CLOUDBURST))) {
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
	public void setDead() {
		super.setDead();
		if (getOwner() != null && !world.isRemote && isDead) {
			BendingData data = BendingData.get(getOwner());
			data.addTickHandler(AIR_STATCTRL_HANDLER);
		}
		removeStatCtrl();
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
	public void onCollideWithEntity(Entity entity) {
		if (getOwner() != null) {
			if (absorbtion) {
				if (entity instanceof AvatarEntity) {
					((AvatarEntity) entity).isProjectile();
					entity.setDead();
					damage += 3F;
				}
				if (entity instanceof EntityArrow) {
					entity.setDead();
					damage += 2F;
				}
				if (entity instanceof EntityThrowable) {
					entity.setDead();
					damage += 1F;
				}
			}
			if (chismash) {
				if (entity instanceof EntityLivingBase) {
					if (Bender.isBenderSupported((EntityLivingBase) entity)) {
						BendingData data = BendingData.get((EntityLivingBase) entity);
						for (UUID uuid : data.getAllBendingIds()) {
							CloudburstPowerModifier cloudModifier = new CloudburstPowerModifier();
							cloudModifier.setTicks(100);
							Objects.requireNonNull(data.getPowerRatingManager(uuid))
									.addModifier(cloudModifier, new BendingContext(data, (EntityLivingBase) entity, new Raytrace.Result()));
						}

					}
				}
			}

		}
		if (getBehavior() instanceof CloudburstBehavior.Thrown) {
			cloudBurst();
		}
		super.onCollideWithEntity(entity);
	}

	/**
	 * Prevents the cloudburst from colliding with arrows and other projectiles and deflecting them,
	 * which messes up the absorption mechanic.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return true;
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
		return expandedHitbox;
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

				WorldServer World = (WorldServer) world;
				World.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 50, 0, 0, 0, speed);
				world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 4.0F,
						(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(hitBox, hitBox, hitBox),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							damageEntity(entity);
							//Divide the result of the position difference to make entities fly
							//further the closer they are to the player.
							double dist = (hitBox - entity.getDistance(entity)) > 1 ? (hitBox - entity.getDistance(entity)) : 1;
							Vector velocity = Vector.getEntityPos(entity).minus(Vector.getEntityPos(this));
							velocity = velocity.dividedBy(40).times(dist).withY(hitBox / 50);

							double x = (velocity.x());
							double y = (velocity.y()) > 0 ? velocity.y() : 0.3F;
							double z = (velocity.z());

							if (!entity.world.isRemote) {
								entity.addVelocity(x, y, z);

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
	}

	public void damageEntity(Entity entity) {
		if (getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			AbilityData abilityData = data.getAbilityData("cloudburst");
			DamageSource ds = AvatarDamageSource.causeAirDamage(entity, getOwner());
			int lvl = abilityData.getLevel();
			float damage = getDamage() / 3;
			if (lvl == 1) {
				damage = getDamage() / 2;
			}
			if (lvl == 2) {
				damage = getDamage() / 1.5F;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = getDamage();
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage = getDamage();
			}
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
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			data.removeStatusControl(StatusControl.THROW_CLOUDBURST);
		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean pushButton(boolean pushStone) {
		return true;
	}

	@Override
	public boolean pushLever() {
		return true;
	}

	@Override
	public boolean pushTrapdoor(boolean pushIron) {
		return true;
	}

	@Override
	public boolean pushDoor(boolean pushIron) {
		return true;
	}

	@Override
	public boolean pushGate() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}
}




