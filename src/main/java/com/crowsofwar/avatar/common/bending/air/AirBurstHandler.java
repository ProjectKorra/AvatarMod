package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.*;
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

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AirBurstHandler extends TickHandler {
	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("f82d325c-9828-11e8-9eb6-529269fb1459");

	public AirBurstHandler(int id) {
		super(id);
	}

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

			float powerRating = ((float) bender.getDamageMult(Airbending.ID));
			float xpMod = abilityData.getTotalXp() / 100;

			int duration = data.getTickHandlerDuration(this);
			double damage = STATS_CONFIG.airBurstSettings.damage;
			//Default 5
			float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40F);
			double knockBack = STATS_CONFIG.airBurstSettings.push;
			//Default 2 + Power rating
			float radius = STATS_CONFIG.airBurstSettings.radius;
			//Default 3
			float durationToFire = STATS_CONFIG.airBurstSettings.durationToFire;
			//Default 40
			double upwardKnockback = STATS_CONFIG.airBurstSettings.push / 7;
			double suction = 0.05;
			int performanceAmount = STATS_CONFIG.airBurstSettings.performanceAmount;

			if (abilityData.getLevel() == 1) {
				damage = (STATS_CONFIG.airBurstSettings.damage * 1.5) + powerRating;
				//7.5
				knockBack *= 1.25;
				radius *=  4 / 3F;
				//4
				durationToFire *= 0.75F;
				//30
				upwardKnockback *= (7 / 5F);
				performanceAmount += 3;
			}

			if (abilityData.getLevel() >= 2) {
				damage *= 2;
				//10
				knockBack *= 2;
				radius *= 5 / 3F ;
				//5
				durationToFire *= 0.5F;
				//20
				upwardKnockback *= (7 / 3F);
				performanceAmount += 5;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				//Piercing Winds
				damage *= 1.5;
				//Blinds enemies

			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				//Maximum Pressure
				//Pulls enemies in then blasts them out
				radius *= 2;
				//6
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 2.5F;
				durationToFire *= 0.75F;
				//30
				performanceAmount += 2;
			}

			durationToFire *= (1 / powerRating);
			durationToFire -= xpMod * 10;
			damage *= powerRating * xpMod;
			radius *= powerRating * xpMod;
			knockBack *= powerRating * xpMod;

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

				int particleController = abilityData.getLevel() > 0 ? 44 - (7 * abilityData.getLevel()) : 44;
				EntityShockwave shockwave = new EntityShockwave(world);
				shockwave.setOwner(entity);
				shockwave.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
				shockwave.setParticle(EnumParticleTypes.EXPLOSION_NORMAL);
				shockwave.setElement(new Airbending());
				shockwave.setParticleSpeed(0.08F);
				shockwave.setDamageSource(AvatarDamageSource.AIR);
				shockwave.setKnockbackHeight(upwardKnockback);
				shockwave.setDamage((float) damage);
				shockwave.setParticleAmount(1);
				shockwave.setRange(radius);
				shockwave.setPerformanceAmount(performanceAmount);
				shockwave.setParticleController(particleController);
				shockwave.setParticleAmount(2);
				shockwave.setSphere(true);
				shockwave.setAbility(new AbilityAirBurst());
				shockwave.setSpeed((float) knockBack / 4);
				world.spawnEntity(shockwave);


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
}
