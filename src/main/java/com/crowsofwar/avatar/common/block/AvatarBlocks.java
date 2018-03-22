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
import net.minecraftforge.fml.common.registry.GameRegistry;
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
	@GameRegistry.ObjectHolder("avatarmod:cloud_block")
	public static CloudBlock cloudBlock;

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		cloudBlock.initModel();
	}

	@SideOnly(Side.CLIENT)
	public static void initItemModels() {
		//cloudBlock.initItemModel();
	}
	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new AvatarBlocks());
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new CloudBlock());
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(cloudBlock).setRegistryName(cloudBlock.getRegistryName()));
	}
}
