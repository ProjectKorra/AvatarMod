/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

import javax.annotation.Nullable;

/**
 * Contains static methods used to acquire custom DamageSources for various
 * uses.
 *
 * @author CrowsOfWar
 */
public class AvatarDamageSource {

	/**
	 * Create a DamageSource for damage caused by a floating block.
	 *
	 * @param hit   Who was hit by floating block
	 * @param owner Who threw the floating block
	 * @return DamageSource for the floating block
	 */
	public static DamageSource causeFloatingBlockDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_earthbendBlock", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a water arc.
	 *
	 * @param hit   Who was hit by the water arc
	 * @param owner Who created the water arc
	 * @return DamageSource for the water arc
	 */
	public static DamageSource causeWaterDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_waterArc", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a fire arc.
	 *
	 * @param hit   Who was hit by the fire arc
	 * @param owner Who created the fire arc
	 * @return DamageSource for the fire arc
	 */
	public static DamageSource causeFireDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_fireArc", hit, owner).setProjectile()
				.setFireDamage();
	}

	/**
	 * Create a DamageSource for damage caused by a ravine.
	 *
	 * @param hit   Who was hit by the ravine
	 * @param owner Who created the ravine
	 */
	public static DamageSource causeRavineDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_ravine", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by a wave.
	 *
	 * @param hit   Who was hit by the wave
	 * @param owner Who created the wave
	 */
	public static DamageSource causeWaveDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_wave", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by a fireball.
	 *
	 * @param hit   Who was hit by the fireball
	 * @param owner Who created the fireball
	 */
	public static DamageSource causeFireballDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_fireball", hit, owner).setProjectile()
				.setFireDamage().setExplosion();
	}

	/**
	 * Create a DamageSource for damage caused by an airblade.
	 *
	 * @param hit   Who was hit by the airblade
	 * @param owner Who created the airblade
	 */
	public static DamageSource causeAirbladeDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_airblade", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by flamethrower.
	 *
	 * @param hit   Who was hit by the flames
	 * @param owner Who created the flames
	 */
	public static DamageSource causeFlamethrowerDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_flamethrower", hit, owner).setProjectile()
				.setFireDamage();
	}

	/**
	 * Create a DamageSource for damage caused by smashing the ground.
	 *
	 * @param hit   Who was hit by the smash
	 * @param owner Who smashed the ground
	 */
	public static DamageSource causeSmashDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_groundSmash", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by lightning bending.
	 *
	 * @param hit   Who was hit by lightning
	 * @param owner The lightning bender
	 */
	public static DamageSource causeLightningDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_lightningBending", hit, owner).setDamageBypassesArmor();
	}

	/**
	 * Create a DamageSource for damage caused by redirected lightning bending.
	 *
	 * @param hit        Who was hit by lightning
	 * @param controller Who redirected the lightning
	 */
	public static DamageSource causeRedirectedLightningDamage(Entity hit, @Nullable Entity
			controller) {
		return new EntityDamageSourceIndirect("avatar_lightningBendingRedirected", hit, controller)
				.setDamageBypassesArmor();
	}

	/**
	 * Create a DamageSource for damage caused by a water cannon.
	 *
	 * @param hit   Who was hit by the water cannon
	 * @param owner Who created the water cannon
	 */
	public static DamageSource causeWaterCannonDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_watercannon", hit, owner);
	}

}
