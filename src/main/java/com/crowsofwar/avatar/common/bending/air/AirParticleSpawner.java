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

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleType;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AirParticleSpawner extends TickHandler {
	
	private static final ParticleSpawner particles = new ClientParticleSpawner();
	
	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase target = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		
		Vector pos = Vector.getEntityPos(target);
		pos.setY(pos.y() + 1.3);
		
		particles.spawnParticles(target.worldObj, ParticleType.AIR, 1, 1, pos, new Vector(0.7, 0.2, 0.7));
		
		if (target.isInWater() || target.onGround || bender.isFlying()) {
			
			if (target.onGround) {
				System.out.println("Smash!");
			}
			bender.getData().setSmashGround(false);
			
			return true;
		}
		
		return false;
		
	}
	
}
