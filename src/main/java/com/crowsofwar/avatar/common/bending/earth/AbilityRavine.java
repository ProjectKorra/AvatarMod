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

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
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
		
		float chi = STATS_CONFIG.chiRavine;
		if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
			chi *= 2;
		}
		
		if (ctx.consumeChi(chi)) {
			
			AbilityData abilityData = ctx.getData().getAbilityData(this);
			float xp = abilityData.getTotalXp();
			
			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			
			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			
			double mult = ctx.getLevel() >= 1 ? 14 : 8;
			
			EntityRavine ravine = new EntityRavine(world);
			ravine.setOwner(entity);
			ravine.setPosition(entity.posX, entity.posY, entity.posZ);
			ravine.velocity().set(look.times(mult));
			ravine.setDamageMult(.75f + xp / 100);
			ravine.setDistance(ctx.getLevel() >= 2 ? 16 : 10);
			ravine.setBreakBlocks(ctx.isMasterLevel(AbilityTreePath.FIRST));
			world.spawnEntityInWorld(ravine);
			
		}
		
	}
	
}
