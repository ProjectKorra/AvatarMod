package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbilityWall extends BendingAbility {
	
	public AbilityWall() {
		super(BendingType.EARTHBENDING, "earth_wall");
		requireRaytrace(6, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		
		BlockPos lookPos = ctx.getClientLookBlock().toBlockPos();
		if (STATS_CONFIG.bendableBlocks.contains(world.getBlockState(lookPos).getBlock())) {
			System.out.println("BEND A WALL");
			EntityWall wall = new EntityWall(world);
			wall.setPosition(lookPos.getX(), lookPos.getY(), lookPos.getZ());
			for (int i = 0; i < 5; i++) {
				EntityWallSegment seg = new EntityWallSegment(world);
				seg.attachToWall(wall);
				seg.setPosition(lookPos.getX() - 2 + i, lookPos.getY(), lookPos.getZ());
				world.spawnEntityInWorld(seg);
			}
			world.spawnEntityInWorld(wall);
		}
		
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
}
