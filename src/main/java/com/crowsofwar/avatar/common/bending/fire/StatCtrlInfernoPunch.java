package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunch extends StatusControl {
	private ParticleSpawner particleSpawner;
	private int timesPunched;

	public StatCtrlInfernoPunch() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		particleSpawner = new NetworkParticleSpawner();
		this.timesPunched = 0;
	}

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
							float powerModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
							float damage = STATS_CONFIG.InfernoPunchDamage + (2 * powerModifier);
							float knockBack = 1 + powerModifier;
							int fireTime = 5 + (int) (powerModifier * 10);

							if (abilityData.getLevel() >= 1) {
								damage = 4 + (2 * powerModifier);
								knockBack = 1.125F + powerModifier;
								fireTime = 6;
							}
							if (abilityData.getLevel() >= 2) {
								damage = 5 + (2 * powerModifier);
								knockBack = 1.25F + powerModifier;
								fireTime = 8 + (int) (powerModifier * 10);
							}
							if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
								damage = 7 + (2 * powerModifier);
								knockBack = 1.5F + powerModifier;
								fireTime = 15 + (int) (powerModifier * 10);
							}

							if (((EntityLivingBase) entity).isPotionActive(MobEffects.STRENGTH)) {
								damage += (Objects.requireNonNull(((EntityLivingBase) entity).getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
							}

							if (ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
								if (((EntityLivingBase) entity).getHeldItemMainhand() == ItemStack.EMPTY && !(source.getDamageType().startsWith("avatar_"))) {
									if (!abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
										if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
											EntityShockwave wave = new EntityShockwave(world);
											wave.setPerformanceAmount(15);
											wave.setFireTime(15);
											wave.setSphere(true);
											wave.setParticleName(AvatarParticles.getParticleBigFlame().getParticleName());
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
												World.spawnParticle(EnumParticleTypes.FLAME, target.posX + pos.x(), (target.posY + (target.getEyeHeight() / 1.25)) + pos.y(), target.posZ + pos.z(),
														4 + abilityData.getLevel(), 0.0, 0.0, 0.0, 0.03 + (abilityData.getLevel() / 100F));
											}
										}

										world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
												SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

										if (target.canBePushed() && target.canBeCollidedWith()) {
											DamageSource fire = AvatarDamageSource.FIRE;
											//Creating a new damage source with the attacker as the source results in an infinite loop
											target.attackEntityFrom(fire, damage);
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
			}
		}
	}

	@SubscribeEvent
	public static void onDragonHurt(LivingHurtEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
			Entity target = event.getEntity();
			if (entity instanceof EntityPlayer || entity instanceof EntityBender) {
				Bender ctx = Bender.get(entity);
				if (ctx != null) {
					if (ctx.getInfo().getId() != null) {
						if (ctx.getData() != null) {
							float damageModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
							float damage = STATS_CONFIG.InfernoPunchDamage + (2 * damageModifier);
							BendingData data = BendingData.get(entity);
							AbilityData aD = AbilityData.get(entity, "inferno_punch");
							if (data.hasStatusControl(INFERNO_PUNCH) && !(event.getSource().getDamageType().equals("avatar_groundSmash")) &&
									!(event.getSource().getDamageType().equals("avatar_Air"))) {
								if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
									if (aD.getLevel() >= 1) {
										damage = 4 + (2 * damageModifier);
									} else if (aD.getLevel() >= 2) {
										damage = 5 + (2 * damageModifier);
									}
									if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
										damage = 7 + (2 * damageModifier);
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
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		HashSet<Entity> excluded = new HashSet<>();

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND) && !ctx.getData().hasTickHandler(TickHandlerController.INFERNO_PUNCH_COOLDOWN)) {
			float damageModifier = (float) (bender.calcPowerRating(Firebending.ID) / 100);
			float damage = STATS_CONFIG.InfernoPunchDamage * 1.5F + (damageModifier);
			float knockBack = 0.75F;
			int fireTime = 4;
			Vector direction = Vector.getLookRectangular(entity);
			RayTraceResult result = AvatarUtils.standardEntityRayTrace(world, entity, null, Vector.getEyePos(entity).toMinecraft(),
					entity.getLookVec().scale(8).add(entity.getPositionVector()), 0.25F, false, excluded);
			if (result != null) {
				if (result.entityHit != null) {
					Entity e = result.entityHit;
					if (e != entity && canDamageEntity(e) && entity.getHeldItemMainhand() == ItemStack.EMPTY) {
						if (world instanceof WorldServer) {
							WorldServer World = (WorldServer) e.getEntityWorld();
							//Spawns the particles in a line towards where the player is looking
							double dist = entity.getDistance(e);
							for (double j = 0; j < 1; j += 1 / dist) {
								Vector startPos = Vector.getEyePos(entity).minusY(0.25);
								Vector distance = Vector.getEyePos(e).minusY(0.4).minus(startPos);
								distance = distance.times(j);
								particleSpawner.spawnParticles(world, EnumParticleTypes.FLAME, 4, 8,
										startPos.x() + distance.x(), startPos.y() + distance.y(), startPos.z() + distance.z(), 0, 0, 0);
							}
							//Spawns particles as if a fireball has slammed into the enemy
							World.spawnParticle(EnumParticleTypes.FLAME, e.posX, e.posY + e.getEyeHeight(), e.posZ, 50, 0.05, 0.05, 0.05, 0.075);

						}
						world.playSound(null, e.posX, e.posY, e.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
								SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

						e.attackEntityFrom(AvatarDamageSource.causeFireDamage(e, entity), damage + (timesPunched / 2F));
						e.setFire(fireTime);
						e.motionX += direction.x() * knockBack;
						e.motionY += direction.y() * knockBack >= 0 ? (direction.y() * (knockBack / 8)) : knockBack / 8;
						e.motionZ += direction.z() * knockBack;
						e.isAirBorne = true;
						// this line is needed to prevent a bug where players will not be pushed in multiplayer
						AvatarUtils.afterVelocityAdded(e);
						timesPunched++;
						ctx.getData().addTickHandler(TickHandlerController.INFERNO_PUNCH_COOLDOWN);
						AxisAlignedBB box = new AxisAlignedBB(e.posX + 2, e.posY + 2, e.posZ + 2, e.posX - 2, e.posY - 2, e.posZ - 2);
						List<Entity> nearby = world.getEntitiesWithinAABB(Entity.class, box);
						if (!nearby.isEmpty()) {
							for (Entity living : nearby) {
								if (living != entity && canDamageEntity(living) && e != living) {
									if (world instanceof WorldServer) {
										WorldServer World = (WorldServer) e.getEntityWorld();
										World.spawnParticle(EnumParticleTypes.FLAME, living.posX, living.posY + living.getEyeHeight(), living.posZ, 50, 0.05, 0.05, 0.05, 0.01);

									}
									living.attackEntityFrom(AvatarDamageSource.causeFireDamage(living, entity), damage + (timesPunched / 2F));
									living.setFire(fireTime + (timesPunched / 2));
									living.motionX += direction.x() * (knockBack + (timesPunched / 2F));
									living.motionY += direction.y() * knockBack >= 0 ? (direction.y() * (knockBack / 10)) : knockBack / 10;
									living.motionZ += direction.x() * (knockBack + (timesPunched / 2F));
									living.isAirBorne = true;
									// this line is needed to prevent a bug where players will not be pushed in multiplayer
									AvatarUtils.afterVelocityAdded(living);

								}
							}
						}
					}
				}
			}
			return result != null && result.entityHit != null && timesPunched >= 2 && !ctx.getData().hasTickHandler(TickHandlerController.INFERNO_PUNCH_COOLDOWN);

		}
		return false;

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
