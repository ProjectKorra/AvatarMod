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
package com.crowsofwar.avatar.common.bending.ice;

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class IceWalkHandler extends TickHandler {
	
	@Override
	public boolean tick(BendingContext ctx) {
		Bender bender = ctx.getBender();
		if (!ctx.getWorld().isRemote && bender.consumeChi(2.0f / 20)) {
			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			EnchantmentFrostWalker.freezeNearby(entity, world, entity.getPosition(), 1);
			return false;
		}
		return true;
	}
	
}
