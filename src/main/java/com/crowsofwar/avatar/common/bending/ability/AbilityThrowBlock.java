package com.crowsofwar.avatar.common.bending.ability;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.EarthbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
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
	public boolean requiresUpdateTick() {
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		EarthbendingState ebs = (EarthbendingState) data.getBendingState(controller);
		EntityPlayer player = data.getPlayerEntity();
		World world = player.worldObj;
		EntityFloatingBlock floating = ebs.getPickupBlock();
		
		if (floating != null) {
			floating.setOwner(null);
			
			float yaw = (float) Math.toRadians(player.rotationYaw);
			float pitch = (float) Math.toRadians(player.rotationPitch);
			
			// Calculate force and everything
			Vector lookDir = Vector.fromYawPitch(yaw, pitch);
			floating.addVelocity(lookDir.times(20));
			
			floating.drop();
			ebs.setPickupBlock(null);
			AvatarMod.network.sendTo(new PacketCPlayerData(data), (EntityPlayerMP) player);
			
		}
	}
	
	@Override
	public int getId() {
		return 2;
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
