package com.crowsofwar.avatar.common.bending.ability;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.FirebendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
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
public class AbilityFireArc extends BendingAbility<FirebendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityFireArc(BendingController<FirebendingState> controller) {
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
		FirebendingState fs = data.getBendingState(controller);
		
		Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
				Math.toRadians(player.rotationPitch));
		Vector lookPos = new Vector(player).plus(look.times(3));
		EntityFireArc fire = new EntityFireArc(world);
		fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
		
		world.spawnEntityInWorld(fire);
		
		fs.setFireArc(fire);
		data.sendBendingState(fs);
	}
	
	@Override
	public int getIconIndex() {
		return 3;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
