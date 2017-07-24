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

import com.crowsofwar.avatar.common.data.BenderInfo;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

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
		
		world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1,
				1);
		setDead();
		
		EntityLivingBase owner = getOwner();
		
		// Shoot arrows at mobs
		
		int arrowsLeft = 12;
		
		double halfRange = 20;
		AxisAlignedBB aabb = new AxisAlignedBB(//
				owner.posX - halfRange, owner.posY - halfRange, owner.posZ - halfRange, //
				owner.posX + halfRange, owner.posY + halfRange, owner.posZ + halfRange);
		List<EntityMob> targets = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		
		int arrowsAtMobs = Math.min(targets.size(), 5);
		for (int i = 0; i < arrowsAtMobs; i++) {
			shootArrowAt(targets.get(i));
		}
		arrowsLeft -= arrowsAtMobs;
		
		shootArrowsAround(owner, 4, new float[] { 20, 0, -30 }, arrowsLeft);
		
	}
	
	private void shootArrowAt(Entity target) {
		
		EntityLivingBase owner = getOwner();
		Vector targetPos = Vector.getEyePos(target);
		Vector ownerPos = Vector.getEyePos(owner);
		
		Vector direction = Vector.getRotationTo(ownerPos, targetPos);
		float yaw = (float) Math.toDegrees(direction.y());
		
		double horizDist = targetPos.copy().setY(0).dist(ownerPos.copy().setY(0));
		double vertDist = targetPos.y() - ownerPos.y();
		float pitch = (float) Math.toDegrees(Vector.getProjectileAngle(30, 20, horizDist, vertDist));
		
		EntityIceShard shard = new EntityIceShard(world);
		shard.setLocationAndAngles(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ, yaw, pitch);
		shard.aim(yaw, pitch, 30);
		world.spawnEntity(shard);
		
	}
	
	/**
	 * Shoot arrows around the entity.
	 * 
	 * @param yawAngles
	 *            Spacing for yaw angles
	 * @param pitchAngles
	 *            All of the pitch angles
	 * @param arrowsLeft
	 *            Limit the number of arrows to shoot. Note that setting this
	 *            very high won't increase arrows shot since this only limits
	 *            the arrows shot
	 */
	private void shootArrowsAround(EntityLivingBase shooter, int yawAngles, float[] pitchAngles,
			int arrowsLeft) {
		for (int i = 0; i < yawAngles; i++) {
			float yaw = 360f / yawAngles * i;
			for (int j = 0; j < pitchAngles.length; j++) {
				
				if (arrowsLeft == 0) {
					break;
				}
				
				float pitch = pitchAngles[j];
				
				EntityIceShard shard = new EntityIceShard(world);
				shard.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ,
						0, 0);
				shard.aim(yaw + shooter.rotationYaw, pitch + shooter.rotationPitch, 53);
				world.spawnEntity(shard);
				
				arrowsLeft--;
				
			}
		}
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
