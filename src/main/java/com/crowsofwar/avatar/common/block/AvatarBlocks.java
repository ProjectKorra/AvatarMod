package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarMod;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CrowsOfWar
 * @author Mahtaran
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarBlocks {

	public static List<Block> allBlocks;
	public static CloudBlock blockCloud;

	public static List<ItemBlock> allItemBlocks;
	public static ItemBlock itemBlockCloud;

	private AvatarBlocks() {
	}

	public static void init() {
		allBlocks = new ArrayList<>();
		allItemBlocks = new ArrayList<>();
		addBlock(blockCloud = new CloudBlock(), itemBlockCloud = new ItemBlock(blockCloud));
		
		MinecraftForge.EVENT_BUS.register(new AvatarBlocks());
	}

	private static void addBlock(Block block, ItemBlock itemBlock) {
		allBlocks.add(block);
		ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(),
					"Block %s has null registry name", block);
		itemBlock.setRegistryName(registryName);
		allItemBlocks.add(itemBlock);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		init();
		Block[] blocksArr = allBlocks.toArray(new Block[allBlocks.size()]);
		e.getRegistry().registerAll(blocksArr);
		
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> e) {
		for (ItemBlock itemBlock : allItemBlocks) {
			e.getRegistry().register(itemBlock);
		}
		AvatarMod.proxy.registerBlockModels();
	}
}