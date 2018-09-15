package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AirBurstHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("f82d325c-9828-11e8-9eb6-529269fb1459");
	//public static TickHandler AIRBURST_CHARGE_HANDLER = new AirBurstHandler();

	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = null;
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		if (!world.isRemote) {
			abilityData = ctx.getData().getAbilityData("air_burst");
		}
		if (abilityData != null) {

			float powerRating = (float) (bender.calcPowerRating(Airbending.ID) / 100);
			int duration = data.getTickHandlerDuration(this);
			double damage = STATS_CONFIG.airBurstSettings.damage + powerRating;
			//0.5
			float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40F);
			double knockBack = STATS_CONFIG.AirBurstSettings.knockback + powerRating;
			//Default 2 + Power rating
			float radius = STATS_CONFIG.AirBurstSettings.radius + powerRating;
			//Default 3
			float durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire - (powerRating * 10);
			//Default 40
			double upwardKnockback = STATS_CONFIG.airBurstSettings.push / 7;
			float knockbackDivider = 1.2F;
			double suction = 0.05;

			if (abilityData.getLevel() == 1) {
				damage = (STATS_CONFIG.airBurstSettings.damage * (3F / 2)) + powerRating;
				//0.75
				knockBack = 3 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 4 / 3) + powerRating;
				//4
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.75F;
				//30
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 5;
				knockbackDivider = 1.5F;
			}

			if (abilityData.getLevel() >= 2) {
				damage = (STATS_CONFIG.airBurstSettings.damage * 2) + powerRating;
				//1
				knockBack = 5 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 5 / 3) + powerRating;
				//5
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.5F;
				//20
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 3;
				knockbackDivider = 2;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				//Piercing Winds
				damage = 3 + powerRating;
				//Blinds enemies

			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				//Maximum Pressure
				//Pulls enemies in then blasts them out
				damage = 1.5 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 7 / 3) + powerRating;
				//7
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 2.5F;
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.75F;
				//30
			}

			applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			double inverseRadius = (durationToFire - duration) / 10;
			//gets smaller
			suction -= (float) duration / 400;


			if (world instanceof WorldServer) {
				WorldServer World = (WorldServer) world;
				for (int i = 0; i < 12; i++) {
					Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							i * 30), 0).times(inverseRadius).withY(entity.getEyeHeight() / 2);
					World.spawnParticle(EnumParticleTypes.CLOUD, lookpos.x() + entity.posX, lookpos.y() + entity.getEntityBoundingBox().minY,
							lookpos.z() + entity.posZ, 1, 0, 0, 0, 0.005);
				}

			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + radius, entity.posZ + radius, entity.posX - radius, entity.posY - radius, entity.posZ - radius);
				List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box, entity1 -> entity1 != entity);
				if (!collided.isEmpty()) {
					for (Entity e : collided) {
						if (e.canBePushed() && e.canBeCollidedWith() && e != entity) {
							pullEntities(e, entity, suction);
						}
					}
				}
			}

			if (duration >= durationToFire) {
				if (world instanceof WorldServer) {
					WorldServer World = (WorldServer) world;
					double x, y, z;

					for (int i = 0; i < radius; i++) {
						for (int j = 0; j < 90; j++) {
							Vector lookPos;
							if (i >= 1) {
								lookPos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
										j * 4), 0).times(i);
							} else {
								lookPos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
										j * 4), 0);
							}
							World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, lookPos.x() + entity.posX, entity.getEntityBoundingBox().minY,
									lookPos.z() + entity.posZ, 1, 0, 0, 0, (double) radius / 200);
						}
					}

					int particleController = abilityData.getLevel() >= 1 ? 20 - (4 * abilityData.getLevel()) : 20;
					for (double theta = 0; theta <= 180; theta += 1) {
						double dphi = particleController / Math.sin(Math.toRadians(theta));

						for (double phi = 0; phi < 360; phi += dphi) {
							double rphi = Math.toRadians(phi);
							double rtheta = Math.toRadians(theta);

							x = radius * Math.cos(rphi) * Math.sin(rtheta);
							y = radius * Math.sin(rphi) * Math.sin(rtheta);
							z = radius * Math.cos(rtheta);
							//Decrease radius so you can use particle speed

							World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x + entity.posX, y + entity.getEntityBoundingBox().minY,
									z + entity.posZ, 1, 0, 0, 0, (double) radius / 100);

						}
					}//Creates a sphere. Courtesy of Project Korra's Air Burst!

				}

				AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + radius, entity.posZ + radius, entity.posX - radius, entity.posY - radius, entity.posZ - radius);
				List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box, entity1 -> entity1 != entity);
				float xp = abilityData.getLevel() > 0 ? SKILLS_CONFIG.airBurstHit - abilityData.getLevel() : SKILLS_CONFIG.airBurstHit;
				if (!collided.isEmpty()) {
					for (Entity e : collided) {
						if (e.canBePushed() && e.canBeCollidedWith() && e != entity) {
							if (canDamageEntity(e)) {
								e.attackEntityFrom(AvatarDamageSource.causeAirDamage(e, entity), (float) damage);
								if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST) && e instanceof EntityLivingBase) {
									((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 50));
									((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 50));
									((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 50));
								}
							}
							abilityData.addXp(xp);
							BattlePerformanceScore.addLargeScore(entity);
							applyKnockback(e, entity, knockBack, radius, upwardKnockback, knockbackDivider);

						}
					}
				}

				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
						SoundCategory.BLOCKS, 1, 0.5F);
				return true;
			}

			return false;
		} else return true;
	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
				.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
				"Airburst charge modifier", multiplier - 1, 1));

	}

	private void applyKnockback(Entity collided, Entity attacker, double knockBack, float radius, double upwardKnockback, float knockbackDivider) {
		knockBack *= STATS_CONFIG.airBurstSettings.push;

		//Divide the result of the position difference to make entities fly
		//further the closer they are to the player.
		Vector velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
		double distance = Vector.getEntityPos(collided).dist(Vector.getEntityPos(attacker));
		double direction = (radius - distance) * (knockBack / knockbackDivider) / radius;
		velocity = velocity.times(direction).withY(upwardKnockback);


		double x = (velocity.x());
		double y = (velocity.y());
		double z = (velocity.z());

		if (radius - distance == 0) {
			velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
			velocity = velocity.times(-1).withY(upwardKnockback);
			x = 0.01/velocity.x();
			z = 0.01/velocity.z();
		}

		if (!collided.world.isRemote) {
			collided.addVelocity(x, y, z);

			if (collided instanceof AvatarEntity) {
				if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
					AvatarEntity avent = (AvatarEntity) collided;
					avent.addVelocity(x, y, z);
				}
				collided.isAirBorne = true;
				AvatarUtils.afterVelocityAdded(collided);
			}
		}

	}

	private void pullEntities(Entity collided, Entity attacker, double suction) {
		Vector velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
		velocity = velocity.times(suction).times(-1);

		double x = (velocity.x());
		double y = (velocity.y());
		double z = (velocity.z());

		if (!collided.world.isRemote) {
			collided.addVelocity(x, y, z);

			if (collided instanceof AvatarEntity) {
				if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
					AvatarEntity avent = (AvatarEntity) collided;
					avent.addVelocity(x, y, z);
				}
				collided.isAirBorne = true;
				AvatarUtils.afterVelocityAdded(collided);
			}
		}


	}

	@SubscribeEvent
	public static void onDragonHurt(LivingHurtEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			Entity target = event.getEntity();
			DamageSource source = event.getSource();
			if (source.getDamageType().equals("avatar_Air")) {
				if (attacker instanceof EntityPlayer || attacker instanceof EntityBender) {
					Bender ctx = Bender.get(attacker);
					if (ctx.getData() != null) {
						AbilityData aD = AbilityData.get(attacker, "air_burst");
						float powerRating = (float) (ctx.calcPowerRating(Airbending.ID) / 100);
						float damage = 0.5F + powerRating;
						if (aD.getLevel() == 1) {
							damage = 0.75F + powerRating;
						}

						if (aD.getLevel() >= 2) {
							damage = 1 + powerRating;
						}

						if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
							//Piercing Winds
							damage = 5 + powerRating;
						}

						if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
							//Maximum Pressure
							//Pulls enemies in then blasts them out
							damage = 2.5f + powerRating;
						}
						event.setAmount(damage);


					}
				}
			}
		}
	}

	private boolean canDamageEntity(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() == entity) {
			return false;
		}
		if (entity instanceof EntityHanging || entity instanceof EntityXPOrb || entity instanceof EntityItem ||
				entity instanceof EntityArmorStand || entity instanceof EntityAreaEffectCloud) {
			return false;
		} else return entity.canBeCollidedWith() && entity.canBePushed();
	}
}
