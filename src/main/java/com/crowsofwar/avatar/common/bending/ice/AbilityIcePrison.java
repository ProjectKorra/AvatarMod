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

import java.util.List;
import java.util.function.Predicate;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityIcePrison;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityIcePrison extends BendingAbility {
	
	public AbilityIcePrison() {
		super(BendingManager.ID_ICEBENDING, "ice_prison");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		if (ctx.consumeChi(ConfigStats.STATS_CONFIG.chiPrison)) {
			
			EntityLivingBase caster = ctx.getBenderEntity();
			World world = ctx.getWorld();
			Vector start = Vector.getEyePos(caster);
			Vector direction = Vector.getLookRectangular(caster);
			
			Predicate<Entity> filter = entity -> entity != caster && entity instanceof EntityLivingBase;
			List<Entity> hit = Raytrace.entityRaytrace(world, start, direction, 10, filter);
			
			if (!hit.isEmpty()) {
				EntityLivingBase prisoner = (EntityLivingBase) hit.get(0);
				if (!world.isRemote) {
					EntityIcePrison.imprison(prisoner);
				}
			}
			
		}
		
	}
	
}
