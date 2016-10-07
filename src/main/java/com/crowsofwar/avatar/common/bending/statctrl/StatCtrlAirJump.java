package com.crowsofwar.avatar.common.bending.statctrl;

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
		super(ctx -> {
			EntityPlayer player = ctx.getPlayerEntity();
			
			Vector rotations = new Vector(Math.toRadians((player.rotationPitch - 15) / 2),
					Math.toRadians(player.rotationYaw), 0);
			
			Vector velocity = rotations.toRectangular();
			velocity.mul(4);
			velocity.setY(velocity.y() * 0.8);
			player.addVelocity(velocity.x(), velocity.y(), velocity.z());
			((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
			
			return true;
			
		}, 0, AvatarControl.CONTROL_SPACE, CrosshairPosition.BELOW_CROSSHAIR);
	}
	
}
