package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import scala.xml.dtd.impl.WordBerrySethi;

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
			int radius = 5;
			int durationToFire = 40;

			if (abilityData.getLevel() == 1) {
				damage = 0.75 + powerRating;
				knockBack = -2.5 - powerRating;
			}

			if (abilityData.getLevel() >= 2) {
				damage = 1 + powerRating;
				knockBack = -3 - powerRating;
				radius = 20;
				durationToFire = 50;
			}

			applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			double inverseRadius = (durationToFire - duration) / 10;
			//gets smaller


			if (world instanceof WorldServer) {
				WorldServer World = (WorldServer) world;
				for (int i = 0; i < 9; i++) {
					Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							i * 40), 0).times(inverseRadius).withY(entity.getEyeHeight()/2);
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

							World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x + entity.posX, y + entity.getEntityBoundingBox().minY + entity.getEyeHeight(),
									z + entity.posZ, 1, 0, 0, 0, (double) radius/100);

						}
					}
					//Creates a sphere. Courtesy of Project Korra's Air Burst!
					for (int i = 0; i < radius; i++) {
						for (int degree = 0; degree < 360; degree++) {
							double radians = Math.toRadians(degree);
							double x1 = Math.cos(radians) * i;
							double z1 = Math.sin(radians) * i;
							double y1 = entity.getEntityBoundingBox().minY + entity.getEyeHeight()/2;
							World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x1 + entity.posX, y1,
									z1 + entity.posZ, 1, 0, 0, 0, (double) radius/100);
						}
					}
				}

				AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + radius, entity.posZ + radius, entity.posX - radius, entity.posY - radius, entity.posZ - radius);
				List<Entity> collided = world.getEntitiesWithinAABB(EntityLivingBase.class, box, entity1 -> entity1 != entity);
				if (!collided.isEmpty()) {
					for (Entity e : collided) {
						if (e instanceof EntityLivingBase) {
							e.attackEntityFrom(AvatarDamageSource.causeAirDamage(e, entity), (float) damage);
							abilityData.addXp(SKILLS_CONFIG.airShockwaveHit);
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
		knockBack *= STATS_CONFIG.airShockwaveSettings.push;
		//Vector vel = attacker.getPositionVector().minus(getEntityPos(collided));
		//vel = vel.normalize().times(mult).plusY(0.3f);

		double x = ((attacker.posX - collided.posX) * knockBack);
		double y = ((attacker.posY - collided.posY) * knockBack) > 0 ? ((attacker.posY - collided.posY) * knockBack) : 0.3F;
		double z = ((attacker.posZ - collided.posZ) * knockBack);
		collided.motionX = x;
		collided.motionY = y;
		collided.motionZ = z;
		if (collided instanceof AvatarEntity) {
			AvatarEntity avent = (AvatarEntity) collided;
			avent.setVelocity(x, y, z);
		}
		collided.isAirBorne = true;
		AvatarUtils.afterVelocityAdded(collided);
	}

}
