package com.crowsofwar.avatar.common.bending.ability;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.FirebendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityLightFire extends BendingAbility<FirebendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityLightFire(BendingController<FirebendingState> controller) {
		super(controller);
		this.raytrace = new Raytrace.Info(-1, false);
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		
		PlayerState ps = data.getState();
		World world = data.getWorld();
		
		VectorI looking = ps.verifyClientLookAtBlock(-1, 5);
		EnumFacing side = ps.getLookAtSide();
		if (ps.isLookingAtBlock(-1, 5)) {
			VectorI setAt = new VectorI(looking.x(), looking.y(), looking.z());
			setAt.offset(side);
			if (world.getBlockState(setAt.toBlockPos()).getBlock() == Blocks.AIR) {
				world.setBlockState(setAt.toBlockPos(), Blocks.FIRE.getDefaultState());
			}
		}
	}
	
	@Override
	public int getIconIndex() {
		return 2;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
