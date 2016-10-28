package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.gorecore.util.Vector;

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
		super(2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(AbilityContext context) {
		
		BendingController<EarthbendingState> controller = (BendingController<EarthbendingState>) BendingManager
				.getBending(BendingType.EARTHBENDING);
		
		EarthbendingState ebs = (EarthbendingState) context.getData().getBendingState(controller);
		EntityPlayer player = context.getPlayerEntity();
		World world = player.worldObj;
		EntityFloatingBlock floating = ebs.getPickupBlock();
		
		if (floating != null) {
			
			float yaw = (float) Math.toRadians(player.rotationYaw);
			float pitch = (float) Math.toRadians(player.rotationPitch);
			
			// Calculate force and everything
			Vector lookDir = Vector.fromYawPitch(yaw, pitch);
			floating.velocity().add(lookDir.times(20));
			floating.setBehavior(new FloatingBlockBehavior.Thrown(floating));
			ebs.setPickupBlock(null);
			AvatarMod.network.sendTo(new PacketCPlayerData(context.getData()), (EntityPlayerMP) player);
			
			controller.post(new FloatingBlockEvent.BlockThrown(floating, player));
			
			context.removeStatusControl(PLACE_BLOCK);
			
		}
		
		return true;
		
	}
	
}
