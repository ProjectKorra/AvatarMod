package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

/**
 * Contains static methods used to acquire custom DamageSources for various uses.
 * 
 * @author CrowsOfWar
 */
public class AvatarDamageSource {
	
	/**
	 * Create a DamageSource for damage caused by a floating block.
	 * 
	 * @param floatingBlock
	 *            The floating block
	 * @param wasHit
	 *            The entity who was hit by the floating block
	 * @return DamageSource for the floating block
	 */
	public static DamageSource causeFloatingBlockDamage(EntityFloatingBlock floatingBlock, Entity wasHit) {
		return new EntityDamageSourceIndirect("avatar_earthbendBlock", floatingBlock, wasHit);
	}
	
	/**
	 * Create a DamageSource for damage caused by a water arc.
	 * 
	 * @param waterArc
	 *            The water arc entity
	 * @param wasHit
	 *            The entity who was hit by the water arc
	 * @return DamageSource for the water arc
	 */
	public static DamageSource causeWaterDamage(EntityWaterArc waterArc, Entity wasHit) {
		return new EntityDamageSourceIndirect("avatar_waterArc", waterArc, wasHit);
	}
	
}
