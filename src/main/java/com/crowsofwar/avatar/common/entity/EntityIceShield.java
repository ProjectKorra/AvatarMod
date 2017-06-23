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

import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityIceShield extends AvatarEntity {
	
	public static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityIceShield.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private final OwnerAttribute ownerAttr;
	private double normalBaseValue;
	
	public EntityIceShield(World world) {
		super(world);
		ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
	}
	
	public void shatter() {
		System.out.println("Shattering");
		
		worldObj.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1,
				1);
		setDead();
		
	}
	
	@Override
	public EntityLivingBase getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityLivingBase owner) {
		ownerAttr.setOwner(owner);
	}
	
	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		EntityLivingBase owner = getOwner();
		if (owner != null) {
			IAttributeInstance speed = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speed.getBaseValue() != 0) {
				normalBaseValue = speed.getBaseValue();
				speed.setBaseValue(0);
			}
			owner.posX = this.posX;
			owner.posY = this.posY;
			owner.posZ = this.posZ;
		}
	}
	
	@Override
	public void setDead() {
		super.setDead();
		EntityLivingBase owner = getOwner();
		if (owner != null) {
			IAttributeInstance speed = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speed.getBaseValue() == 0) {
				speed.setBaseValue(normalBaseValue);
			}
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ownerAttr.load(nbt);
		normalBaseValue = nbt.getDouble("NormalBaseValue");
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.save(nbt);
		nbt.setDouble("NormalBaseValue", normalBaseValue);
	}
	
}
