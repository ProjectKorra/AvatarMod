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

import java.util.List;

import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityIceShard extends Entity {
	
	public EntityIceShard(World worldIn) {
		super(worldIn);
		setSize(0.5f, 0.5f);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		motionY -= 1.0 / 20;
		if (onGround) {
			shatter();
		}
		
		moveEntity(MoverType.SELF, motionX, motionY, motionZ);
		
		Vector direction = Vector.toRectangular(Math.toRadians(rotationYaw), Math.toRadians(rotationPitch));
		List<Entity> collidedEntities = Raytrace.entityRaytrace(worldObj, new Vector(this), direction, 4,
				entity -> !(entity instanceof EntityPlayer));
		
		if (!collidedEntities.isEmpty()) {
			
			Entity collided = collidedEntities.get(0);
			
			DamageSource source = DamageSource.anvil;
			collided.attackEntityFrom(source, 5);
			
			shatter();
		}
		
	}
	
	/**
	 * Breaks the ice shard and plays particle/sound effects
	 */
	private void shatter() {
		if (!worldObj.isRemote) {
			float volume = 0.3f + rand.nextFloat() * 0.3f;
			float pitch = 1.1f + rand.nextFloat() * 0.2f;
			worldObj.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS,
					volume, pitch);
		}
		
		setDead();
	}
	
	/**
	 * Sets the shard's rotations and motion to the given rotations/speed.
	 * Parameters should be in degrees.
	 * 
	 * @param speed
	 *            Speed in m/s
	 */
	public void aim(float yaw, float pitch, double speed) {
		rotationYaw = yaw;
		rotationPitch = pitch;
		
		double yawRad = Math.toRadians(yaw);
		double pitchRad = Math.toRadians(pitch);
		Vector velocity = Vector.toRectangular(yawRad, pitchRad).times(speed).dividedBy(20);
		motionX = velocity.x();
		motionY = velocity.y();
		motionZ = velocity.z();
		
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
}
