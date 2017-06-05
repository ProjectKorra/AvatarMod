/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import java.util.Iterator;

import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.data.ScheduledDestroyBlock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EarthbendingEvents {
	
	private EarthbendingEvents() {}
	
	@SubscribeEvent
	public void digSpeed(PlayerEvent.BreakSpeed e) {
		EntityPlayer player = e.getEntityPlayer();
		World world = player.worldObj;
		
		IBlockState state = e.getState();
		if (STATS_CONFIG.bendableBlocks.contains(state.getBlock())) {
			e.setNewSpeed(e.getOriginalSpeed() * 2);
		}
		
	}
	
	@SubscribeEvent
	public void worldUpdate(WorldTickEvent e) {
		World world = e.world;
		if (!world.isRemote && e.phase == TickEvent.Phase.START && world.provider.getDimension() == 0) {
			
			AvatarWorldData wd = AvatarWorldData.getDataFromWorld(world);
			Iterator<ScheduledDestroyBlock> iterator = wd.getScheduledDestroyBlocks().iterator();
			while (iterator.hasNext()) {
				
				ScheduledDestroyBlock sdb = iterator.next();
				sdb.decrementTicks();
				if (sdb.getTicks() <= 0) {
					
					world.destroyBlock(sdb.getPos(), sdb.isDrop());
					iterator.remove();
					
				}
				
			}
			
		}
	}
	
	private void destroyBlock(World world, BlockPos pos, boolean dropBlock, int fortune) {
		
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		
		if (!block.isAir(iblockstate, world, pos)) {
			world.playEvent(2001, pos, Block.getStateId(iblockstate));
			
			if (dropBlock) {
				block.dropBlockAsItem(world, pos, iblockstate, fortune);
			}
			
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
		
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new EarthbendingEvents());
	}
	
}
