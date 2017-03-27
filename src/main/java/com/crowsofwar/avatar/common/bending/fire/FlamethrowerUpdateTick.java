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

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.*;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class FlamethrowerUpdateTick extends TickHandler {
	
	@Override
	public boolean tick(BendingContext ctx) {
		
		BendingData data = ctx.getData();
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		
		if (entity.ticksExisted % 3 < 2) {
			
			Chi chi = data.chi();
			float required = STATS_CONFIG.chiFlamethrowerSecond / 20f;
			
			boolean infinite = bender.isCreativeMode() && CHI_CONFIG.infiniteInCreative;
			
			if (chi.getAvailableChi() >= required || infinite) {
				
				if (!infinite) {
					chi.changeTotalChi(-required);
					chi.changeAvailableChi(-required);
				}
				
				Vector look = getLookRectangular(entity);
				Vector eye = getEyePos(entity);
				
				World world = ctx.getWorld();
				
				EntityFlames flames = new EntityFlames(world, entity);
				flames.velocity().set(look.times(10).plus(getVelocityMpS(entity)));
				flames.setPosition(eye.x(), eye.y(), eye.z());
				world.spawnEntityInWorld(flames);
				
				if (entity.ticksExisted % 3 == 0) world.playSound(null, entity.getPosition(),
						SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, 0.8f);
				
			} else {
				return true;
			}
		}
		
		return false;
		
	}
	
}
