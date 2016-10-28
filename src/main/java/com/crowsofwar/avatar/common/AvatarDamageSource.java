package com.crowsofwar.avatar.common;

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
	 * @param hit
	 *            Who was hit by floating block
	 * @param owner
	 *            Who threw the floating block
	 * @return DamageSource for the floating block
	 */
	public static DamageSource causeFloatingBlockDamage(Entity hit, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_earthbendBlock", hit, owner);
	}
	
	/**
	 * Create a DamageSource for damage caused by a water arc.
	 * 
	 * @param hit
	 *            Who was hit by the water arc
	 * @param owner
	 *            Who created the water arc
	 * @return DamageSource for the water arc
	 */
	public static DamageSource causeWaterDamage(Entity hit, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_waterArc", hit, owner);
	}
	
	/**
	 * Create a DamageSource for damage caused by a ravine.
	 * 
	 * @param hit
	 *            Who was hit by the ravine
	 * @param owner
	 *            Who created the ravine
	 */
	public static DamageSource causeRavineDamage(Entity hit, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_ravine", hit, owner);
	}
	
	/**
	 * Create a DamageSource for damage caused by a wave.
	 * 
	 * @param hit
	 *            Who was hit by the wave
	 * @param owner
	 *            Who created the wave
	 */
	public static DamageSource causeWaveDamage(Entity hit, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_wave", hit, owner);
	}
	
}
