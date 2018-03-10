package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CloudBlock extends Block {
	public CloudBlock() {
		super(Material.GLASS);
		this.setCreativeTab(AvatarItems.tabItems);
		this.setUnlocalizedName("cloud_block");
		this.setHardness(1f);
		this.setLightLevel(0.1F);
		this.setResistance(10F);
		this.setSoundType(SoundType.CLOTH);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "json"));
	}

}
