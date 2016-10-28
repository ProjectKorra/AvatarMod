package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityThrowBlock extends BendingAbility<EarthbendingState> {
	
	/**
	 * @param controller
	 */
	public AbilityThrowBlock(BendingController<EarthbendingState> controller) {
		super(controller);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
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
	}
	
	@Override
	public int getIconIndex() {
		return 1;
	}
	
	@Override
	public Info getRaytrace() {
		return new Info();
	}
	
}
