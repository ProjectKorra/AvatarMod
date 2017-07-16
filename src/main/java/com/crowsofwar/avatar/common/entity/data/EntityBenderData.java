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
package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbstractBendingData;
import com.crowsofwar.avatar.common.data.DataCategory;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityBenderData extends AbstractBendingData {
	
	private final EntityLivingBase entity;
	
	public EntityBenderData(EntityLivingBase entity) {
		this.entity = entity;
	}
	
	@Override
	public void save(DataCategory category) {}

}
