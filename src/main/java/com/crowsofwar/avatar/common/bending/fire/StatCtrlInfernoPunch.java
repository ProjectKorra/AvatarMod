package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.entity.mob.EntityFirebender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.world.AvatarFireExplosion;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunch extends StatusControl {
	public StatCtrlInfernoPunch() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}


	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		int i = 0;

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			float damageModifier = (float) (bender.calcPowerRating(Firebending.ID) / 100);
			float damage = STATS_CONFIG.InfernoPunchDamage * 1.5F + (2 * damageModifier);
			float knockBack = 0.75F;
			int fireTime = 4;
			Vector direction = Vector.getLookRectangular(entity);
			List<Entity> hit = Raytrace.entityRaytrace(world, Vector.getEyePos(entity), Vector.getLookRectangular(entity).times(1.5), 8, entity1 -> entity1 != entity);
			if (!hit.isEmpty()) {
				for (Entity e : hit) {
					if (e != entity && canDamageEntity(e) && entity.getHeldItemMainhand() == ItemStack.EMPTY) {
						if (world instanceof WorldServer) {
							WorldServer World = (WorldServer) e.getEntityWorld();
							World.spawnParticle(EnumParticleTypes.FLAME, e.posX, e.posY + e.getEyeHeight(), e.posZ, 50, 0.05, 0.05, 0.05, 0.05);

						}
						world.playSound(null, e.posX, e.posY, e.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
								SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
						AxisAlignedBB box = new AxisAlignedBB(e.posX + 2, e.posY + 2, e.posZ + 2, e.posX - 2, e.posY - 2, e.posZ - 2);
						List<Entity> nearby = world.getEntitiesWithinAABB(Entity.class, box);
						if (!nearby.isEmpty()) {
							for (Entity living : nearby) {
								if (living != entity && canDamageEntity(living)) {
									if (world instanceof WorldServer) {
										WorldServer World = (WorldServer) e.getEntityWorld();
										World.spawnParticle(EnumParticleTypes.FLAME, living.posX, living.posY + living.getEyeHeight(), living.posZ, 50, 0.05, 0.05, 0.05, 0.01);

									}
									living.attackEntityFrom(AvatarDamageSource.causeFireDamage(living, entity), damage - (i / 2F));
									living.setFire(fireTime - (i / 2));
									living.motionX += direction.x() * (knockBack - (i / 2F));
									living.motionY += direction.y() * knockBack >= 0 ? (direction.y() * (knockBack / 10)) : knockBack / 10;
									living.motionZ += direction.x() * (knockBack - (i / 2F));
									living.isAirBorne = true;
									// this line is needed to prevent a bug where players will not be pushed in multiplayer
									AvatarUtils.afterVelocityAdded(e);
									i++;

								}
							}
						}

						e.attackEntityFrom(AvatarDamageSource.causeFireDamage(e, entity), damage - (i / 2F));
						e.setFire(fireTime);
						e.motionX += direction.x() * knockBack;
						e.motionY += direction.y() * knockBack >= 0 ? (direction.y() * (knockBack / 8)) : knockBack / 8;
						e.motionZ += direction.z() * knockBack;
						e.isAirBorne = true;
						// this line is needed to prevent a bug where players will not be pushed in multiplayer
						AvatarUtils.afterVelocityAdded(e);
						i++;

					}
				}
				return true;
			}

		}
		return false;

	}


	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		Entity entity = event.getSource().getTrueSource();
		Entity target = event.getEntity();
		DamageSource source = event.getSource();
		World world = target.getEntityWorld();
			if (entity instanceof EntityLivingBase) {
				if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer)) {
					Bender ctx = Bender.get((EntityLivingBase) entity);
					if (ctx.getData() != null) {
						Vector direction = Vector.getLookRectangular(entity);
						AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
						float powerModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
						float damage = STATS_CONFIG.InfernoPunchDamage + (2 * powerModifier);
						float knockBack = 1 + powerModifier;
						int fireTime = 5 + (int) (powerModifier * 10);

						if (abilityData.getLevel() >= 1) {
							damage = 4 + (2 * powerModifier);
							knockBack = 1.125F + powerModifier;
							fireTime = 6;
						} else if (abilityData.getLevel() >= 2) {
							damage = 5 + (2 * powerModifier);
							knockBack = 1.25F + powerModifier;
							fireTime = 8 + (int) (powerModifier * 10);
						}
						if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
							damage = 7 + (2 * powerModifier);
							knockBack = 1.5F + powerModifier;
							fireTime = 15 + (int) (powerModifier * 10);
						}
						if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
							damage = STATS_CONFIG.InfernoPunchDamage * 1.333F + (2 * powerModifier);
							knockBack = 0.75F + powerModifier;
							fireTime = 4 + (int) (powerModifier * 10);
						}

						if (((EntityLivingBase) entity).isPotionActive(MobEffects.STRENGTH)) {
							damage += (Objects.requireNonNull(((EntityLivingBase) entity).getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
						}

						if (ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
							if (((EntityLivingBase) entity).getHeldItemMainhand() == ItemStack.EMPTY && !(source.getDamageType().startsWith("avatar_"))) {
								if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
									EntityShockwave wave = new EntityShockwave(world);
									wave.setPerformanceAmount(15);
									wave.setFire(true);
									wave.setFireTime(15);
									wave.setSphere(true);
									wave.setParticle(EnumParticleTypes.FLAME);
									wave.setParticleSpeed(0.05);
									wave.setParticleAmount(4);
									wave.setParticleController(10);
									//Used for spheres
									wave.setSpeed(0.7);
									wave.setParticleAmount(2);
									wave.setAbility(new AbilityInfernoPunch());
									wave.setDamage(4);
									wave.setOwner((EntityLivingBase) entity);
									wave.setPosition(target.posX, target.getEntityBoundingBox().minY, target.posZ);
									wave.setRange(4);
									wave.setKnockbackHeight(0.25);
									world.spawnEntity(wave);
								}
								if (world instanceof WorldServer) {
									WorldServer World = (WorldServer) target.getEntityWorld();
									World.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY + target.getEyeHeight()/2, target.posZ, 40 + 20 * abilityData.getLevel(), 0.05, 0.05, 0.05, 0.05);

								}

								world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
										SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

								if (target.canBePushed() && target.canBeCollidedWith()) {
									target.attackEntityFrom(DamageSource.IN_FIRE, damage);
									target.setFire(fireTime);
									target.motionX += direction.x() * knockBack;
									target.motionY += direction.y() * knockBack >= 0 ? knockBack / 2 + (direction.y() * knockBack / 2) : knockBack / 2;
									target.motionZ += direction.z() * knockBack;
									target.isAirBorne = true;
									abilityData.addXp(4 - abilityData.getLevel());
									// this line is needed to prevent a bug where players will not be pushed in multiplayer
									AvatarUtils.afterVelocityAdded(target);
								}
								if (!(target instanceof EntityDragon)) {
									ctx.getData().removeStatusControl(INFERNO_PUNCH);
								}
							}
						}
					}
				}
			}
		}

	@SubscribeEvent
	public static void onDragonHurt(LivingHurtEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
			Entity target = event.getEntity();
			if (entity instanceof EntityPlayer || entity instanceof EntityBender) {
				BendingData data = BendingData.get(entity);
				if (data != null) {
					AbilityData aD = AbilityData.get(entity, "inferno_punch");
					Bender ctx = Bender.get(entity);
					if (ctx != null) {
						float damageModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
						float damage = STATS_CONFIG.InfernoPunchDamage + (2 * damageModifier);
						if (data.hasStatusControl(INFERNO_PUNCH) && !(event.getSource().getDamageType().equals("avatar_groundSmash")) &&
								!(event.getSource().getDamageType().equals("avatar_Air"))) {
							if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
								if (aD.getLevel() >= 1) {
									damage = 4 + (2 * damageModifier);
								} else if (aD.getLevel() >= 2) {
									damage = 5 + (2 * damageModifier);
								}
								if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
									damage = 10 + (2 * damageModifier);
								}
								if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
									damage = STATS_CONFIG.InfernoPunchDamage * 1.333F + (2 * damageModifier);
								}
								if (target instanceof EntityDragon) {
									event.setAmount(damage);
									data.removeStatusControl(INFERNO_PUNCH);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean canDamageEntity(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() != entity) {
			return false;
		}
		if (entity instanceof EntityHanging || entity instanceof EntityXPOrb || entity instanceof EntityItem ||
				entity instanceof EntityArmorStand || entity instanceof EntityAreaEffectCloud) {
			return false;
		} else return entity.canBeCollidedWith() && entity.canBePushed();
	}
}
