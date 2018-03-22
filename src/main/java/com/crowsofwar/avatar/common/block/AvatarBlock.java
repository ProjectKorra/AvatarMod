package com.crowsofwar.avatar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.crowsofwar.avatar.AvatarInfo;

/**
 * @author Mahtaran
 */
public class AvatarBlock extends Block {
	public AvatarBlock(Material material, String name) {
		super(material);
		this.setUnlocalizedName(AvatarInfo.MOD_ID + ":" + name);
		this.setRegistryName(name);
	}
}
