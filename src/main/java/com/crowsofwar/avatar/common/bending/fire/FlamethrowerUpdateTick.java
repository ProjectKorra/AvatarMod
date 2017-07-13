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

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_FLAMETHROWER;
import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getVelocityMpS;
import static java.lang.Math.toRadians;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
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
		
		AbilityData abilityData = data.getAbilityData(ABILITY_FLAMETHROWER);
		AbilityTreePath path = abilityData.getPath();
		float totalXp = abilityData.getTotalXp();
		int level = abilityData.getLevel();
		
		int flamesPerSecond = level == 0 ? 6 : 10;
		if (level == 3 && path == AbilityTreePath.FIRST) {
			flamesPerSecond = 15;
		}
		if (level == 3 && path == AbilityTreePath.SECOND) {
			flamesPerSecond = 8;
		}
		
		if (!entity.world.isRemote && Math.random() < flamesPerSecond / 20.0) {
			
			Chi chi = data.chi();
			float required = STATS_CONFIG.chiFlamethrowerSecond / flamesPerSecond;
			if (level == 3 && path == AbilityTreePath.FIRST) {
				required *= 1.5f;
			}
			if (level == 3 && path == AbilityTreePath.SECOND) {
				required *= 2;
			}
			
			boolean infinite = bender.isCreativeMode() && CHI_CONFIG.infiniteInCreative;
			
			if (infinite || chi.consumeChi(required)) {
				
				Vector eye = getEyePos(entity);
				
				World world = ctx.getWorld();
				
				double speedMult = 6 + 5 * totalXp / 100;
				double randomness = 20 - 10 * totalXp / 100;
				boolean lightsFires = false;
				if (level == 3 && path == AbilityTreePath.FIRST) {
					speedMult = 15;
					randomness = 1;
				}
				if (level == 3 && path == AbilityTreePath.SECOND) {
					speedMult = 8;
					randomness = 20;
					lightsFires = true;
				}
				
				double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
				double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
				Vector look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
				
				EntityFlames flames = new EntityFlames(world, entity);
				flames.velocity().set(look.times(speedMult).plus(getVelocityMpS(entity)));
				flames.setPosition(eye.x(), eye.y(), eye.z());
				flames.setLightsFires(lightsFires);
				world.spawnEntity(flames);
				
				world.playSound(null, entity.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE,
						SoundCategory.PLAYERS, 0.2f, 0.8f);
				
			} else {
				// not enough chi
				return true;
			}
		}
		
		return false;
		
	}
	
}
