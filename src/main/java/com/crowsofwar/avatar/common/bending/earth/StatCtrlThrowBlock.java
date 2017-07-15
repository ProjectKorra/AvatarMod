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


import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowBlock extends StatusControl {
	
	public StatCtrlThrowBlock() {
		super(2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(BendingContext ctx) {
		
		BendingStyle controller = BendingStyles.get(Earthbending.ID);
		
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = entity.world;
		BendingData data = ctx.getData();
		
		EntityFloatingBlock floating = AvatarEntity.lookupControlledEntity(world, EntityFloatingBlock.class,
				entity);
		
		if (floating != null) {
			
			float yaw = (float) Math.toRadians(entity.rotationYaw);
			float pitch = (float) Math.toRadians(entity.rotationPitch);
			
			// Calculate force and everything
			double forceMult = data.getAbilityData(AbilityPickUpBlock.ID).getLevel() >= 1 //
					? 35 : 25;
			Vector lookDir = Vector.toRectangular(yaw, pitch);
			floating.velocity().add(lookDir.times(forceMult));
			floating.setBehavior(new FloatingBlockBehavior.Thrown());
			
			data.removeStatusControl(PLACE_BLOCK);
			
		}
		
		return true;
		
	}
	
}
