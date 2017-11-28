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

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getVelocity;
import static java.lang.Math.toRadians;

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
		
		AbilityData abilityData = data.getAbilityData("flamethrower");
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

			double powerRating = bender.calcPowerRating(Firebending.ID);

			float requiredChi = STATS_CONFIG.chiFlamethrowerSecond / flamesPerSecond;
			if (level == 3 && path == AbilityTreePath.FIRST) {
				requiredChi *= 1.5f;
			}
			if (level == 3 && path == AbilityTreePath.SECOND) {
				requiredChi *= 2;
			}

			// Adjust chi to power rating
			// Multiply chi by a number (from 0..2) based on the power rating - powerFactor
			//  Numbers 0..1 would reduce the chi, while numbers 1..2 would increase the chi
			// maxPowerFactor: maximum amount that the chi can be multiplied by
			// e.g. 0.1 -> chi can be changed by 10%; powerFactor in between 0.9..1.1
			double maxPowerFactor = 0.4;
			double powerFactor = (powerRating + 100) / 100 * maxPowerFactor + 1 - maxPowerFactor;
			requiredChi *= powerFactor;
			
			if (bender.consumeChi(requiredChi)) {
				
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

				// Affect stats by power rating
				speedMult += powerRating / 100f * 2.5f;
				randomness -= powerRating / 100f * 6f;

				double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
				double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
				Vector look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
				
				EntityFlames flames = new EntityFlames(world, entity);
				flames.setVelocity(look.times(speedMult).plus(getVelocity(entity)));
				flames.setPosition(eye.x(), eye.y(), eye.z());
				flames.setLightsFires(lightsFires);
				flames.setDamageMult(bender.getDamageMult(Firebending.ID));
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
