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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.earth.AbilityEarthspikes;
import com.crowsofwar.avatar.common.data.*;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityEarthspike extends AvatarEntity {

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityEarthspike.class, DataSerializers.FLOAT);

	private double damage;
	private float Size;
	private double lifetime;

	public EntityEarthspike(World world) {
		super(world);
		Size = 1;
		setSize(Size, Size);
		damage = STATS_CONFIG.earthspikeSettings.damage;
		noClip = true;
		lifetime = 30;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public void setLifetime(double lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE, 1F);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void onEntityUpdate() {
		//Add width and height stuff

		motionX = 0;
		motionY = 0;
		motionZ = 0;

		if (ticksExisted >= lifetime) {
			setDead();
		}

		setSize(getSize(), getSize());

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();
		damage = damage * belowBlock.getBlockHardness(world.getBlockState(below), world, below);
		if (getAbility() instanceof AbilityEarthspikes) {
			AbilityData aD = AbilityData.get(getOwner(), getAbility().getName());
			if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				if (!world.isRemote && (!STATS_CONFIG.bendableBlocks.contains(belowBlock) || belowBlock == Blocks.AIR)) {
					setDead();
				}
			}
		} else if (belowBlock == Blocks.AIR) {
			setDead();
		}

		// Push collided entities back
		if (!world.isRemote) {
			AxisAlignedBB box = new AxisAlignedBB(posX + getSize(), posY + getSize(), posZ + getSize(), posX - getSize(), posY, posZ - getSize());
			List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box);
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && canCollideWith(entity)) {
						onCollideWithEntity(entity);
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (!world.isRemote && entity != getOwner() && !(entity instanceof EntityEarthspike) && !(entity instanceof EntityEarthspikeSpawner)
						&& canCollideWith(entity)) {
			pushEntity(entity);
			if (attackEntity(entity)) {
				if (getOwner() != null) {
					BattlePerformanceScore.addScore(getOwner(), 15);
				}

			}
			if (getOwner() != null && getAbility() != null) {
				BendingData data = BendingData.get(getOwner());
				if (data != null) {
					data.getAbilityData(getAbility().getName()).addXp(SKILLS_CONFIG.earthspikeHit);
				}
			}
		}

	}

	private boolean attackEntity(Entity entity) {
		if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && canCollideWith(entity) && canDamageEntity(entity)) {
			DamageSource ds = AvatarDamageSource.causeEarthspikeDamage(entity, getOwner());
			float damage = (float) this.damage;
			return entity.attackEntityFrom(ds, damage);
			//Modify damage based on power rating!
		} else return false;
	}

	private void pushEntity(Entity entity) {
		entity.motionX += motionX / 4;
		entity.motionY += (STATS_CONFIG.earthspikeSettings.push / 6) + (damage / 100);
		entity.motionZ += motionZ / 4;
	}
}
