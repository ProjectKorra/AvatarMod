package com.crowsofwar.avatar.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AvatarAttackEvent extends LivingAttackEvent {
	public AvatarAttackEvent(EntityLivingBase entity, DamageSource source, float amount) {
		super(entity, source, amount);
	}
}
