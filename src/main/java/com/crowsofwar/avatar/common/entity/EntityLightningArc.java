package com.crowsofwar.avatar.common.entity;

import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class EntityLightningArc extends EntityArc {

	public EntityLightningArc(World world) {
		super(world);
	}

	@Override
	public int getAmountOfControlPoints() {
		return 2;
	}

}
