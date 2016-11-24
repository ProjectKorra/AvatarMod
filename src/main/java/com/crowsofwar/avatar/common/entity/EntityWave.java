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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWave extends Entity {
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	
	private EntityPlayer owner;
	
	public EntityWave(World world) {
		super(world);
		//@formatter:off
		this.internalVelocity = new BackedVector(x -> this.motionX = x / 20, y -> this.motionY = y / 20, z -> this.motionZ = z / 20,
				() -> this.motionX * 20, () -> this.motionY * 20, () -> this.motionZ * 20);
		this.internalPosition = new Vector();
		
		setSize(2f, 2);
		
	}
	
	@Override
	public void onUpdate() {
		
		Vector move = velocity().dividedBy(20);
		Vector newPos = getVecPosition().add(move);
		setPosition(newPos.x(), newPos.y(), newPos.z());
		
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(), entity -> entity != owner);
			for (Entity entity : collided) {
				Vector motion = velocity().dividedBy(20).times(6);
				motion.setY(0.4);
				entity.addVelocity(motion.x(), motion.y(), motion.z());
				entity.attackEntityFrom(AvatarDamageSource.causeWaveDamage(entity, owner), 9);
			}
			if (!collided.isEmpty()) setDead();
		}
		
		if (ticksExisted > 7000 || worldObj.getBlockState(getPosition()).getBlock() != Blocks.WATER) setDead();
		
	}
	
	public Vector getVecPosition() {
		return internalPosition.set(posX, posY, posZ);
	}
	
	/**
	 * Get velocity in m/s. Any modifications to this vector will modify the entity motion fields.
	 */
	public Vector velocity() {
		return internalVelocity;
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		// TODO Save/load waves??
		setDead();
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
}
