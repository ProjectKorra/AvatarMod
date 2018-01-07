package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.AvatarInfo;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarBlocks {

	public static List<Block> allBlocks;
	public static CloudBlock blockCloud;

	private AvatarBlocks() {
	}

	public static void init() {
		allBlocks = new ArrayList<>();
		addBlock(blockCloud = new CloudBlock());
	}

	private static void addBlock(Block block) {
		block.setRegistryName(AvatarInfo.MOD_ID, block.getUnlocalizedName().substring(5));
		allBlocks.add(block);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		Block[] blocksArr = allBlocks.toArray(new Block[allBlocks.size()]);
		e.getRegistry().registerAll(blocksArr);
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

}
