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

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
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
	public boolean execute(AbilityContext ctx) {
		
		BendingController controller = BendingManager.getBending(BendingType.EARTHBENDING);
		
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = entity.worldObj;
		BendingData data = ctx.getData();
		
		EntityFloatingBlock floating = AvatarEntity.lookupEntity(ctx.getWorld(), EntityFloatingBlock.class,
				fb -> fb.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled
						&& fb.getOwner() == ctx.getBenderEntity());
		
		if (floating != null) {
			
			float yaw = (float) Math.toRadians(entity.rotationYaw);
			float pitch = (float) Math.toRadians(entity.rotationPitch);
			
			// Calculate force and everything
			Vector lookDir = Vector.toRectangular(yaw, pitch);
			floating.velocity().add(lookDir.times(35));
			floating.setBehavior(new FloatingBlockBehavior.Thrown());
			
			controller.post(new FloatingBlockEvent.BlockThrown(floating, entity));
			
			data.removeStatusControl(PLACE_BLOCK);
			
		}
		
		return true;
		
	}
	
}
