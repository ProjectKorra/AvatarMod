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

package com.crowsofwar.avatar.common.damageutils;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
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

	public static String getNameFromBendingStyle(String elementName) {
		String damageName = "";
		if (elementName.equalsIgnoreCase("firebending"))
			damageName = "Fire";
		else if (elementName.equalsIgnoreCase("waterbending"))
			damageName = "Water";
		else if (elementName.equalsIgnoreCase("earthbending"))
			damageName = "Earth";
		else if (elementName.equalsIgnoreCase("airbending"))
			damageName = "Air";
		else if (elementName.equalsIgnoreCase("lightningbending"))
			damageName = "Lightning";
		else if (elementName.equalsIgnoreCase("sandbending"))
			damageName = "Sand";
		else if (elementName.equalsIgnoreCase("icebending"))
			damageName = "Ice";
		else if (elementName.equalsIgnoreCase("combustionbending"))
			damageName = "Combustion";
		return damageName;
	}

	public static boolean isAirDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Air");
	}

	public static boolean isEarthDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Earth");
	}

	public static boolean isWaterDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Water");
	}

	public static boolean isFireDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Fire");
	}

	public static boolean isLightningDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Lightning");
	}

	public static boolean isCombustionDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Combustion");
	}

	public static boolean isIceDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Ice");
	}

	public static boolean isSandDamage(DamageSource source) {
		return source.getDamageType().startsWith("avatar_Sand");
	}


	//For some reason, using these methods doesn't hurt the ender dragon, even though it's technically the correct usage.
	//Weird.
	/*public static DamageSource causeIndirectBendingDamage(Entity owner,  @Nullable Entity abilityEntity, DamageSource source) {
		return new EntityDamageSourceIndirect(source.toString(), owner, abilityEntity);
	}

	public static DamageSource causeDirectBendingDamage(Entity owner, DamageSource source) {
		return new EntityDamageSource(source.toString(), owner);
	}
	**/

	//AIR

	/**
	 * Create a DamageSource for damage caused by an air shockwave, or other air abilities that cause non-slicing damage.
	 *
	 * @param hit   Who was hit by the compressed air
	 * @param owner Who released the compressed air
	 * @return DamageSource for the compressed air
	 */

	public static DamageSource causeAirDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(AIR.getDamageType(), hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by an airblade.
	 *
	 * @param hit   Who was hit by the airblade
	 * @param owner Who created the airblade
	 */
	public static DamageSource causeAirbladeDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(AIR.getDamageType() + "_airblade", hit, owner).setProjectile();
	}

	//WATER

	/**
	 * Create a DamageSource for generic water damage.
	 *
	 * @param hit   Who was hit by the water
	 * @param owner Who created the water
	 * @return DamageSource for the water
	 */
	public static DamageSource causeWaterDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(WATER.getDamageType(), hit, owner);
	}

	/**
	 * Create a DamageSource for water arc.
	 *
	 * @param hit   Who was hit by the water arc
	 * @param owner Who created the water arc
	 * @return DamageSource for the water arc
	 */
	public static DamageSource causeWaterArcDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(WATER.getDamageType() + "_waterArc", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a wave.
	 *
	 * @param hit   Who was hit by the wave
	 * @param owner Who created the wave
	 */
	public static DamageSource causeWaveDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(WATER.getDamageType() + "_wave", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a water cannon.
	 *
	 * @param hit   Who was hit by the water cannon
	 * @param owner Who created the water cannon
	 */
	public static DamageSource causeWaterCannonDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(WATER.getDamageType() +  "_waterBlast", hit, owner);
	}

	//FIRE

	/**
	 * Create a DamageSource for damage generic fire damage.
	 *
	 * @param hit   Who was hit by the fire
	 * @param owner Who created the fire
	 * @return DamageSource for the fire
	 */
	public static DamageSource causeFireDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(FIRE.getDamageType(), hit, owner);
	}

	/**
	 * Create a DamageSource for fire shot
	 *
	 * @param hit   Who was hit by the fire shot
	 * @param owner Who created the fire shot
	 * @return DamageSource for the fire shot
	 */
	public static DamageSource causeFireShotDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(FIRE.getDamageType() + "_fireShot", hit, owner);
	}


	/**
	 * Create a DamageSource for damage from dragon fire.
	 *
	 * @param hit   Who was hit by the draconic fire
	 * @param owner Who created the dragon fire
	 * @return DamageSource for the Dragon fire
	 */
	public static DamageSource causeDragonFireDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(FIRE.getDamageType() + "_dragonFire", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a fireball.
	 *
	 * @param hit   Who was hit by the fireball
	 * @param owner Who created the fireball
	 */
	public static DamageSource causeFireballDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(FIRE.getDamageType() + "_fireball", hit, owner).setProjectile()
				.setExplosion();
	}

	/**
	 * Create a DamageSource for damage caused by flamethrower.
	 *
	 * @param hit   Who was hit by the flames
	 * @param owner Who created the flames
	 */
	public static DamageSource causeFlamethrowerDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(FIRE.getDamageType() + "_flamethrower", hit, owner).setProjectile();
	}


	//EARTH

	/**
	 * Create a DamageSource for generic earth damage.
	 *
	 * @param hit   Who was hit by the damage
	 * @param owner Who caused the damage
	 * @return DamageSource for the damage
	 */
	public static DamageSource causeEarthDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect(AvatarDamageSource.EARTH.getDamageType(), hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by a floating block.
	 *
	 * @param hit   Who was hit by floating block
	 * @param owner Who threw the floating block
	 * @return DamageSource for the floating block
	 */
	public static DamageSource causeFloatingBlockDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Earth_floatingBlock", hit, owner).setProjectile();
	}

	/**
	 * Create a DamageSource for damage caused by a ravine.
	 *
	 * @param hit   Who was hit by the ravine
	 * @param owner Who created the ravine
	 */
	public static DamageSource causeRavineDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Earth_ravine", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by an earthspike.
	 *
	 * @param hit   Who was hit by the earthspike
	 * @param owner Who created the earthspike
	 */
	public static DamageSource causeEarthspikeDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Earth_earthspike", hit, owner);
	}


	//GENERIC

	/**
	 * Create a DamageSource for damage caused by smashing the ground.
	 *
	 * @param hit   Who was hit by the shockwave
	 * @param owner Who smashed the ground
	 * @param element The element of the shockwave.
	 */
	public static DamageSource causeShockwaveDamage(Entity hit, @Nullable Entity owner, DamageSource element) {
		return new EntityDamageSourceIndirect(element.getDamageType() + "_shockwave", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by a burst of an element
	 *
	 * @param hit   Who was hit by the shockwave
	 * @param owner Who released the shockwave
	 * @param element The element of the shockwave.
	 */
	public static DamageSource causeSphericalShockwaveDamage(Entity hit, @Nullable Entity owner, DamageSource element) {
		return new EntityDamageSourceIndirect(element.getDamageType() + "_sphere_shockwave", hit, owner);
	}

	/**
	 *
	 * @param hit What was hit by the beam.
	 * @param owner What released the beam.
	 * @param element The element of the beam.
	 */
	public static DamageSource causeBeamDamage(Entity hit, @Nullable Entity owner, DamageSource element) {
		return new EntityDamageSourceIndirect(element.getDamageType() + "_beam", hit, owner);
	}



	//LIGHTNING

	/**
	 * Create a DamageSource for damage caused by lightning bending.
	 *
	 * @param hit   Who was hit by lightning
	 * @param owner The lightning bender
	 */
	public static DamageSource causeLightningDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Lightning", hit, owner).setDamageBypassesArmor();
	}

	/**
	 * Create a DamageSource for damage caused by redirected lightning bending.
	 *
	 * @param hit        Who was hit by lightning
	 * @param controller Who redirected the lightning
	 */
	public static DamageSource causeRedirectedLightningDamage(Entity hit, @Nullable Entity
			controller) {
		return new EntityDamageSourceIndirect("avatar_Lightning_redirected", hit, controller)
				.setDamageBypassesArmor();
	}

	/**
	 * Create a DamageSource for damage caused by a lightning spear.
	 *
	 * @param hit        Who was hit by the spear
	 * @param controller Who threw the spear
	 */
	public static DamageSource causeLightningSpearDamage(Entity hit, @Nullable Entity
			controller) {
		return new EntityDamageSourceIndirect("avatar_Lightning_lightningSpear", hit, controller)
				.setDamageBypassesArmor().setProjectile();
	}

	//ICE

	/**
	 * Create a DamageSource for damage caused by an ice prison.
	 *
	 * @param hit   Who was hurt by the ice prison
	 * @param owner Who created the ice prison
	 */
	public static DamageSource causeIcePrisonDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Ice_icePrison", hit, owner).setDamageBypassesArmor();
	}

	/**
	 * Create a DamageSource for damage caused by an ice shard.
	 *
	 * @param hit   Who was hit by the ice shard
	 * @param owner Who created the ice shard
	 */
	public static DamageSource causeIceShardDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Ice_iceShard", hit, owner).setDamageBypassesArmor().setProjectile();
	}

	//SAND

	/**
	 * Create a DamageSource for damage caused by a sand prison.
	 *
	 * @param hit   Who was hurt by the sand prison
	 * @param owner Who created the sand prison
	 */
	public static DamageSource causeSandPrisonDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Sand_sandPrison", hit, owner);
	}

	/**
	 * Create a DamageSource for damage caused by a sandstorm.
	 *
	 * @param hit   Who was hit by the sandstorm
	 * @param owner Who created the sandstorm
	 */
	public static DamageSource causeSandstormDamage(Entity hit, @Nullable Entity owner) {
		return new EntityDamageSourceIndirect("avatar_Sand_sandstorm", hit, owner);
	}

	@SubscribeEvent
	public static void onElementalDamage(LivingHurtEvent event) {
		//TODO: Config for all this stuff; definitely in the rewrite
		DamageSource source = event.getSource();
		Entity hit = event.getEntity();
		if (hit instanceof EntityLivingBase) {
			if (AvatarDamageSource.isAvatarDamageSource(source)) {
				source.setMagicDamage();
			}

			if (AvatarDamageSource.isWaterDamage(source)) {
				hit.setFire(0);
				if (hit instanceof EntityEnderman || hit instanceof EntityEndermite) {
					event.setAmount(event.getAmount() * 1.25F);
				}
				if (hit instanceof EntityGuardian || hit instanceof EntityWaterMob) {
					event.setAmount(event.getAmount() * 0.75F);
				}
			}

			if (AvatarDamageSource.isFireDamage(source)) {
				if (hit.isImmuneToFire()) {
					event.setAmount(event.getAmount() * 0.25F);
				}
				if (((EntityLivingBase) hit).isPotionActive(MobEffects.FIRE_RESISTANCE)) {
					event.setAmount(event.getAmount() * (0.75F / (Objects.requireNonNull(((EntityLivingBase) hit).getActivePotionEffect(MobEffects.FIRE_RESISTANCE)).getAmplifier() + 1)));
				}
			}

			if (AvatarDamageSource.isAirDamage(source)) {
				if (hit instanceof EntityFlying) {
					event.setAmount(event.getAmount() * 1.25F);
				}
			}

			if (AvatarDamageSource.isLightningDamage(source)) {
				source.setDamageBypassesArmor();
				if (hit instanceof EntityWaterMob) {
					event.setAmount(event.getAmount() * 1.5F);
				}
				if (hit instanceof EntityCreeper) {
					AvatarUtils.chargeCreeper((EntityCreeper) hit);
				}

			}

			if (AvatarDamageSource.isCombustionDamage(source)) {
				source.setExplosion();
				if (hit instanceof EntityCreeper) {
					event.setAmount(event.getAmount() * 0.5F);
					AvatarUtils.igniteCreeper((EntityCreeper) hit);
				}
			}

			if (AvatarDamageSource.isSandDamage(source)) {
				if (hit instanceof EntityHusk) {
					event.setAmount(event.getAmount() * 0.75F);
				}
				if (hit.world.rand.nextInt(3) + 1 == 2) {
					((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 5, 0, false, false));
				}
			}

			if (AvatarDamageSource.isIceDamage(source)) {
				if (hit instanceof EntityStray) {
					event.setAmount(event.getAmount() * 0.75F);
				}
				if (hit.world.rand.nextInt(3) + 1 == 2) {
					((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5, 0, false, false));
				}
			}
		}
	}


}
