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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
			double damage = 0.5 + powerRating;
			float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
			double knockBack = -2 - powerRating;
			//The negative number doesn't mean it's small- in fact, the smaller the number, the larger the knockback
			int radius = 6;
			int durationToFire = 40;
			double knockbackMax = 12;
			double knockbackMin = 6;

			if (abilityData.getLevel() == 1) {
				damage = 0.75 + powerRating;
				knockBack = -2.5 - powerRating;
				radius = 8;
				knockbackMax = 16;
				knockbackMin = 8;
			}

			if (abilityData.getLevel() >= 2) {
				damage = 1 + powerRating;
				knockBack = -3 - powerRating;
				radius = 12;
				durationToFire = 50;
				knockbackMax = 20;
				knockbackMin = 10;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				//Piercing Winds
				damage = 10 + powerRating;
				radius = 18;
				knockbackMax = 30;
				knockbackMin = 16;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				//Maximum Pressure
				//Pulls enemies in then blasts them out
				radius = 16;
				knockbackMin = 14;
				knockbackMax = 26;
				knockBack = -3.5 - powerRating;
			}

			applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			double inverseRadius = (durationToFire - duration) / 10;
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

							World.spawnParticle(EnumParticleTypes.CLOUD, x + entity.posX, y + entity.getEntityBoundingBox().minY + entity.getEyeHeight(),
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
				if (!collided.isEmpty()) {
					for (Entity e : collided) {
						if (e.canBePushed() && e.canBeCollidedWith() && e != entity || (e instanceof AvatarEntity && ((AvatarEntity) e).getOwner() != entity)) {
							e.attackEntityFrom(AvatarDamageSource.causeAirDamage(e, entity), (float) damage);
							abilityData.addXp(SKILLS_CONFIG.airShockwaveHit);
							BattlePerformanceScore.addLargeScore(entity);
							applyKnockback(e, entity, knockBack, knockbackMax, knockbackMin);
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

	private void applyKnockback(Entity collided, Entity attacker, double knockBack, double knockbackMax, double knockbackMin) {
		knockBack *= STATS_CONFIG.airShockwaveSettings.push;

		//Divide the result of the position difference to make entities fly
		//further the closer they are to the player.
		double x = (0.5 / (attacker.posX - collided.posX) * knockBack);
		double y = (0.5 / (attacker.posY - collided.posY) * knockBack) > 0 ? (0.5 / (attacker.posY - collided.posY) * knockBack) : 0.3F;
		double z = (0.5 / (attacker.posZ - collided.posZ) * knockBack);
		//These make sure the knockback for the x and y is never great than the maximum. and never
		//less than the minimum.
		if (x > 0 && x > knockbackMax) {
			x = knockbackMax;
		}
		if (x > 0 && x < knockbackMin) {
			x = knockbackMin;
		}
		if (x < 0 && x < -knockbackMax) {
			x = -knockbackMax;
		}
		if (x < 0 && x > -knockbackMin) {
			x = -knockbackMin;
		}
		if (z > 0 && z > knockbackMax) {
			z = knockbackMax;
		}
		if (z > 0 && z < knockbackMin) {
			z = knockbackMin;
		}
		if (z < 0 && z < -knockbackMax) {
			z = -knockbackMax;
		}
		if (z < 0 && z > -knockbackMin) {
			z = -knockbackMin;
		}
		collided.motionX += x;
		collided.motionY += y;
		collided.motionZ += z;
		if (collided instanceof AvatarEntity) {
			if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
				AvatarEntity avent = (AvatarEntity) collided;
				avent.addVelocity(x, y, z);
			}
			collided.isAirBorne = true;
			AvatarUtils.afterVelocityAdded(collided);
		}

	}

	@SubscribeEvent
	public static void onDragonHurt(LivingHurtEvent event) {
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		Entity target = event.getEntity();
		DamageSource source = event.getSource();
		if (source.getDamageType().equals("avatar_Air")) {
			System.out.println("Step 1");
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

					event.setAmount(damage);
					System.out.println(event.getAmount());


				}
			}
		}
	}
}
