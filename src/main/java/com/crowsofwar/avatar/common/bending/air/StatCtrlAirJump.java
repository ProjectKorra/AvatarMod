package com.crowsofwar.avatar.common.bending.air;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.particle.AvatarParticleType;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

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
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(player);
			if (data != null) {
				AbilityData abilityData = data.getAbilityData(AbilityAirJump.INSTANCE);
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
			spawner.spawnParticles(player.worldObj, AvatarParticleType.AIR, 2, 6, new Vector(player),
					new Vector(1, 0, 1));
			
			AirJumpParticleSpawner.spawnParticles(player);
			
		}
		
		return player.onGround;
		
	}
	
}
