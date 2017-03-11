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

package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityRavine extends EarthAbility {
	
	/**
	 * @param controller
	 */
	public AbilityRavine() {
		super("ravine");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		if (ctx.consumeChi(STATS_CONFIG.chiRavine)) {
			
			AbilityData abilityData = ctx.getData().getAbilityData(this);
			float xp = abilityData.getTotalXp();
			
			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			
			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			
			EntityRavine ravine = new EntityRavine(world);
			ravine.setOwner(entity);
			ravine.setPosition(entity.posX, entity.posY, entity.posZ);
			ravine.velocity().set(look.times(10));
			ravine.setDamageMult(.75f + xp / 100);
			world.spawnEntityInWorld(ravine);
			
			BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.Created(ravine, entity));
			
		}
		
	}
	
}
