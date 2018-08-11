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
import net.minecraft.init.SoundEvents;
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
			float knockbackDivider = (float) STATS_CONFIG.airBurstSettings.push/4;

			if (abilityData.getLevel() == 1) {
				damage = (STATS_CONFIG.airBurstSettings.damage * (3F / 2)) + powerRating;
				//0.75
				knockBack = 3 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 4/3) + powerRating;
				//4
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.75F;
				//30
			}

			if (abilityData.getLevel() >= 2) {
				damage = (STATS_CONFIG.airBurstSettings.damage * 2) + powerRating;
				//1
				knockBack = 5 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 5/3) + powerRating;
				//5
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.5F;
				//20
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				//Piercing Winds
				damage = 3 + powerRating;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				//Maximum Pressure
				//Pulls enemies in then blasts them out
				damage = 1.5 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 7/3) + powerRating;
				//7
			}

			applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			double inverseRadius = ((float) durationToFire - duration) / 10;
			//gets smaller


			if (world instanceof WorldServer) {
				WorldServer World = (WorldServer) world;
				for (int i = 0; i < 9; i++) {
					Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							i * 40), 0).times(inverseRadius).withY(entity.getEyeHeight() / 2);
					World.spawnParticle(EnumParticleTypes.CLOUD, lookpos.x() + entity.posX, lookpos.y() + entity.getEntityBoundingBox().minY,
							lookpos.z() + entity.posZ, 1, 0, 0, 0, 0.005);
				}

			}


			if (duration >= durationToFire) {
				if (world instanceof WorldServer) {
					WorldServer World = (WorldServer) world;
					double x, y, z;

					for (double theta = 0; theta <= 180; theta += 1) {
						double dphi = 10 / Math.sin(Math.toRadians(theta));

						for (double phi = 0; phi < 360; phi += dphi) {
							double rphi = Math.toRadians(phi);
							double rtheta = Math.toRadians(theta);

							x = radius * Math.cos(rphi) * Math.sin(rtheta);
							y = radius * Math.sin(rphi) * Math.sin(rtheta);
							z = radius * Math.cos(rtheta);
							//Decrease radius so you can use particle speed

							World.spawnParticle(EnumParticleTypes.CLOUD, x + entity.posX, y + entity.getEntityBoundingBox().minY,
									z + entity.posZ, 1, 0, 0, 0, (double) radius / 100);

						}
					}//Creates a sphere. Courtesy of Project Korra's Air Burst!

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
							World.spawnParticle(EnumParticleTypes.CLOUD, lookPos.x() + entity.posX, entity.getEntityBoundingBox().minY,
									lookPos.z() + entity.posZ, 2, 0, 0, 0, (double) radius / 50);
						}
					}
				}

				AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + radius, entity.posZ + radius, entity.posX - radius, entity.posY - radius, entity.posZ - radius);
				List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box, entity1 -> entity1 != entity);
				float xp = abilityData.getLevel() > 0 ? SKILLS_CONFIG.airBurstHit - abilityData.getLevel() : SKILLS_CONFIG.airBurstHit;
				if (!collided.isEmpty()) {
					for (Entity e : collided) {
						if (e.canBePushed() && e.canBeCollidedWith() && e != entity) {
							if (canDamageEntity(e)) {
								e.attackEntityFrom(AvatarDamageSource.causeAirDamage(e, entity), (float) damage);
							}
							abilityData.addXp(xp);
							BattlePerformanceScore.addLargeScore(entity);
							applyKnockback(e, entity, knockBack);
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

	private void applyKnockback(Entity collided, Entity attacker, double knockBack) {
		knockBack *= STATS_CONFIG.airBurstSettings.push;

		//Divide the result of the position difference to make entities fly
		//further the closer they are to the player.
		Vector velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
		velocity = velocity.times(knockBack / 7.5);

		double x = (0.25/velocity.x());
		double y = (velocity.y()) > 0 ? (velocity.y()) : STATS_CONFIG.airBurstSettings.push/3F;
		double z = (0.25/velocity.z());

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

	private boolean canDamageEntity(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() != entity) {
			return false;
		}
		if (entity instanceof EntityHanging || entity instanceof EntityXPOrb || entity instanceof EntityItem ||
				entity instanceof EntityArmorStand || entity instanceof EntityAreaEffectCloud) {
			return false;
		}
		else return entity.canBeCollidedWith() && entity.canBePushed();
	}
}
