package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityRaytraceHandler extends EntityOffensive {

	//This class handles most laser-like abilities, such as air burst's second mechanic.
	public EntityRaytraceHandler(World world) {
		super(world);
	}

	private ResourceLocation particleType;
	private double range;
	private DamageSource element;

	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeBeamDamage(target, getOwner(), element);
	}
}
