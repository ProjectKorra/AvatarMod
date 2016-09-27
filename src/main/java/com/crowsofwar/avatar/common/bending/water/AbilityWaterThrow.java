package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityWaterThrow extends BendingAbility<WaterbendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityWaterThrow(BendingController<WaterbendingState> controller) {
		super(controller);
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public boolean requiresUpdateTick() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		
		WaterbendingState bendingState = data.getBendingState(controller);
		EntityPlayer player = data.getPlayerEntity();
		
		if (bendingState.isBendingWater()) {
			
			EntityWaterArc water = bendingState.getWaterArc();
			
			Vector force = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
					Math.toRadians(player.rotationPitch));
			force.mul(10);
			water.velocity().add(force);
			water.setGravityEnabled(true);
			
			bendingState.releaseWater();
			data.sendBendingState(bendingState);
			
		}
	}
	
	@Override
	public int getIconIndex() {
		return -1;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
