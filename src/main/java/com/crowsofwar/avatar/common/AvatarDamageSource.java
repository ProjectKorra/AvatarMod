package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.EntityWave;

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
	 * @param owner
	 *            Who threw the floating block
	 * @return DamageSource for the floating block
	 */
	public static DamageSource causeFloatingBlockDamage(EntityFloatingBlock floatingBlock, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_earthbendBlock", floatingBlock, owner);
	}
	
	/**
	 * Create a DamageSource for damage caused by a water arc.
	 * 
	 * @param waterArc
	 *            The water arc entity
	 * @param owner
	 *            Who created the water arc
	 * @return DamageSource for the water arc
	 */
	public static DamageSource causeWaterDamage(EntityWaterArc waterArc, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_waterArc", waterArc, owner);
	}
	
	/**
	 * Create a DamageSource for damage caused by a ravine.
	 * 
	 * @param ravine
	 *            The ravine entity
	 * @param owner
	 *            Who created the ravine
	 */
	public static DamageSource causeRavineDamage(EntityRavine ravine, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_ravine", ravine, owner);
	}
	
	/**
	 * Create a DamageSource for damage caused by a wave.
	 * 
	 * @param wave
	 *            The wave entity
	 * @param owner
	 *            Who created the wave
	 */
	public static DamageSource causeWaveDamage(EntityWave wave, Entity owner) {
		return new EntityDamageSourceIndirect("avatar_wave", wave, owner);
	}
	
}
