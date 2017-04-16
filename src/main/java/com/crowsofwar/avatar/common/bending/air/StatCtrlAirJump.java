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

package com.crowsofwar.avatar.common.bending.air;

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_AIR_JUMP;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleType;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlAirJump extends StatusControl {
	
	public StatCtrlAirJump() {
		super(0, AvatarControl.CONTROL_SPACE, CrosshairPosition.BELOW_CROSSHAIR);
	}
	
	@Override
	public boolean execute(BendingContext ctx) {
		
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		
		AbilityData abilityData = data.getAbilityData(ABILITY_AIR_JUMP);
		boolean allowDoubleJump = abilityData.getLevel() == 3
				&& abilityData.getPath() == AbilityTreePath.FIRST;
		
		if (entity.onGround || (allowDoubleJump && ctx.consumeChi(STATS_CONFIG.chiAirJump))) {
			
			float xp = 0;
			if (data != null) {
				xp = abilityData.getTotalXp();
				abilityData.addXp(SKILLS_CONFIG.airJump);
			}
			
			Vector rotations = new Vector(Math.toRadians((entity.rotationPitch) / 1),
					Math.toRadians(entity.rotationYaw), 0);
			
			Vector velocity = rotations.toRectangular();
			velocity.setY(Math.pow(velocity.y(), .1));
			velocity.mul(1 + xp / 250.0);
			if (!entity.onGround) {
				velocity.mul(0.6);
				entity.motionX = 0;
				entity.motionY = 0;
				entity.motionZ = 0;
			}
			entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
			AvatarUtils.afterVelocityAdded(entity);
			
			ParticleSpawner spawner = new NetworkParticleSpawner();
			spawner.spawnParticles(entity.worldObj, ParticleType.AIR, 2, 6, new Vector(entity),
					new Vector(1, 0, 1));
			
			// Find approximate maximum distance. In actuality, a bit less, due
			// to max velocity and drag
			// Using kinematic equation, gravity for players is 32 m/s
			float fallAbsorption;
			{
				float h = (5 + xp / 50) / 8f;
				float v = 20 * (1 + xp / 250f);
				fallAbsorption = v * h - 16 * h * h;
			}
			fallAbsorption -= 2; // compensate that it may be a bit extra
			data.setFallAbsorption(fallAbsorption);
			
			data.addTickHandler(TickHandler.AIR_PARTICLE_SPAWNER);
			if (abilityData.getLevel() == 3 && abilityData.getPath() == AbilityTreePath.SECOND) {
				data.setSmashGround(true);
			}
			
			entity.worldObj.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_BAT_TAKEOFF,
					SoundCategory.PLAYERS, 1, .7f);
			
			return true;
			
		}
		
		return false;
		
	}
	
}
