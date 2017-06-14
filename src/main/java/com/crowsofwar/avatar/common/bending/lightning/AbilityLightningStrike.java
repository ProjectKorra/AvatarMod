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
package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityLightningStrike extends BendingAbility {
	
	public AbilityLightningStrike() {
		super(BendingManager.ID_LIGHTNINGBENDING, "lightning_strike");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		
		Raytrace.Result hit = Raytrace.getTargetBlock(entity, 10);
		if (hit.hitSomething()) {
			World world = ctx.getWorld();
			BlockPos hitAt = hit.getPos().toBlockPos();
			
			EntityLightningBolt bolt = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ,
					false);
			world.addWeatherEffect(bolt);
			
		}
		
	}
	
}
