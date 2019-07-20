package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.common.bending.StatusControl.*;
import static com.crowsofwar.avatar.common.bending.fire.AbilityFireShot.*;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireEventListener {
	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		Entity entity = event.getSource().getTrueSource();
		Entity target = event.getEntity();
		DamageSource source = event.getSource();
		World world = target.getEntityWorld();
		if (entity instanceof EntityLivingBase && !AvatarDamageSource.isAvatarDamageSource(source)) {
			if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer)) {
				Bender ctx = Bender.get((EntityLivingBase) entity);
				if (ctx != null) {
					if (ctx.getInfo().getId() != null) {
						if (ctx.getData() != null) {
							Vector direction = Vector.getLookRectangular(entity);
							AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
							BendingData data = BendingData.get((EntityLivingBase) entity);
							boolean hasInfernoPunch = data.hasStatusControl(INFERNO_PUNCH_MAIN) || data.hasStatusControl(INFERNO_PUNCH_FIRST);
							float powerModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
							float damage = STATS_CONFIG.InfernoPunchDamage + (2 * powerModifier);
							float knockBack = 1 + powerModifier;
							int fireTime = 5 + (int) (powerModifier * 10);

							if (abilityData.getLevel() >= 1) {
								damage = STATS_CONFIG.InfernoPunchDamage * 4 / 3 + (2 * powerModifier);
								knockBack = 1.125F + powerModifier;
								fireTime = 6;
							}
							if (abilityData.getLevel() >= 2) {
								damage = STATS_CONFIG.InfernoPunchDamage * 5 / 3 + (2 * powerModifier);
								knockBack = 1.25F + powerModifier;
								fireTime = 8 + (int) (powerModifier * 10);
							}
							if (data.hasStatusControl(INFERNO_PUNCH_FIRST)) {
								damage = STATS_CONFIG.InfernoPunchDamage * 7 / 3 + (2 * powerModifier);
								knockBack = 1.5F + powerModifier;
								fireTime = 15 + (int) (powerModifier * 10);
							}

							if (((EntityLivingBase) entity).isPotionActive(MobEffects.STRENGTH)) {
								damage += (Objects.requireNonNull(((EntityLivingBase) entity).getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
							}

							if (hasInfernoPunch) {
								if (((EntityLivingBase) entity).getHeldItemMainhand() == ItemStack.EMPTY && !(source.getDamageType().startsWith("avatar_"))) {
									if (!data.hasStatusControl(INFERNO_PUNCH_SECOND)) {
										if (data.hasStatusControl(INFERNO_PUNCH_FIRST)) {
											EntityShockwave wave = new EntityShockwave(world);
											wave.setPerformanceAmount(15);
											wave.setFireTime(15);
											wave.setSphere(true);
											wave.setParticle(AvatarParticles.getParticleBigFlame());
											wave.setParticleSpeed(0.12F);
											wave.setParticleAmount(1);
											wave.setParticleController(35);
											//Used for spheres
											wave.setSpeed(0.8F);
											wave.setDamageSource(AvatarDamageSource.FIRE);
											wave.setParticleAmount(2);
											wave.setAbility(new AbilityInfernoPunch());
											wave.setDamage(3);
											wave.setOwner((EntityLivingBase) entity);
											wave.setPosition(target.posX, target.getEntityBoundingBox().minY, target.posZ);
											wave.setRange(4);
											wave.setKnockbackHeight(0.2);
											world.spawnEntity(wave);
										}
										if (world instanceof WorldServer) {
											WorldServer World = (WorldServer) target.getEntityWorld();
											for (double angle = 0; angle < 360; angle += 15) {
												Vector pos = Vector.getOrthogonalVector(Vector.getLookRectangular(entity), angle, 0.2);
												World.spawnParticle(world.rand.nextBoolean() ? AvatarParticles.getParticleFlames() : AvatarParticles.getParticleFire(),
														target.posX + pos.x(), (target.posY + (target.getEyeHeight() / 1.25)) + pos.y(), target.posZ + pos.z(),
														4 + abilityData.getLevel(), 0.0, 0.0, 0.0, 0.03 + (abilityData.getLevel() / 100F));
											}
										}

										world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
												SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

										if (target.canBePushed() && target.canBeCollidedWith()) {
											DamageSource fire = AvatarDamageSource.causeInfernoPunchDamage(target, entity);
											//Creating a new damage source with the attacker as the source results in an infinite loop
											target.attackEntityFrom(fire, damage);
											target.setFire(fireTime);
											target.motionX += direction.x() * knockBack;
											target.motionY += direction.y() * knockBack >= 0 ? knockBack / 4 + (direction.y() * knockBack / 4) : knockBack / 4;
											target.motionZ += direction.z() * knockBack;
											target.isAirBorne = true;
											abilityData.addXp(4 - abilityData.getLevel());
											// this line is needed to prevent a bug where players will not be pushed in multiplayer
											AvatarUtils.afterVelocityAdded(target);
										}
										ctx.getData().removeStatusControl(INFERNO_PUNCH_FIRST);
										ctx.getData().removeStatusControl(INFERNO_PUNCH_MAIN);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUpdateEvent(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving() != null) {
			EntityLivingBase entity = event.getEntityLiving();
			if (entity instanceof EntityPlayer || entity instanceof EntityBender) {
				Bender b = Bender.get(entity);
				if (b != null) {
					if (b.getData().hasBendingId(Firebending.ID)) {
						if (!ignitedTimes.isEmpty()) {
							UUID benderUUID = b.getEntity().getUniqueID();
							for (BlockPos pos : ignitedTimes.get(benderUUID).keySet()) {
								HashMap<BlockPos, Integer> newIgnitedTime = new HashMap<>();
								getIgnitedTimes(benderUUID).forEach((blockPos, integer) -> {
									newIgnitedTime.put(blockPos, integer - 1);
								});
								if (entity.getUniqueID().toString().equals(getIgnitedOwner(pos))) {
									ignitedTimes.replace(benderUUID, getIgnitedTimes(benderUUID), newIgnitedTime);
									if (getIgnitedTimes(benderUUID).get(pos) == 0) {
										if (entity.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
											entity.world.setBlockToAir(pos);
											ignitedTimes.remove(pos);
											ignitedBlocks.remove(pos);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}