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

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.blocks.tiles.TileBlockTemp;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aang23
 */
public class AvatarBlocks {


	public static BlockTemp BLOCK_TEMP = new BlockTemp();

	public static List<Block> allBlocks;
	public static CloudBlock blockCloud;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new AvatarBlocks());
				allBlocks = new ArrayList<>();
		addBlock(blockCloud = new CloudBlock());

		MinecraftForge.EVENT_BUS.register(new AvatarBlocks());
	}

	private static void addBlock(Block block) {
		// Remove the "tile." prefix
		block.setRegistryName("avatarmod", block.getTranslationKey().substring(5));
		block.setTranslationKey("avatarmod:" + block.getTranslationKey().substring(5));
		allBlocks.add(block);
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> e) {
		for (Block block : allBlocks) {
			ItemBlock itemBlock = new ItemBlock(block);
			ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(),
					"Block %s has null registry name", block);
			itemBlock.setRegistryName(registryName);
			e.getRegistry().register(itemBlock);

		}
	}


	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> e) {
		init();
		Block[] blocksArr = allBlocks.toArray(new Block[allBlocks.size()]);
		e.getRegistry().registerAll(blocksArr);
		AvatarMod.proxy.registerItemModels();
		GameRegistry.registerTileEntity(TileBlockTemp.class, "avatarmod:block_temp");

	}



}
