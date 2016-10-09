package com.crowsofwar.avatar.common.statctrl;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.controls.AvatarControl;
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
			Vector rotations = new Vector(Math.toRadians((player.rotationPitch) / 1),
					Math.toRadians(player.rotationYaw), 0);
			
			Vector velocity = rotations.toRectangular();
			velocity.setY(Math.pow(velocity.y(), .1));
			velocity.mul(1);
			player.addVelocity(velocity.x(), velocity.y(), velocity.z());
			((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
		}
		
		return player.onGround;
		
	}
	
}
