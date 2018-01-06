package com.crowsofwar.avatar.common.blocks;


import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

public class CloudBlock extends Block {
	public CloudBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterial.AIR);
		this.setCreativeTab(AvatarItems.tabItems);
		this.setUnlocalizedName("cloud_block");
		this.setHardness(1f);
		this.setLightLevel(0.1F);
		this.setResistance(10F);
		this.setSoundType(SoundType.CLOTH);


	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return true;
	}
}
