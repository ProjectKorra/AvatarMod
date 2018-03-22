package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.AvatarInfo;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Mahtaran
 */
public class AvatarBlock extends Block {
	public AvatarBlock(Material material, String name) {
		super(material);
		this.setUnlocalizedName(AvatarInfo.MOD_ID + ":" + name);
		this.setRegistryName(name);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
