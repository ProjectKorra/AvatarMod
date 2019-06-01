package com.crowsofwar.avatar.common.bending.air;

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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AirBurstHandler extends TickHandler {
	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("f82d325c-9828-11e8-9eb6-529269fb1459");

	public AirBurstHandler(int id) {
		super(id);
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
					if (ctx != null) {
						if (ctx.getInfo().getId() != null) {
							if (ctx.getData() != null) {
								AbilityData aD = AbilityData.get(attacker, "air_burst");
								float powerRating = (float) (ctx.calcPowerRating(Airbending.ID) / 100);
								float damage = STATS_CONFIG.airBurstSettings.damage + powerRating;
								if (aD.getLevel() == 1) {
									damage = STATS_CONFIG.airBurstSettings.damage * 1.5F + powerRating;
								}

								if (aD.getLevel() >= 2) {
									damage = STATS_CONFIG.airBurstSettings.damage * 2 + powerRating;
								}

								if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
									//Piercing Winds
									damage = STATS_CONFIG.airBurstSettings.damage * 3 + powerRating;
								}

								if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
									//Maximum Pressure
									//Pulls enemies in then blasts them out
									damage = STATS_CONFIG.airBurstSettings.damage * 2.5F + powerRating;
								}
								event.setAmount(damage);


							}
						}
					}
				}
			}
		}
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

			float powerRating = (float) (bender.calcPowerRating(Airbending.ID) / 100);
			int duration = data.getTickHandlerDuration(this);
			double damage = STATS_CONFIG.airBurstSettings.damage + powerRating;
			//Default 5
			float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40F);
			double knockBack = STATS_CONFIG.AirBurstSettings.knockback + powerRating;
			//Default 2 + Power rating
			float radius = STATS_CONFIG.AirBurstSettings.radius + powerRating;
			//Default 3
			float durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire - (powerRating * 10);
			//Default 40
			double upwardKnockback = STATS_CONFIG.airBurstSettings.push / 7;
			double suction = 0.05;
			int performanceAmount = STATS_CONFIG.AirBurstSettings.performanceAmount;

			if (abilityData.getLevel() == 1) {
				damage = (STATS_CONFIG.airBurstSettings.damage * 1.5) + powerRating;
				//7.5
				knockBack = STATS_CONFIG.AirBurstSettings.knockback * 1.5 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 4 / 3) + powerRating;
				//4
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.75F;
				//30
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 5;
				performanceAmount += 3;
			}

			if (abilityData.getLevel() >= 2) {
				damage = (STATS_CONFIG.airBurstSettings.damage * 2) + powerRating;
				//10
				knockBack = 5 + powerRating;
				radius = (STATS_CONFIG.AirBurstSettings.radius * 5 / 3) + powerRating;
				//5
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.5F;
				//20
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 3;
				performanceAmount += 5;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				//Piercing Winds
				damage = (STATS_CONFIG.airBurstSettings.damage * 3) + powerRating;
				//Default: 15
				//Blinds enemies

			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				//Maximum Pressure
				//Pulls enemies in then blasts them out
				damage = (STATS_CONFIG.airBurstSettings.damage * 2.5) + powerRating;
				//Default: 12.5
				radius = (STATS_CONFIG.AirBurstSettings.radius * 7 / 3) + powerRating;
				//7
				upwardKnockback = STATS_CONFIG.airBurstSettings.push / 2.5F;
				durationToFire = STATS_CONFIG.AirBurstSettings.durationToFire * 0.75F;
				//30
				performanceAmount += 8;
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

				int particleController = abilityData.getLevel() >= 1 ? 37 - (5 * abilityData.getLevel()) : 37;
				EntityShockwave shockwave = new EntityShockwave(world);
				shockwave.setOwner(entity);
				shockwave.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
				shockwave.setParticleName("explosion");
				shockwave.setElement(new Airbending());
				shockwave.setParticleSpeed(0.08F);
				shockwave.setKnockbackHeight(upwardKnockback);
				shockwave.setDamage((float) damage);
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
