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

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BenderInfo;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAirBubble extends AvatarEntity {
	
	public static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityAirBubble.class, AvatarDataSerializers.SERIALIZER_BENDER);
	public static final DataParameter<Integer> SYNC_DISSIPATE = EntityDataManager
			.createKey(EntityAirBubble.class, DataSerializers.VARINT);
	public static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityAirBubble.class,
			DataSerializers.FLOAT);
	
	public static final UUID SLOW_ATTR_ID = UUID.fromString("40354c68-6e88-4415-8a6b-e3ddc56d6f50");
	public static final AttributeModifier SLOW_ATTR = new AttributeModifier(SLOW_ATTR_ID,
			"airbubble_slowness", -.3, 2);
	
	private final OwnerAttribute ownerAttr;
	
	public EntityAirBubble(World world) {
		super(world);
		setSize(2.5f, 2.5f);
		this.ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DISSIPATE, 0);
		dataManager.register(SYNC_HEALTH, 20f);
	}
	
	@Override
	public EntityLivingBase getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityLivingBase owner) {
		ownerAttr.setOwner(owner);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		EntityLivingBase ownerEnt = ownerAttr.getOwner();
		Bender ownerBender = ownerAttr.getOwnerBender();
		
		if (ownerEnt != null) {
			setPosition(ownerEnt.posX, ownerEnt.posY, ownerEnt.posZ);
			if (ownerEnt.isDead) {
				dissipateSmall();
			}
			
			if (!isDissipating()) {
				IAttributeInstance attribute = ownerEnt
						.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				if (attribute.getModifier(SLOW_ATTR_ID) == null) {
					attribute.applyModifier(SLOW_ATTR);
				}
				if (!ownerEnt.onGround && !ownerEnt.isInWater() && !ownerBender.isFlying())
					ownerEnt.motionY += .03;
			}
			
		}
		if (isDissipatingLarge()) {
			setDissipateTime(getDissipateTime() + 1);
			float mult = 1 + getDissipateTime() / 10f;
			setSize(2.5f * mult, 2.5f * mult);
			if (getDissipateTime() >= 10) {
				setDead();
			}
		}
		if (isDissipatingSmall()) {
			setDissipateTime(getDissipateTime() - 1);
			float mult = 1 + getDissipateTime() / 40f;
			setSize(2.5f * mult, 2.5f * mult);
			if (getDissipateTime() <= -10) {
				setDead();
			}
		}
	}
	
	@Override
	public void setDead() {
		super.setDead();
		EntityLivingBase owner = getOwner();
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
		if (entity instanceof AvatarEntity || entity instanceof EntityArrow) return;
		
		double mult = -2;
		if (isDissipatingLarge()) mult = -4;
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
		AvatarUtils.afterVelocityAdded(entity);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ownerAttr.load(nbt);
		setDissipateTime(nbt.getInteger("Dissipate"));
		setHealth(nbt.getFloat("Health"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.save(nbt);
		nbt.setInteger("Dissipate", getDissipateTime());
		nbt.setFloat("Health", getHealth());
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!isEntityInvulnerable(source)) {
			setHealth(getHealth() - (float) amount);
			setBeenAttacked();
			return true;
		}
		return false;
	}
	
	public int getDissipateTime() {
		return dataManager.get(SYNC_DISSIPATE);
	}
	
	public void setDissipateTime(int dissipate) {
		dataManager.set(SYNC_DISSIPATE, dissipate);
	}
	
	public float getHealth() {
		return dataManager.get(SYNC_HEALTH);
	}
	
	public void setHealth(float health) {
		dataManager.set(SYNC_HEALTH, health);
		if (health <= 0) dissipateSmall();
	}
	
	public void dissipateLarge() {
		if (!isDissipating()) setDissipateTime(1);
		removeStatCtrl();
	}
	
	public void dissipateSmall() {
		if (!isDissipating()) setDissipateTime(-1);
		removeStatCtrl();
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
	
	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Bender.create(getOwner()).getData();
			data.removeStatusControl(StatusControl.BUBBLE_EXPAND);
			data.removeStatusControl(StatusControl.BUBBLE_CONTRACT);
			data.sync();
			
			IAttributeInstance attribute = getOwner()
					.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (attribute.getModifier(SLOW_ATTR_ID) != null) {
				attribute.removeModifier(SLOW_ATTR);
			}
		}
	}
	
}
