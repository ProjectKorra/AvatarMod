package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

public class EntityAirShockwave extends AvatarEntity {

	private static final DataParameter<Integer> SYNC_DISSIPATE = EntityDataManager
		.createKey(EntityAirShockwave.class, DataSerializers.VARINT);
	public static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityAirShockwave.class,
			DataSerializers.FLOAT);

	public EntityAirShockwave(World world) {
		super(world);
		setSize(0.5f, 0.5f);
		//setSize(0, 0);

		this.putsOutFires = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DISSIPATE, 0);
		dataManager.register(SYNC_SIZE, 0.5f);
	}

	@Override
	public EntityLivingBase getController() {
		return !isDissipating() ? getOwner() : null;
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}


	@Override
	public void setPositionAndUpdate(double x, double y, double z) {
		if (getOwner() != null) {
			super.setPositionAndUpdate(getOwner().posX, getOwner().getEntityBoundingBox().minY, getOwner().posZ);
		}
	}

	@Override
	public void onUpdate() {
		//super.onUpdate();
		//Otherwise the entity glitches out


		EntityLivingBase owner = getOwner();
		if (owner == null) {
			return;
		}

		if (putsOutFires && ticksExisted % 2 == 0) {
			setFire(0);
			for (int x = 0; x <= 2; x++) {
				for (int z = 0; z <= 2; z++) {
					BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
					if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
						world.setBlockToAir(pos);
						world.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
								SoundCategory.PLAYERS, 1, 1, false);
					}
				}
			}
		}

		if (owner.isDead) {
			dissipateLarge();
			return;
		}

		setPosition(owner.posX, owner.getEntityBoundingBox().minY, owner.posZ);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;


		Bender ownerBender = Bender.get(getOwner());


		float size = getSize();

		if (isDissipatingLarge()) {
			setDissipateTime(getDissipateTime() + 1);
			float mult = 1 + getDissipateTime() / 10f;
			setSize(size * mult, size * mult);
			if (getDissipateTime() >= 10) {
				setDead();
			}
		} else if (isDissipatingSmall()) {
			setDissipateTime(getDissipateTime() - 1);
			float mult = 1 + getDissipateTime() / 40f;
			setSize(size * mult, size * mult);
			if (getDissipateTime() <= -10) {
				setDead();
			}
		} else {
			setSize(size, size);
		}

	}

	/**
	 * Handles hovering logic to make the owner hover. Preconditions (not in water, owner
	 * present, etc) are handled by the caller
	 */
	private void handleHovering() {

		if (getOwner() == null) {
			return;
		}
		if (getOwner() != null) {
			getOwner().fallDistance = 0;
		}

		// Min/max acceptable hovering distance
		// Hovering is allowed between these two values
		// Hover distance doesn't need to be EXACT
		final double minFloatHeight = 1.8;
		final double maxFloatHeight = 2.2;

		EntityLivingBase owner = getOwner();

		// Find whether there are blocks under the owner
		// Done by making a hitbox around the owner's feet and checking if there are blocks
		// colliding with that hitbox

		double x = owner.posX;
		double y = owner.posY;
		double z = owner.posZ;
		//Don't use setPosition; that makes it super duper ultra glitchy
		AxisAlignedBB hitbox = new AxisAlignedBB(x, y, z, x, y, z);
		hitbox = hitbox.grow(0.2, 0, 0.2);
		hitbox = hitbox.expand(0, -maxFloatHeight, 0);

		List<AxisAlignedBB> blockCollisions = world.getCollisionBoxes(null, hitbox);

		if (!blockCollisions.isEmpty()) {

			// Calculate the top-of-ground ground y position
			// Performed by finding the maximum ypos of each collided block
			double groundPosition = Double.MIN_VALUE;
			for (AxisAlignedBB blockHitbox : blockCollisions) {
				if (blockHitbox.maxY > groundPosition) {
					groundPosition = blockHitbox.maxY;
				}
			}
			// Now calculate the distance from ground
			// and use that to determine whether owner should float
			double distanceFromGround = owner.posY - groundPosition;

			// Tweak motion based on distance to ground, and target distance
			// Minecraft gravity is 0.08 blocks/tick

			if (distanceFromGround < minFloatHeight) {
				owner.motionY += 0.11;
			}
			if (distanceFromGround >= minFloatHeight && distanceFromGround < maxFloatHeight) {
				owner.motionY *= 0.7;
			}
			if (distanceFromGround >= maxFloatHeight) {
				owner.motionY += 0.07;

				// Avoid falling at over 3 m/s
				if (owner.motionY < -3.0 / 20) {
					owner.motionY = 0;
				}
			}

		}
	}

	@Override
	public void setDead() {
		super.setDead();
	}

	@Override
	public boolean isShield() {
		return true;
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {

		double mult = -2;
		if (isDissipatingLarge()) mult = -4;
		Vector vel = position().minus(getEntityPos(entity));
		vel = vel.normalize().times(mult).plusY(0.3f);

		entity.motionX = vel.x();
		entity.motionY = vel.y();
		entity.motionZ = vel.z();

		if (entity instanceof AvatarEntity) {
			AvatarEntity avent = (AvatarEntity) entity;
			avent.setVelocity(vel);
		}
		entity.isAirBorne = true;
		AvatarUtils.afterVelocityAdded(entity);
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() == getOwner()) {
			return false;
		} else return entity != getOwner() && !(entity instanceof EntityArrow) && !(entity instanceof EntityItem);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDissipateTime(nbt.getInteger("Dissipate"));
		setSize(nbt.getFloat("Size"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Dissipate", getDissipateTime());
		nbt.setFloat("Size", getSize());
	}



	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	public int getDissipateTime() {
		return dataManager.get(SYNC_DISSIPATE);
	}

	public void setDissipateTime(int dissipate) {
		dataManager.set(SYNC_DISSIPATE, dissipate);
	}

	public void dissipateLarge() {
		if (!isDissipating()) setDissipateTime(1);
	}


	public boolean isDissipating() {
		return getDissipateTime() != 0;
	}

	public boolean isDissipatingLarge() {
		return getDissipateTime() > 0;
	}

	public boolean isDissipatingSmall() {
		return getDissipateTime() < 0;
	}

	}


