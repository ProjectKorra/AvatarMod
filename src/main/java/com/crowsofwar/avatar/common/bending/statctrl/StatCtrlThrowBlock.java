package com.crowsofwar.avatar.common.bending.statctrl;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.earth.EarthbendingState;
import com.crowsofwar.avatar.common.bending.earth.FloatingBlockEvent;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowBlock extends StatusControl {
	
	public StatCtrlThrowBlock() {
		super(ctx -> {
			
			BendingController<EarthbendingState> controller = (BendingController<EarthbendingState>) BendingManager
					.getBending(BendingType.EARTHBENDING);
			
			EarthbendingState ebs = (EarthbendingState) ctx.getData().getBendingState(controller);
			EntityPlayer player = ctx.getPlayerEntity();
			World world = player.worldObj;
			EntityFloatingBlock floating = ebs.getPickupBlock();
			
			if (floating != null) {
				floating.setOwner(null);
				
				float yaw = (float) Math.toRadians(player.rotationYaw);
				float pitch = (float) Math.toRadians(player.rotationPitch);
				
				// Calculate force and everything
				Vector lookDir = Vector.fromYawPitch(yaw, pitch);
				floating.velocity().add(lookDir.times(20));
				floating.setBehavior(new FloatingBlockBehavior.Thrown(floating));
				ebs.setPickupBlock(null);
				AvatarMod.network.sendTo(new PacketCPlayerData(ctx.getData()), (EntityPlayerMP) player);
				
				controller.post(new FloatingBlockEvent.BlockThrown(floating, player));
				
			}
			
			return true;
			
		}, 2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
}
