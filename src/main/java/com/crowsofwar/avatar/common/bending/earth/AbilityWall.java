package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
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
		EnumFacing cardinal = player.getHorizontalFacing();
		
		BlockPos lookPos = ctx.getClientLookBlock().toBlockPos();
		if (STATS_CONFIG.bendableBlocks.contains(world.getBlockState(lookPos).getBlock())) {
			System.out.println("BEND A WALL");
			EntityWall wall = new EntityWall(world);
			// Minecraft
			wall.setPosition(lookPos.getX() + .5, lookPos.getY(), lookPos.getZ() + .5);
			for (int i = 0; i < 5; i++) {
				
				int horizMod = -2 + i;
				int x = lookPos.getX()
						+ (cardinal == EnumFacing.NORTH || cardinal == EnumFacing.SOUTH ? horizMod : 0);
				int y = lookPos.getY() - 4;
				int z = lookPos.getZ()
						+ (cardinal == EnumFacing.EAST || cardinal == EnumFacing.WEST ? horizMod : 0);
				
				EntityWallSegment seg = new EntityWallSegment(world);
				seg.attachToWall(wall);
				seg.setPosition(x + .5, y, z + .5);
				seg.setDirection(cardinal);
				
				for (int j = 0; j < EntityWallSegment.SEGMENT_HEIGHT; j++) {
					BlockPos pos = new BlockPos(x, y + j, z);
					seg.setBlock(j, world.getBlockState(pos));
					world.setBlockToAir(pos);
				}
				
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
