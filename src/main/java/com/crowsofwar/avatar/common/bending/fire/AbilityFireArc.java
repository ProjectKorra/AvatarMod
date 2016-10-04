package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
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
		return true;
	}
	
	@Override
	public void update(AvatarPlayerData data) {
		PlayerState state = data.getState();
		EntityPlayer player = data.getPlayerEntity();
		World world = player.worldObj;
		FirebendingState fs = data.getBendingState(controller);
		if (fs.isManipulatingFire()) {
			EntityFireArc fire = fs.getFireArc();
			if (fire != null) {
				Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
						Math.toRadians(player.rotationPitch));
				Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
				Vector motion = lookPos.minus(new Vector(fire));
				motion.normalize();
				motion.mul(.15);
				fire.moveEntity(motion.x(), motion.y(), motion.z());
				fire.setOwner(player);
			} else {
				if (!world.isRemote) fs.setNoFireArc();
			}
		}
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		FirebendingState fs = ctx.getData().getBendingState(controller);
		
		Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
				Math.toRadians(player.rotationPitch));
		Vector lookPos = new Vector(player).plus(look.times(3));
		EntityFireArc fire = new EntityFireArc(world);
		fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
		
		world.spawnEntityInWorld(fire);
		
		fs.setFireArc(fire);
		ctx.getData().sendBendingState(fs);
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
