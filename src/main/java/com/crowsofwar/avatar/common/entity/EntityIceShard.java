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

import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
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
			setDead();
		}
		
		moveEntity(MoverType.SELF, motionX, motionY, motionZ);
		
	}
	
	/**
	 * Sets the shard's rotations and motion to the given rotations/speed.
	 * Parameters should be in radians.
	 * 
	 * @param speed
	 *            Speed in m/s
	 */
	public void aim(float yaw, float pitch, double speed) {
		rotationYaw = yaw;
		rotationPitch = pitch;
		
		Vector velocity = Vector.toRectangular(yaw, pitch).times(speed).dividedBy(20);
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
