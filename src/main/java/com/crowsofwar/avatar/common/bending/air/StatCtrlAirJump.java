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

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleType;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityVelocity;
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
	public boolean execute(AbilityContext context) {
		
		EntityPlayer player = context.getPlayerEntity();
		
		if (player.onGround) {
			
			float xp = 0;
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			if (data != null) {
				AbilityData abilityData = data.getAbilityData(BendingAbility.ABILITY_AIR_JUMP);
				xp = abilityData.getXp();
				abilityData.addXp(SKILLS_CONFIG.airJump);
			}
			
			Vector rotations = new Vector(Math.toRadians((player.rotationPitch) / 1),
					Math.toRadians(player.rotationYaw), 0);
			
			Vector velocity = rotations.toRectangular();
			velocity.setY(Math.pow(velocity.y(), .1));
			velocity.mul(1 + xp / 250.0);
			player.addVelocity(velocity.x(), velocity.y(), velocity.z());
			((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
			
			ParticleSpawner spawner = new NetworkParticleSpawner();
			spawner.spawnParticles(player.worldObj, ParticleType.AIR, 2, 6, new Vector(player),
					new Vector(1, 0, 1));
			
			AirJumpParticleSpawner.spawnParticles(player);
			
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
			
			player.worldObj.playSound(null, new BlockPos(player), SoundEvents.ENTITY_BAT_TAKEOFF,
					SoundCategory.PLAYERS, 1, .7f);
			
		}
		
		return player.onGround;
		
	}
	
}
