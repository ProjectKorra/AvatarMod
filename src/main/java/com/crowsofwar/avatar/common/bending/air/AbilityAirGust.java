package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityAirGust extends BendingAbility<AirbendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityAirGust(BendingController<AirbendingState> controller) {
		super(controller);
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		
		EntityPlayer player = data.getPlayerEntity();
		World world = data.getWorld();
		
		Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
				Math.toRadians(player.rotationPitch));
		Vector pos = Vector.getEyePos(player);
		
		EntityAirGust gust = new EntityAirGust(world);
		gust.setVelocity(look.times(10));
		gust.setPosition(pos.x(), pos.y(), pos.z());
		gust.setOwner(player);
		
		world.spawnEntityInWorld(gust);
	}
	
	@Override
	public int getIconIndex() {
		return 7;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
