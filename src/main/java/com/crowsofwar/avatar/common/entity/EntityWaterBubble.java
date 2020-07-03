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

import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControlController;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.Objects;
import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class EntityWaterBubble extends EntityOffensive implements IShieldEntity {

	private static final DataParameter<WaterBubbleBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWaterBubble.class, WaterBubbleBehavior.DATA_SERIALIZER);
	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_DEGREES_PER_SECOND = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_MAX_SIZE = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);

	/**
	 * Whether the water bubble will get a water source upon landing. Only
	 * set on server-side.
	 */
	private boolean sourceBlock;

	public EntityWaterBubble(World world) {
		super(world);
		setSize(.8f, .8f);
		this.putsOutFires = true;
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public float getMaxSize() {
		return dataManager.get(SYNC_MAX_SIZE);
	}

	public void setMaxSize(float maxSize) {
		dataManager.set(SYNC_MAX_SIZE, maxSize);
	}

	public float getHealth() {
		return dataManager.get(SYNC_HEALTH);
	}

	@Override
	public float getMaxHealth() {
		return 0;
	}

	public void setHealth(float health) {
		dataManager.set(SYNC_HEALTH, health);
	}

	@Override
	public void setMaxHealth(float maxHealth) {

	}

	public float getDegreesPerSecond() {
		return dataManager.get(SYNC_DEGREES_PER_SECOND);
	}

	public void setDegreesPerSecond(float degrees) {
		dataManager.set(SYNC_DEGREES_PER_SECOND, degrees);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new WaterBubbleBehavior.Drop());
		dataManager.register(SYNC_SIZE, 1F);
		dataManager.register(SYNC_MAX_SIZE, 1.5F);
		dataManager.register(SYNC_HEALTH, 3F);
		dataManager.register(SYNC_DEGREES_PER_SECOND, 5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		setSize(getSize(), getSize());

		if (getBehavior() != null && getBehavior() instanceof WaterBubbleBehavior.Lobbed) {
			setVelocity(velocity().times(0.9));
		}
		if (getHealth() == 0) {
			this.setDead();
		}

		WaterBubbleBehavior currentBehavior = getBehavior();
		WaterBubbleBehavior nextBehavior = (WaterBubbleBehavior) currentBehavior.onUpdate(this);
		if (currentBehavior != nextBehavior) setBehavior(nextBehavior);

		if (ticksExisted % 5 == 0) {
			BlockPos down = getPosition().down();
			IBlockState downState = world.getBlockState(down);
			if (downState.getBlock() == Blocks.FARMLAND) {
				int moisture = downState.getValue(BlockFarmland.MOISTURE);
				if (moisture < 7) world.setBlockState(down,
						Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, moisture + 1));
			}
		}

		boolean inWaterSource = false;
		if (!world.isRemote && ticksExisted % 2 == 1 && ticksExisted > 10) {
			for (int x = 0; x <= 1; x++) {
				for (int z = 0; z <= 1; z++) {
					BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
					IBlockState state = world.getBlockState(pos);
					if (state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0) {
						inWaterSource = true;
						break;
					}
				}
			}
		}
		/*
		if (!world.isRemote && inWaterSource) {
			setDead();
			if (getOwner() != null) {
				BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
				if (data != null) {
					data.removeStatusControl(StatusControlController.LOB_BUBBLE);
				}
			}
		}**/
		if (this.getOwner() == null) {
			this.setDead();
		}

		//particles!
		if (world.isRemote && getOwner() != null) {
			for (double h = 0; h < width; h += 0.5) {
				Random random = new Random();
				AxisAlignedBB boundingBox = getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(15 + AvatarUtils.getRandomNumberInRange(0, 10)).clr(0, 102, 255, 255)
						.scale(getSize()).element(getElement()).spawnEntity(getOwner()).element(new Waterbending())
						.spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(15 + AvatarUtils.getRandomNumberInRange(0, 10)).clr(0, 102, 255, 255)
						.scale(getSize()).element(getElement()).spawnEntity(getOwner()).element(new Waterbending())
						.spawn(world);
			}

		}


	}


	@Override
	public boolean shouldExplode() {
		return !(getBehavior() instanceof WaterBubbleBehavior.PlayerControlled);
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

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onMajorWaterContact();
			if (((AvatarEntity) entity).getAbility() != null && ((AvatarEntity) entity).getOwner() != null && getBehavior() != null && getBehavior() instanceof WaterBubbleBehavior.PlayerControlled) {
				float damage = AbilityData.get(((AvatarEntity) entity).getOwner(), ((AvatarEntity) entity).getAbility().getName()).getLevel();
				if (((AvatarEntity) entity).getElement() instanceof Firebending) {
					damage *= 0.5;
				}
				if (((AvatarEntity) entity).getElement() instanceof Lightningbending) {
					damage *= 2;
				}
				if (((AvatarEntity) entity).getElement() instanceof Waterbending) {
					damage *= 0.75;
				}
				((AvatarEntity) entity).onCollideWithSolid();
				this.setHealth(getHealth() - damage);
			}
		}
		if (getBehavior() instanceof WaterBubbleBehavior.PlayerControlled) {
			if (entity instanceof EntityArrow) {
				float damage = (float) ((EntityArrow) entity).getDamage();
				Vector vel = Vector.getVelocity(entity).times(-1);
				entity.addVelocity(vel.x(), 0, vel.z());
				setHealth(getHealth() - damage);
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setBehavior((WaterBubbleBehavior) Behavior.lookup(compound.getInteger("Behavior"), this));
		getBehavior().load(compound);
		setSourceBlock(compound.getBoolean("SourceBlock"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Behavior", getBehavior().getId());
		getBehavior().save(compound);
		compound.setBoolean("SourceBlock", sourceBlock);
	}

	public WaterBubbleBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(WaterBubbleBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	public boolean isSourceBlock() {
		return sourceBlock;
	}

	public void setSourceBlock(boolean sourceBlock) {
		this.sourceBlock = sourceBlock;
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof WaterBubbleBehavior.PlayerControlled ? getOwner() : null;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
}
