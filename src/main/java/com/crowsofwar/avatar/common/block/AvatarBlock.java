package com.crowsofwar.avatar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * @author Mahtaran
 */
public class AvatarBlock extends Block {
	public AvatarBlock(Material material, String name) {
		super(material);
		this.setUnlocalizedName(name);
        this.setRegistryName(name);
	}
}