package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
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
		super(controller, "light_fire");
		this.raytrace = new Raytrace.Info(-1, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		World world = ctx.getWorld();
		
		VectorI looking = ctx.verifyClientLookBlock(-1, 5);
		EnumFacing side = ctx.getLookSide();
		if (ctx.isLookingAtBlock(-1, 5)) {
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
