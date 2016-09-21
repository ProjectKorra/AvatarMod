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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
		
		System.out.println("Execute...");
		
		Vector look = Vector.fromEntityLook(player);
		look.setY(0);
		Raytrace.Result result = Raytrace.predicateRaytrace(world, Vector.getEntityPos(player).add(0, -1, 0),
				look, 4, (pos, blockState) -> blockState.getBlock() == Blocks.WATER);
		if (result.hitSomething()) {
			
			VectorI hitPos = result.getPos();
			IBlockState hitBlockState = world.getBlockState(hitPos.toBlockPos());
			System.out.println("Wave hit at " + hitPos);
			
			EntityWave wave = new EntityWave(world);
			wave.velocity().set(look.times(0));
			System.out.println("set velocity to " + look);
			wave.setPosition(hitPos.x() + 0.5, hitPos.y(), hitPos.z() + 0.5);
			
			wave.rotationYaw = (float) look.toSpherical().toDegrees().y();
			
			world.spawnEntityInWorld(wave);
			
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
