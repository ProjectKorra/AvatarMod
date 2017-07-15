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

import java.util.UUID;

import com.crowsofwar.avatar.common.bending.Ability;
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
public class AbilityLightningStrike extends Ability {
	
	public static final UUID ID = UUID.fromString("67e9390e-38d5-4858-ab99-31b6a206eaf6");
	
	public AbilityLightningStrike() {
		super(LightningBending.ID, "lightning_strike");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		
		Raytrace.Result hit = Raytrace.getTargetBlock(entity, 10);
		if (hit.hitSomething()) {
			World world = ctx.getWorld();
			BlockPos hitAt = hit.getPos().toBlockPos();
			
			EntityLightningBolt bolt = new EntityLightningBolt(world, hitAt.getX(), hitAt.getY(),
					hitAt.getZ(), false);
			world.addWeatherEffect(bolt);
			
		}
		
	}
	
	@Override
	public UUID getId() {
		return ID;
	}
	
}
