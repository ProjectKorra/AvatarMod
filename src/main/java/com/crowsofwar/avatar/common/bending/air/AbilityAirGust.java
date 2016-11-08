package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
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
public class AbilityAirGust extends BendingAbility {
	
	public static BendingAbility INSTANCE;
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityAirGust(BendingController controller) {
		super(controller, "air_gust");
		this.raytrace = new Raytrace.Info();
		INSTANCE = this;
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		
		Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
				Math.toRadians(player.rotationPitch));
		Vector pos = Vector.getEyePos(player);
		
		EntityAirGust gust = new EntityAirGust(world);
		gust.velocity().set(look.times(25));
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
