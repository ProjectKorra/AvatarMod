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
package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.data.TemporaryWaterLocation;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Iterator;
import java.util.List;

/**
 * A hacky workaround to manage a temporary water block at a position. The water
 * block will flow outwards. Creating a flowing water block (which would
 * naturally disappear) still wouldn't work since it wouldn't flow outwards.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class TemporaryWaterHandler {

	@SubscribeEvent
	public static void onTick(WorldTickEvent e) {
		if (e.phase == Phase.START && e.side == Side.SERVER) {

			World world = e.world;
			AvatarWorldData wd = AvatarWorldData.getDataFromWorld(world);

			List<TemporaryWaterLocation> twls = wd.geTemporaryWaterLocations();
			Iterator<TemporaryWaterLocation> iterator = twls.iterator();

			while (iterator.hasNext()) {

				TemporaryWaterLocation twl = iterator.next();

				if (twl.getDimension() == world.provider.getDimension()) {

					twl.decrementTicks();
					if (twl.getTicks() <= 0) {
						BlockPos pos = twl.getPos();
						Block block = world.getBlockState(pos).getBlock();
						if (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
							// world.setBlockToAir(pos);
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}

						iterator.remove();
					}

				}

			}

		}
	}

}
