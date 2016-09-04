package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFlames;
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
public class AbilityFlamethrower extends BendingAbility<FirebendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityFlamethrower(BendingController<FirebendingState> controller) {
		super(controller);
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return true;
	}
	
	@Override
	public void update(AvatarPlayerData data) {
		FirebendingState fs = data.getBendingState(controller);
		EntityPlayer player = data.getPlayerEntity();
		if (fs.isFlamethrowing() && player.ticksExisted % 2 == 0) {
			
			Vector look = Vector.fromEntityLook(player);
			Vector eye = Vector.getEyePos(player);
			
			World world = data.getWorld();
			
			EntityFlames flames = new EntityFlames(world);
			flames.setVelocity(look.times(4));
			flames.setPosition(eye.x(), eye.y(), eye.z());
			world.spawnEntityInWorld(flames);
			
			fs.setFlamethrowing(fs.getTicksLeftFlamethrowing() - 1);
			
		}
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		System.out.println("FWOOOSH!");
		data.getBendingState(controller).setFlamethrowing(30);
	}
	
	@Override
	public int getIconIndex() {
		return 9;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
