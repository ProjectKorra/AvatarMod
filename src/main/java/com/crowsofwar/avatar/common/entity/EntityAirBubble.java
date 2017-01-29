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

import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
public class EntityAirBubble extends AvatarEntity {
	
	public static final DataParameter<String> SYNC_OWNER = EntityDataManager.createKey(EntityAirBubble.class,
			DataSerializers.STRING);
	
	private final OwnerAttribute ownerAttr;
	
	public EntityAirBubble(World world) {
		super(world);
		setSize(2.5f, 2.5f);
		this.ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
	}
	
	public EntityPlayer getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityPlayer owner) {
		ownerAttr.setOwner(owner);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		EntityPlayer owner = getOwner();
		if (owner != null) {
			// setPosition(owner.posX, owner.posY, owner.posZ);
		}
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		double mult = 20;
		double velX = (this.posX - entity.posX) * mult;
		double velY = (this.posY - entity.posY) * mult;
		double velZ = (this.posZ - entity.posZ) * mult;
		// Need to use addVelocity() so avatar entities can detect it
		entity.motionX = entity.motionY = entity.motionZ = 0;
		// entity.addVelocity(velX, velY, velZ);
		entity.motionY = velY;
		entity.motionX = velX;
		entity.motionZ = velZ;
		if (entity instanceof AvatarEntity) {
			AvatarEntity avent = (AvatarEntity) entity;
			avent.velocity().set(velX, velY, velZ);
		}
		entity.isAirBorne = true;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ownerAttr.load(nbt);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.save(nbt);
	}
	
}
