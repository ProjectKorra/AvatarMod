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
package com.crowsofwar.avatar.common.bending.air;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.data.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirblade;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityAirblade extends AirAbility {
	
	public AbilityAirblade() {
		super("airblade");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		
		if (!ctx.consumeChi(STATS_CONFIG.chiAirblade)) return;
		
		double x = player.rotationPitch;
		boolean flip = false;
		if (x < 0) {
			x = -x;
			flip = true;
		}
		double pitch = -1.0 / ((.015 * x + .1825) * (.015 * x + .1825)) + 30;
		if (flip) pitch = -pitch;
		pitch = Math.toRadians(pitch);
		
		Vector look = Vector.toRectangular(Math.toRadians(player.rotationYaw), pitch);
		Vector spawnAt = Vector.getEntityPos(player).add(look).add(0, 1, 0);
		spawnAt.add(look);
		
		float xp = ctx.getData().getAbilityData(this).getTotalXp();
		
		EntityAirblade airblade = new EntityAirblade(world);
		airblade.setPosition(spawnAt.x(), spawnAt.y(), spawnAt.z());
		airblade.velocity().set(look.times(25));
		airblade.setDamage(STATS_CONFIG.airbladeSettings.damage * (1 + xp * .015f));
		airblade.setOwner(player);
		world.spawnEntityInWorld(airblade);
		
	}
	
}
