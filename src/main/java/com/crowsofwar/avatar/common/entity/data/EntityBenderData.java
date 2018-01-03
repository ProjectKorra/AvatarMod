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

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.DataCategory;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author CrowsOfWar
 */
public class EntityBenderData extends BendingData {

	private final EntityLivingBase entity;

	public EntityBenderData(EntityLivingBase entity) {
		super(dataCategory -> {
		}, () -> {
		});
		// Entities are saved automatically; no dirty flag etc.

		this.entity = entity;
	}

	@Override
	public void save(DataCategory category) {
	}

}
