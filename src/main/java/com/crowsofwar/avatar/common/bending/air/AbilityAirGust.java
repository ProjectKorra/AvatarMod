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

import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityAirGust extends AirAbility {
	
	public AbilityAirGust() {
		super("air_gust");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		
		if (!bender.consumeChi(STATS_CONFIG.chiAirGust)) return;
		
		Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
				Math.toRadians(entity.rotationPitch));
		Vector pos = Vector.getEyePos(entity);
		
		EntityAirGust gust = new EntityAirGust(world);
		gust.setVelocity(look.times(25));
		gust.setPosition(pos.x(), pos.y(), pos.z());
		gust.setOwner(entity);
		gust.setDestroyProjectiles(ctx.isMasterLevel(FIRST));
		gust.setAirGrab(ctx.isMasterLevel(SECOND));
		
		world.spawnEntity(gust);
	}
	
	@Override
	public int getCooldown(AbilityContext ctx) {
		return ctx.getLevel() >= 1 ? 30 : 60;
	}
	
	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirGust(this, entity, bender);
	}
	
}
