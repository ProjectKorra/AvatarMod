package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.EntityRavine;
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
public class AbilityRavine extends BendingAbility<EarthbendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityRavine(BendingController<EarthbendingState> controller) {
		super(controller, "ravine");
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		float xp = ctx.getData().getAbilityData(this).getXp();
		
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		
		Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw), 0);
		
		EntityRavine ravine = new EntityRavine(world);
		ravine.setOwner(player);
		ravine.setPosition(player.posX, player.posY, player.posZ);
		ravine.velocity().set(look.times(10));
		ravine.setDamageMult(.75f + xp / 100);
		world.spawnEntityInWorld(ravine);
		
		BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.Created(ravine, player));
		
	}
	
	@Override
	public int getIconIndex() {
		return 8;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
