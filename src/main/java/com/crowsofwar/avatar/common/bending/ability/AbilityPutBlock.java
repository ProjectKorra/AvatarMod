package com.crowsofwar.avatar.common.bending.ability;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.EarthbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock.OnBlockLand;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.util.EnumFacing;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityPutBlock extends BendingAbility<EarthbendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityPutBlock(BendingController<EarthbendingState> controller) {
		super(controller);
		
		this.raytrace = new Raytrace.Info(-1, true);
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		
		EarthbendingState ebs = data.getBendingState(controller);
		PlayerState state = data.getState();
		
		EntityFloatingBlock floating = ebs.getPickupBlock();
		if (floating != null) {
			// TODO Verify look at block
			VectorI looking = state.getClientLookAtBlock();
			EnumFacing lookingSide = state.getLookAtSide();
			looking.offset(lookingSide);
			if (looking != null && lookingSide != null) {
				// if (world.getBlock(x, y, z) == Blocks.air) {
				// world.setBlock(x, y, z, floating.getBlock());
				// floating.setDead();
				// }
				floating.setOnLandBehavior(OnBlockLand.DO_NOTHING);
				floating.setMovingToBlock(looking.toBlockPos());
				floating.setGravityEnabled(false);
				Vector force = looking.precision().minus(new Vector(floating));
				force.normalize();
				floating.addVelocity(force);
				ebs.dropBlock();
				
			}
		}
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
