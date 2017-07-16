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
package com.crowsofwar.avatar.common.entity.mob;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class EntityBender extends EntityCreature implements Bender {
	
	private BendingData data;
	
	/**
	 * @param world
	 */
	public EntityBender(World world) {
		super(world);
		data = new BendingData(category -> {}, () -> {}); // mc automatically saves entity
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		data.readFromNbt(nbt);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		data.writeToNbt(nbt);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		data.decrementCooldown();
	}
	
	@Override
	public EntityLivingBase getEntity() {
		return this;
	}
	
	@Override
	public BendingData getData() {
		return data;
	}
	
	@Override
	public boolean isCreativeMode() {
		return false;
	}
	
	@Override
	public boolean isFlying() {
		return false;
	}
	
	@Override
	public boolean isPlayer() {
		return false;
	}
	
	@Override
	public boolean consumeWaterLevel(int amount) {
		return false;
	}
	
}
