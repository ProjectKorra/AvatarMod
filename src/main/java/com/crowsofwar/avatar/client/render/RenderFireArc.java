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

package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.ControlPoint;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class RenderFireArc extends RenderArc {
	
	private static final ResourceLocation fire = new ResourceLocation("avatarmod",
			"textures/entity/fire-ribbon.png");
	
	public RenderFireArc(RenderManager renderManager) {
		super(renderManager);
		enableFullBrightness();
	}
	
	@Override
	protected ResourceLocation getTexture() {
		return fire;
	}
	
	@Override
	protected void onDrawSegment(EntityArc arc, ControlPoint first, ControlPoint second) {
		// Parametric equation
		// For parameters, they will be same as linear equation: y = mx+b
		
		Vector m = second.position().minus(first.position());
		Vector b = first.position();
		double x = Math.random(); // 0-1
		Vector spawnAt = m.times(x).plus(b);
		Vector velocity = new Vector(0, 0, 0);
		
		// TODO [1.10] Re-introduce flame particle
		arc.worldObj.spawnParticle(EnumParticleTypes.FLAME, spawnAt.x(), spawnAt.y(), spawnAt.z(),
				velocity.x() / 20, 0.05, velocity.z() / 20);
		// AvatarParticles.createParticle(arc.worldObj, spawnAt.xCoord,
		// spawnAt.yCoord,
		// spawnAt.zCoord,
		// velocity.xCoord / 20, 0.05, velocity.zCoord / 20);
		
	}
	
}
