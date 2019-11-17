package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.powermods.CloudburstPowerModifier;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.common.data.TickHandlerController.AIR_STATCTRL_HANDLER;

public class EntityCloudBall extends EntityOffensive {

	//We're keeping the size data parameter for special rendering
	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityCloudBall.class, DataSerializers.VARINT);
	private static final DataParameter<CloudburstBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityCloudBall.class, CloudburstBehavior.DATA_SERIALIZER);

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
		if (getBehavior() == null) {
			this.setBehavior(new CloudburstBehavior.PlayerControlled());
		}
		setBehavior((CloudburstBehavior) getBehavior().onUpdate(this));

		if (ticksExisted % 2 == 0) {
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, (0.05F),
					(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
		}

		// Add hook or something
		if (getOwner() == null)
			Dissipate();


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

		if (world.isRemote) {
			AxisAlignedBB boundingBox = getEntityBoundingBox();
			double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
			double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
			double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
					world.rand.nextGaussian() / 60).time(12).clr(0.8F, 0.8F, 0.8F)
					.scale(getSize() * 0.03125F * 2).element(getElement()).spawn(world);
		}

		//I'm using 0.03125, because that results in a size of 0.5F when rendering, as the default size for the cloudburst is actually 16.
		//This is due to weird rendering shenanigans
		setEntitySize(getSize() * 0.03125F);

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
	public void onCollideWithEntity(Entity entity) {
		if (getOwner() != null) {
			if (absorbtion) {
				if (entity instanceof EntityOffensive) {
					if (((EntityOffensive) entity).isProjectile()) {
						if (((EntityOffensive) entity).getDamage() <= 1.25 * getDamage() && ((EntityOffensive) entity).velocity().sqrMagnitude() <=
								velocity().sqrMagnitude() * 1.25) {
							((EntityOffensive) entity).Dissipate();
							setDamage(getDamage() + ((EntityOffensive) entity).getDamage() / 2);

						}
					}
				}
				if (entity instanceof EntityArrow) {
					entity.setDead();
					setDamage(getDamage() + 2);
				}
				if (entity instanceof EntityThrowable) {
					entity.setDead();
					setDamage(getDamage() + 1);
				}
			}
			if (getBehavior() instanceof CloudburstBehavior.Thrown) {
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
				super.onCollideWithEntity(entity);
			}
		}
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
		setAbsorb(nbt.getBoolean("Absorb"));
		setChiSmash(nbt.getBoolean("ChiSmash"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
		nbt.setBoolean("Absorb", absorbtion);
		nbt.setBoolean("ChiSmash", chismash);
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

	@Override
	public double getExpandedHitboxWidth() {
		return getSize() * 0.03125F / 2;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return getSize() * 0.03125F / 2;
	}

	@Override
	public void spawnExplosionParticles(World world, Vec3d pos) {
		if (world.isRemote)
			for (int i = 0; i < getSize(); i++)
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(getAvgSize()).collide(true).vel(world.rand.nextGaussian() / 10,
						world.rand.nextGaussian() / 10, world.rand.nextGaussian() / 10).time(8).pos(AvatarEntityUtils.getMiddleOfEntity(this))
						.clr(0.85F, 0.85F, 0.85F).element(getElement()).spawn(world);
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {
		if (world.isRemote)
			for (int i = 0; i < getSize(); i++)
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(getAvgSize()).collide(true).vel(world.rand.nextGaussian() / 40,
						world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40).time(8).pos(AvatarEntityUtils.getMiddleOfEntity(this))
						.clr(0.85F, 0.85F, 0.85F).element(getElement()).spawn(world);

	}

	@Override
	public boolean shouldDissipate() {
		return getBehavior() instanceof CloudburstBehavior.Thrown;
	}

	@Override
	public boolean shouldExplode() {
		return getBehavior() instanceof CloudburstBehavior.Thrown;
	}

	@Override
	public SoundEvent[] getSounds() {
		SoundEvent[] events = new SoundEvent[1];
		events[0] = SoundEvents.BLOCK_FIRE_EXTINGUISH;
		return events;
	}

	@Override
	public float getVolume() {
		return super.getVolume() * 5;
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(1.5, 1, 1.5);
	}

	@Override
	public Vec3d getExplosionKnockbackMult() {
		return new Vec3d(0.5, 0.75, 0.5);
	}

	@Override
	public int getFireTime() {
		return 0;
	}
}




