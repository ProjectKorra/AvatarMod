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
package com.crowsofwar.avatar.common.bending;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SmashGroundHandler extends TickHandler {
	
	@Override
	public boolean tick(BendingContext ctx) {
		
		EntityLivingBase target = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		
		if (target.isInWater() || target.onGround || bender.isFlying()) {
			
			if (target.onGround) {
				
				double range = 3;
				
				World world = target.world;
				AxisAlignedBB box = new AxisAlignedBB(target.posX - range, target.posY - range,
						target.posZ - range, target.posX + range, target.posY + range, target.posZ + range);
				
				List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase entity : nearby) {
					if (entity != target) {
						entity.attackEntityFrom(AvatarDamageSource.causeSmashDamage(entity, target), 5);
					}
				}
				
			}
			
			return true;
		}
		
		return false;
	}
	
}
