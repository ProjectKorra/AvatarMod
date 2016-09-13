package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWave;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class AbilityCreateWave extends BendingAbility<WaterbendingState> {
    
	private final Raytrace.Info raytrace;
	
	public AbilityCreateWave(BendingController<WaterbendingState> controller) {
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
		
		Vector look = Vector.fromEntityLook(player);
		Raytrace.Result result = Raytrace.raytrace(world, Vector.getEntityPos(player).add(0, -1, 0), look, 4, false);
		if (result.hitSomething()) {
			VectorI hitPos = result.getPos();
			IBlockState hitBlockState = world.getBlockState(hitPos.toBlockPos());
		}
		
	}
	
	@Override
	public int getIconIndex() {
		return 10;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
