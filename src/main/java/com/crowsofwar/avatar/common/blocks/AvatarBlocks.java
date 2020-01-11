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
package com.crowsofwar.avatar.common.blocks;

import com.crowsofwar.avatar.common.blocks.tiles.TileBlockTemp;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author Aang23
 */
public class AvatarBlocks {

//	@GameRegistry.ObjectHolder("modtut:datablock")
	public static BlockTemp BLOCK_TEMP = new BlockTemp();

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new AvatarBlocks());
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().register(BLOCK_TEMP);
		GameRegistry.registerTileEntity(TileBlockTemp.class, "avatarmod:block_temp");
	}

	@SubscribeEvent
	public void registerItemBlocks(RegistryEvent.Register<Item> e) {
		
	}
	
}
