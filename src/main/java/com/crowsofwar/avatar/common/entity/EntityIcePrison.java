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

import com.crowsofwar.avatar.common.entity.data.SyncedEntity;
import com.google.common.base.Optional;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityIcePrison extends AvatarEntity {
	
	public static final DataParameter<Optional<UUID>> SYNC_IMPRISONED = EntityDataManager
			.createKey(EntityIcePrison.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	
	public static final int IMPRISONED_TIME = 100;
	
	private double normalBaseValue;
	private SyncedEntity<EntityLivingBase> imprisonedAttr;
	
	/**
	 * @param world
	 */
	public EntityIcePrison(World world) {
		super(world);
		imprisonedAttr = new SyncedEntity<>(this, SYNC_IMPRISONED);
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
			imprisoned.posX = this.posX;
			imprisoned.posY = this.posY;
			imprisoned.posZ = this.posZ;
		}
		if (ticksExisted >= IMPRISONED_TIME) {
			setDead();
			
			if (!world.isRemote && imprisoned != null) {
				world.playSound(null, imprisoned.posX, imprisoned.posY, imprisoned.posZ,
						SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1, 1);
				imprisoned.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"),
						60, 1, false, false));
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
		imprisonedAttr.readFromNbt(nbt);
		normalBaseValue = nbt.getDouble("NormalSpeed");
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		imprisonedAttr.writeToNbt(nbt);
		nbt.setDouble("NormalSpeed", normalBaseValue);
	}
	
	public static boolean isImprisoned(EntityLivingBase entity) {
		
		return getPrison(entity) != null;
		
	}
	
	/**
	 * Get the prison holding that entity, or null if the entity is not
	 * imprisoned
	 */
	public static EntityIcePrison getPrison(EntityLivingBase entity) {
		
		World world = entity.world;
		List<EntityIcePrison> prisons = world.getEntities(EntityIcePrison.class,
				prison -> prison.getImprisoned() == entity);
		
		return prisons.isEmpty() ? null : prisons.get(0);
		
	}
	
	public static void imprison(EntityLivingBase entity) {
		World world = entity.world;
		EntityIcePrison prison = new EntityIcePrison(world);
		prison.setImprisoned(entity);
		prison.copyLocationAndAnglesFrom(entity);
		world.spawnEntity(prison);
	}
	
}
