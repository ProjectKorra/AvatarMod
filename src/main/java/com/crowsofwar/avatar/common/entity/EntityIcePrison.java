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

import java.util.UUID;

import com.crowsofwar.avatar.common.entity.data.SyncableEntityReference;
import com.google.common.base.Optional;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityIcePrison extends AvatarEntity {
	
	public static final DataParameter<Optional<UUID>> SYNC_IMPRISONED = EntityDataManager
			.createKey(EntityIcePrison.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	
	private double normalBaseValue;
	private SyncableEntityReference<EntityLivingBase> imprisonedAttr;
	
	/**
	 * @param world
	 */
	public EntityIcePrison(World world) {
		super(world);
		imprisonedAttr = new SyncableEntityReference<>(this, SYNC_IMPRISONED);
		setSize(3, 4);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_IMPRISONED, Optional.absent());
	}
	
	public EntityLivingBase getImprisoned() {
		return imprisonedAttr.getEntity();
	}
	
	public void setImprisoned(EntityLivingBase entity) {
		imprisonedAttr.setEntity(entity);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		EntityLivingBase imprisoned = getImprisoned();
		if (imprisoned != null) {
			IAttributeInstance speed = imprisoned.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speed.getBaseValue() != 0) {
				normalBaseValue = speed.getBaseValue();
				speed.setBaseValue(0);
			}
		}
	}
	
	@Override
	public void setDead() {
		super.setDead();
		EntityLivingBase imprisoned = getImprisoned();
		if (imprisoned != null) {
			IAttributeInstance speed = imprisoned.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			speed.setBaseValue(normalBaseValue);
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		imprisonedAttr.readFromNBT(nbt);
		normalBaseValue = nbt.getDouble("NormalSpeed");
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		imprisonedAttr.writeToNBT(nbt);
		nbt.setDouble("NormalSpeed", normalBaseValue);
	}
	
	public static boolean isImprisoned(EntityLivingBase entity) {
		
		World world = entity.worldObj;
		return !world.getEntities(EntityIcePrison.class, prison -> prison.getImprisoned() == entity)
				.isEmpty();
		
	}
	
	public static void imprison(EntityLivingBase entity) {
		World world = entity.worldObj;
		EntityIcePrison prison = new EntityIcePrison(world);
		prison.setImprisoned(entity);
		prison.copyLocationAndAnglesFrom(entity);
		world.spawnEntityInWorld(prison);
	}
	
}
