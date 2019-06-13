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
import com.crowsofwar.avatar.common.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityAirblade extends AvatarEntity {

	private static final DataParameter<Float> SYNC_SIZE_MULT = EntityDataManager.createKey(EntityAirblade.class, DataSerializers.FLOAT);
	private float damage;
	/**
	 * Hardness threshold to chop blocks. For example, setting to 1.5 will allow
	 * the airblade to chop stone.
	 * <p>
	 * Note: Threshold of 0 means that the airblade can chop grass and similar
	 * blocks. Set to < 0 to avoid chopping blocks at all.
	 */
	private float chopBlocksThreshold;
	private boolean pierceArmor;

	public EntityAirblade(World world) {
		super(world);
		setSize(0.2f, 1.5f);
		this.chopBlocksThreshold = -1;
		this.noClip = true;
	}

	public float getSizeMult() {
		return dataManager.get(SYNC_SIZE_MULT);
	}

	public void setSizeMult(float sizeMult) {
		dataManager.set(SYNC_SIZE_MULT, sizeMult);
	}


	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE_MULT, 1.0F);
	}

	@Override
	public BendingStyle getElement() {
		return new Airbending();
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return super.canCollideWith(entity) && !(entity instanceof EntityAirblade) || entity instanceof EntityLivingBase;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		setSize(getSizeMult() * 0.2F, getSizeMult() * 1.5F);

		this.motionX = this.motionX * 0.98;
		this.motionY = this.motionY * 0.98;
		this.motionZ = this.motionZ * 0.98;

		if (!world.isRemote && getOwner() != null && getAbility() instanceof AbilityAirblade) {
			AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				if (velocity().sqrMagnitude() <= 0.5) {
					setDead();
				}

			} else if (velocity().sqrMagnitude() <= 0.9) {
				setDead();
			}
		} else if (!world.isRemote && velocity().sqrMagnitude() <= .9) {
			setDead();
		}

		if (this.ticksExisted > 200) {
			this.setDead();
		}
		if (!world.isRemote && inWater) {
			setDead();
		}

		if (!world.isRemote && chopBlocksThreshold >= 0) {
			breakCollidingBlocks();
		}

		if (!isDead && !world.isRemote) {
			List<Entity> collidedList = world.getEntitiesWithinAABB(Entity.class,
					getEntityBoundingBox().grow(0.35F));
			if (!collidedList.isEmpty()) {
				for (Entity collided : collidedList) {
					if (collided instanceof AvatarEntity) {
						((AvatarEntity) collided).onAirContact();
					} else if (canCollideWith(collided)) {
						handleCollision(collided);
					}

				}
			}
		}

	}

	private void handleCollision(Entity collided) {
		Vector motion = velocity();
		motion = motion.times(STATS_CONFIG.airbladeSettings.push).withY(0.08);
		collided.addVelocity(motion.x(), motion.y(), motion.z());

		if (canDamageEntity(collided) && getOwner() != null) {

			BendingData data = getOwnerBender().getData();
			DamageSource source = AvatarDamageSource.causeAirbladeDamage(collided, getOwner());
			if (pierceArmor) {
				source.setDamageBypassesArmor();
			}

			boolean successfulHit = collided.attackEntityFrom(source, damage);

			if (successfulHit && getAbility() != null) {
				BattlePerformanceScore.addMediumScore(getOwner());
				data.getAbilityData(getAbility().getName()).addXp(SKILLS_CONFIG.airbladeHit);
			}
			if (!(getAbility() instanceof AbilityAirblade) || data.getAbilityData("airblade").getLevel() < 3) {
				setDead();
			}

		}
	}

	/**
	 * When the airblade can break blocks, checks any blocks that the airblade
	 * collides with and tries to break them
	 */
	private void breakCollidingBlocks() {
		// Hitbox expansion (in each direction) to destroy blocks before the
		// airblade collides with them
		double expansion = getSizeMult() / 20;
		AxisAlignedBB hitbox = getEntityBoundingBox().grow(expansion, expansion, expansion);
		for (int iy = 0; iy <= getSizeMult() * 1.5F; iy++) {
			for (int ix = 0; ix <= 1; ix++) {
				for (int iz = 0; iz <= 1; iz++) {

					double x = ix == 0 ? hitbox.minX : hitbox.maxX;
					double y = iy == 0 ? hitbox.minY : hitbox.maxY;
					double z = iz == 0 ? hitbox.minZ : hitbox.maxZ;
					BlockPos pos = new BlockPos(x, y, z);

					tryBreakBlock(world.getBlockState(pos), pos);

				}
			}
		}
	}


	/**
	 * Assuming the airblade can break blocks, tries to break the block.
	 */
	private void tryBreakBlock(IBlockState state, BlockPos pos) {
		if (state.getBlock() == Blocks.AIR || !STATS_CONFIG.airBladeBreakableBlocks.contains(state.getBlock())) {
			return;
		}

		float hardness = state.getBlockHardness(world, pos);
		if (hardness <= chopBlocksThreshold) {
			breakBlock(pos);
			setVelocity(velocity().times(0.95));
		}
	}


	public Bender getOwnerBender() {
		return Bender.get(getOwner());
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getChopBlocksThreshold() {
		return chopBlocksThreshold;
	}

	public void setChopBlocksThreshold(float chopBlocksThreshold) {
		this.chopBlocksThreshold = chopBlocksThreshold;
	}

	public boolean getPierceArmor() {
		return pierceArmor;
	}

	public void setPierceArmor(boolean piercing) {
		this.pierceArmor = piercing;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		damage = nbt.getFloat("Damage");
		chopBlocksThreshold = nbt.getFloat("ChopBlocksThreshold");
		pierceArmor = nbt.getBoolean("Piercing");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", damage);
		nbt.setFloat("ChopBlocksThreshold", chopBlocksThreshold);
		nbt.setBoolean("Piercing", pierceArmor);
	}


	@Override
	public boolean isProjectile() {
		return true;
	}

}
