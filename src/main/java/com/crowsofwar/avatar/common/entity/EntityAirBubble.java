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

import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
	
	public static final UUID SLOW_ATTR_ID = UUID.fromString("40354c68-6e88-4415-8a6b-e3ddc56d6f50");
	public static final AttributeModifier SLOW_ATTR = new AttributeModifier(SLOW_ATTR_ID,
			"airbubble_slowness", -.3, 2);
	
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
			setPosition(owner.posX, owner.posY, owner.posZ);
			if (owner.isSneaking() || owner.isDead) {
				setDead();
			}
			IAttributeInstance attribute = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (attribute.getModifier(SLOW_ATTR_ID) == null) {
				attribute.applyModifier(SLOW_ATTR);
			}
		}
	}
	
	@Override
	public void setDead() {
		super.setDead();
		EntityPlayer owner = getOwner();
		if (owner != null) {
			IAttributeInstance attribute = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (attribute.getModifier(SLOW_ATTR_ID) != null) {
				attribute.removeModifier(SLOW_ATTR);
			}
		}
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity == getOwner()) return;
		
		double mult = -2;
		Vector vel = new Vector(this.posX - entity.posX, this.posY - entity.posY, this.posZ - entity.posZ);
		vel.normalize();
		vel.mul(mult);
		vel.add(0, .3f, 0);
		
		double velX = vel.x(), velY = vel.y(), velZ = vel.z();
		
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
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
}
