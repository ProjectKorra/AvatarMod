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

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Contains static methods used to acquire custom DamageSources for various
 * uses.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarDamageSource {

	public static final DamageSource WATER = new DamageSource("avatar_Water");
	public static final DamageSource FIRE = new DamageSource("avatar_Fire");
	public static final DamageSource EARTH = new DamageSource("avatar_Earth");
	public static final DamageSource AIR = new DamageSource("avatar_Air");
	public static final DamageSource LIGHTNING = new DamageSource("avatar_Lightning");
	public static final DamageSource COMBUSTION = new DamageSource("avatar_Combustion");
	public static final DamageSource SAND = new DamageSource("avatar_Sand");
	public static final DamageSource ICE = new DamageSource("avatar_Ice");

	/**
	 * Returns whether the given damage was inflicted using an Avatar damage source.
	 */
	public static boolean isAvatarDamageSource(DamageSource source) {
		return source.getDamageType().startsWith("avatar_");
	}

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
	 * Create a DamageSource for damage caused by an air shockwave, or other air abilities that cause non-slicing damage.
	 *
	 * @param hit   Who was hit by the compressed air
	 * @param owner Who released the compressed air
	 * @return DamageSource for the compressed air
	 */

	public static DamageSource causeAirDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_AirDamage", hit, owner);
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
		return new EntityDamageSourceIndirect("avatar_fireArc", hit, owner).setProjectile();
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
		return new EntityDamageSourceIndirect("avatar_wave", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a fireball.
	 *
	 * @param hit   Who was hit by the fireball
	 * @param owner Who created the fireball
	 */
	public static DamageSource causeFireballDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_fireball", hit, owner).setProjectile()
				.setExplosion();
	}

	/**
	 * Create a DamageSource for damage caused by an earthspike.
	 *
	 * @param hit   Who was hit by the earthspike
	 * @param owner Who created the earthspike
	 */
	public static DamageSource causeEarthspikeDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_earthspike", hit, owner);
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
		return new EntityDamageSourceIndirect("avatar_flamethrower", hit, owner).setProjectile();
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
		return new EntityDamageSourceIndirect("avatar_waterCannon", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by an ice prison.
	 *
	 * @param hit   Who was hurt by the ice prison
	 * @param owner Who created the ice prison
	 */
	public static DamageSource causeIcePrisonDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_icePrison", hit, owner).setDamageBypassesArmor();
	}

	/**
	 * Create a DamageSource for damage caused by an ice shard.
	 *
	 * @param hit   Who was hit by the ice shard
	 * @param owner Who created the ice shard
	 */
	public static DamageSource causeIceShardDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_iceShard", hit, owner).setDamageBypassesArmor().setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a sand prison.
	 *
	 * @param hit   Who was hurt by the sand prison
	 * @param owner Who created the sand prison
	 */
	public static DamageSource causeSandPrisonDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_sandPrison", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by a sandstorm.
	 *
	 * @param hit   Who was hit by the sandstorm
	 * @param owner Who created the sandstorm
	 */
	public static DamageSource causeSandstormDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_sandstorm", hit, owner);
	}

	public static DamageSource causeShockwaveDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_shockWave", hit, owner).setExplosion();
	}

	@SubscribeEvent
	public static void onElementalDamage(LivingHurtEvent event) {
		//TODO: Config for all this stuff; definitely in the rewrite
		DamageSource source = event.getSource();
		Entity hit = event.getEntity();
		if (hit instanceof EntityLivingBase) {
			if (hit instanceof EntityDragon && AvatarDamageSource.isAvatarDamageSource(source)) {
				event.setCanceled(false);
				event.setAmount(event.getAmount());
			}

			if (source == AvatarDamageSource.WATER) {
				hit.setFire(0);
				if (hit instanceof EntityEnderman || hit instanceof EntityEndermite) {
					event.setAmount(event.getAmount() * 1.25F);
				}
				if (hit instanceof EntityGuardian || hit instanceof EntityWaterMob) {
					event.setAmount(event.getAmount() * 0.75F);
				}
			}

			if (source == AvatarDamageSource.FIRE) {
				if (hit.isImmuneToFire()) {
					event.setAmount(event.getAmount() * 0.25F);
				}
				if (((EntityLivingBase) hit).isPotionActive(MobEffects.FIRE_RESISTANCE)) {
					event.setAmount(event.getAmount() * (0.75F / (Objects.requireNonNull(((EntityLivingBase) hit).getActivePotionEffect(MobEffects.FIRE_RESISTANCE)).getAmplifier() + 1)));
				}
			}

			if (source == AvatarDamageSource.AIR) {
				if (hit instanceof EntityFlying) {
					event.setAmount(event.getAmount() * 1.25F);
				}
			}

			if (source == AvatarDamageSource.LIGHTNING) {
				if (hit instanceof EntityWaterMob) {
					event.setAmount(event.getAmount() * 1.5F);
				}
				if (hit instanceof EntityCreeper) {
					AvatarUtils.chargeCreeper((EntityCreeper) hit);
				}

			}

			if (source == AvatarDamageSource.COMBUSTION) {
				if (hit instanceof EntityCreeper) {
					event.setAmount(event.getAmount() * 0.5F);
					AvatarUtils.igniteCreeper((EntityCreeper) hit);
				}
			}

			if (source == AvatarDamageSource.SAND) {
				if (hit instanceof EntityHusk) {
					event.setAmount(event.getAmount() * 0.75F);
				}
			}

			if (source == AvatarDamageSource.ICE) {
				if (hit instanceof EntityStray) {
					event.setAmount(event.getAmount() * 0.75F);
				}
			}
		}
	}


}
