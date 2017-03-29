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

import javax.annotation.Nullable;

import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityBisonSaddle extends AvatarEntity {
	
	private static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityBisonSaddle.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private OwnerAttribute bisonAttr;
	
	/**
	 * @param world
	 */
	public EntityBisonSaddle(World world) {
		super(world);
		bisonAttr = new OwnerAttribute(this, SYNC_OWNER);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		bisonAttr.load(nbt);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		bisonAttr.save(nbt);
	}
	
	@Nullable
	public EntitySkyBison getBison() {
		EntityLivingBase bison = bisonAttr.getOwner();
		if (bison != null && !(bison instanceof EntitySkyBison)) {
			bison = null;
			bisonAttr.setOwner(null);
		}
		return (EntitySkyBison) bison;
	}
	
	public void setBison(@Nullable EntitySkyBison bison) {
		bisonAttr.setOwner(bison);
	}
	
}
