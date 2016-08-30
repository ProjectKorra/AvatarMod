package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
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
		super(controller);
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		System.out.println("I make a ravine!");
		
		EntityPlayer player = data.getPlayerEntity();
		World world = data.getWorld();
		
		Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw), 0);
		
		EntityRavine ravine = new EntityRavine(world);
		ravine.setPosition(player.posX, player.posY, player.posZ);
		ravine.setInitialPosition(Vector.getEntityPos(player));
		ravine.setVelocity(look.times(10));
		world.spawnEntityInWorld(ravine);
		
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
