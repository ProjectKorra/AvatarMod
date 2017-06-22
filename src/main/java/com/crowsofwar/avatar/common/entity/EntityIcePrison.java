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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityIcePrison extends AvatarEntity {
	
	public static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityIcePrison.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private OwnerAttribute ownerAttr;
	
	/**
	 * @param world
	 */
	public EntityIcePrison(World world) {
		super(world);
		ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		setSize(3, 4);
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
	
	public static boolean isImprisoned(EntityLivingBase entity) {
		return lookupControlledEntity(entity.worldObj, EntityIcePrison.class, entity) != null;
	}
	
	public static void imprison(EntityLivingBase entity) {
		World world = entity.worldObj;
		EntityIcePrison prison = new EntityIcePrison(world);
		prison.setOwner(entity);
		prison.copyLocationAndAnglesFrom(entity);
		world.spawnEntityInWorld(prison);
	}
	
}
