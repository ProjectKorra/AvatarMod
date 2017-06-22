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

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
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
		System.out.println("Imprision");
		
		EntityLivingBase entity = ctx.getBenderEntity();
		Vector start = Vector.getEyePos(entity);
		Vector end = start.plus(Vector.getLookRectangular(entity).times(10));
		
		raytraceEntities(ctx.getWorld(), start, end);
		
		// Item
	}
	
	private List<Entity> raytraceEntities(World world, Vector start, Vector end) {
		
		List<Entity> hit = new ArrayList<>();
		
		AxisAlignedBB aabb = new AxisAlignedBB(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		
		for (Entity entity : entities) {
			AxisAlignedBB collisionBox = entity.getEntityBoundingBox();
			RayTraceResult result = collisionBox.calculateIntercept(start.toMinecraft(), end.toMinecraft());
			if (result != null) {
				System.out.println("Hit " + entity);
				hit.add(entity);
			}
		}
		
		return hit;
		
	}
	
}
