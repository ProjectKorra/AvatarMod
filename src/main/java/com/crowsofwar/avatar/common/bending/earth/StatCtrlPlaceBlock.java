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
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.block.SoundType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlPlaceBlock extends StatusControl {
	
	public StatCtrlPlaceBlock() {
		super(1, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR);
		
		requireRaytrace(-1, true);
		
	}
	
	@Override
	public boolean execute(BendingContext ctx) {
		
		BendingStyle controller = BendingStyles.get(Earthbending.ID);
		
		BendingData data = ctx.getData();
		
		EntityFloatingBlock floating = AvatarEntity.lookupEntity(ctx.getWorld(), EntityFloatingBlock.class,
				fb -> fb.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled
						&& fb.getOwner() == ctx.getBenderEntity());
		
		if (floating != null) {
			// TODO Verify look at block
			VectorI looking = ctx.getClientLookBlock();
			EnumFacing lookingSide = ctx.getLookSide();
			if (looking != null && lookingSide != null) {
				looking.offset(lookingSide);
				
				floating.setBehavior(new FloatingBlockBehavior.Place(looking.toBlockPos()));
				Vector force = looking.precision().minus(new Vector(floating));
				force.normalize();
				floating.addVelocity(force);
				
				SoundType sound = floating.getBlock().getSoundType();
				if (sound != null) {
					floating.world.playSound(null, floating.getPosition(), sound.getPlaceSound(),
							SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
				}
				
				data.removeStatusControl(THROW_BLOCK);
				
				data.getAbilityData(AbilityPickUpBlock.ID).addXp(SKILLS_CONFIG.blockPlaced);
				
				return true;
			}
			return false;
		}
		
		return true;
		
	}
	
}
