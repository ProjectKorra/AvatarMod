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

package com.crowsofwar.avatar.common.bending.water;

import java.util.List;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowWater extends StatusControl {
	
	public StatCtrlThrowWater() {
		super(3, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(BendingContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = data.getAbilityData(Ability.ABILITY_WATER_ARC);
		
		int lvl = abilityData.getLevel();
		double velocity = 6;
		if (lvl >= 2) {
			velocity = 10;
		}
		if (abilityData.isMasterPath(AbilityTreePath.SECOND)) {
			velocity = 8;
		}
		
		AxisAlignedBB boundingBox = new AxisAlignedBB(entity.posX - 5, entity.posY - 5, entity.posZ - 5,
				entity.posX + 5, entity.posY + 5, entity.posZ + 5);
		List<EntityWaterArc> existing = world.getEntitiesWithinAABB(EntityWaterArc.class, boundingBox,
				arc -> arc.getOwner() == entity
						&& arc.getBehavior() instanceof WaterArcBehavior.PlayerControlled);
		
		for (EntityWaterArc arc : existing) {
			arc.setBehavior(new WaterArcBehavior.Thrown());
			
			Vector force = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
					Math.toRadians(entity.rotationPitch));
			force.mul(velocity);
			arc.velocity().add(force);
			arc.setBehavior(new WaterArcBehavior.Thrown());
			
		}
		
		return true;
		
	}
	
}
