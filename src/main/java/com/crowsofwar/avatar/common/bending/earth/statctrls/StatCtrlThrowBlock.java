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

package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import static com.crowsofwar.avatar.util.data.StatusControlController.PLACE_BLOCK;

import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthControl;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowBlock extends StatusControl {

	public StatCtrlThrowBlock() {
		super(2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		World world = entity.world;
		BendingData data = ctx.getData();
		AbilityData abilityData = AbilityData.get(entity, new AbilityEarthControl().getName());

		EntityFloatingBlock floating = AvatarEntity.lookupControlledEntity(world, EntityFloatingBlock.class,
				entity);

		if (floating != null) {
			float yaw = (float) Math.toRadians(entity.rotationYaw);
			float pitch = (float) Math.toRadians(entity.rotationPitch);

			// Calculate force and everything
			double forceMult = 20;
			if (abilityData.getLevel() == 1)
				forceMult += 8;
			if (abilityData.getLevel() == 2)
				forceMult += 14;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST))
				forceMult += 16;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND))
				forceMult += 30;

			Vector direction;
			Vec3d look = entity.getLook(1.0F);
			Vec3d pos = entity.getPositionEyes(1.0F);
			
			//Drillgon200: Raytrace from the bender's line of sight and if it hit anything, use the vector from the 
			//block to the hit point for the motion vector rather than the look vector. This improves accuracy when the
			//block isn't directly in front of the bender.
			RayTraceResult r = Raytrace.rayTrace(world, pos, look.scale(75).add(pos), 0, false, true, false, 
					Entity.class, e -> e instanceof EntityFloatingBlock || e == entity);
			
			if(r != null && r.hitVec != null){
				Vec3d dir = r.hitVec.subtract(floating.getPositionVector()).normalize();
				direction = new Vector(dir.x, dir.y, dir.z);
			} else {
				direction = Vector.toRectangular(yaw, pitch);
			}
			
			floating.setVelocity(direction.times(forceMult));
			floating.setBehavior(new FloatingBlockBehavior.Thrown());

			data.removeStatusControl(PLACE_BLOCK);

			return true;

		}

		return false;

	}

}
