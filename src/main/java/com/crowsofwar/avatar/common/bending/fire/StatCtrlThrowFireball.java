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
package com.crowsofwar.avatar.common.bending.fire;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

import java.util.List;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowFireball extends StatusControl {
	
	public StatCtrlThrowFireball() {
		super(10, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		
		double size = 6;
		Vec3d playerPos = entity.getPositionVector();
		AxisAlignedBB boundingBox = new AxisAlignedBB(playerPos.subtract(size, size, size),
				playerPos.addVector(size, size, size));
		
		List<EntityFireball> fireballs = world.getEntitiesWithinAABB(EntityFireball.class, //
				boundingBox, //
				fireball -> fireball.getOwner() == entity);
		
		for (EntityFireball fireball : fireballs) {
			fireball.velocity().add(Vector.getLookRectangular(entity).mul(25));
			fireball.setBehavior(new FireballBehavior.Thrown());
		}
		
		return true;
	}
	
}
