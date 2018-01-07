package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class CloudBlock extends Block {
	public CloudBlock() {
		super(Material.AIR);
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
}
