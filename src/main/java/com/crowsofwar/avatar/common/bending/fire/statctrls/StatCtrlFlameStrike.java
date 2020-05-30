package com.crowsofwar.avatar.common.bending.fire.statctrls;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.fire.AbilityFlameStrike;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.damageutils.DamageUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityShield;
import com.crowsofwar.avatar.common.entity.IShieldEntity;
import com.crowsofwar.avatar.common.event.ParticleCollideEvent;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_OFF;
import static com.crowsofwar.avatar.common.data.TickHandlerController.FLAME_STRIKE_HANDLER;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class StatCtrlFlameStrike extends StatusControl {

	private static HashMap<UUID, Integer> timesUsed = new HashMap<>();
	private static HashMap<UUID, Integer> chargeLevel = new HashMap<>();
	//Ensures the target has been struck by the centre of the flame blast.
	private static HashMap<UUID, Integer> timesHit = new HashMap<>();
	EnumHand hand;

	public StatCtrlFlameStrike(EnumHand hand) {
		super(18, hand == EnumHand.MAIN_HAND ? CONTROL_LEFT_CLICK : CONTROL_RIGHT_CLICK,
				hand == EnumHand.MAIN_HAND ? CrosshairPosition.LEFT_OF_CROSSHAIR : CrosshairPosition.RIGHT_OF_CROSSHAIR);
		this.hand = hand;
	}

	public static int getTimesUsed(UUID id) {
		return timesUsed.getOrDefault(id, 0);
	}

	public static void setTimesUsed(UUID id, int times) {
		if (timesUsed.containsKey(id))
			timesUsed.replace(id, times);
		else timesUsed.put(id, times);
	}

	public static int getChargeLevel(UUID id) {
		return chargeLevel.getOrDefault(id, 1);
	}

	public static void setChargeLevel(UUID id, int level) {
		if (chargeLevel.containsKey(id)) {
			chargeLevel.replace(id, level);
		} else chargeLevel.put(id, level);
	}

	public static int getTimesHit(UUID id) {
		return chargeLevel.getOrDefault(id, 1);
	}

	public static void setTimesHit(UUID id, int hit) {
		if (timesHit.containsKey(id)) {
			timesHit.replace(id, hit);
		} else timesHit.put(id, hit);
	}

	@SubscribeEvent
	public static void particleCollision(ParticleCollideEvent event) {
		if (event.getAbility() instanceof AbilityFlameStrike) {
			if (event.getSpawner() != event.getEntity()) {
				if (event.getSpawner() instanceof EntityLivingBase) {
					if (AvatarUtils.getMagnitude(event.getVelocity()) > 0.5)
						attackEntity((EntityLivingBase) event.getSpawner(), event.getEntity(), event.getVelocity());
				}
			}
		}
	}

	private static boolean attackEntity(EntityLivingBase attacker, Entity target, Vec3d vel) {
		AbilityData abilityData = AbilityData.get(attacker, new AbilityFlameStrike().getName());
		Bender bender = Bender.get(attacker);
		World world = attacker.world;
		if (abilityData != null && bender != null && !world.isRemote) {
			float powerModifier = (float) (bender.getDamageMult(Firebending.ID));
			float xpMod = abilityData.getXpModifier();

			float damage = STATS_CONFIG.flameStrikeSettings.damage;
			int performance = STATS_CONFIG.flameStrikeSettings.performanceAmount;
			float knockBack = STATS_CONFIG.flameStrikeSettings.knockback;
			int fireTime = STATS_CONFIG.flameStrikeSettings.fireTime;
			float xp = SKILLS_CONFIG.flameStrikeHit;

			if (abilityData.getLevel() == 1) {
				damage *= 1.25F;
				knockBack *= 1.125F;
				fireTime += 2;
				performance += 2;
			}
			if (abilityData.getLevel() == 2) {
				damage *= 2F;
				knockBack *= 1.25F;
				fireTime += 4;
				performance += 5;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage *= 2.5F;
				performance += 10;
				fireTime += 3;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage *= 4;
				performance += 2;
				fireTime += 5;
			}

			damage *= powerModifier * xpMod;
			knockBack *= powerModifier * xpMod;
			fireTime *= powerModifier * xpMod;
			performance *= powerModifier * xpMod;

			vel = vel.scale(0.0005).scale(knockBack);


			if (canDamageEntity(target, attacker)) {
				if (!(target instanceof EntityLivingBase) || ((EntityLivingBase) target).attackable() &&
						((EntityLivingBase) target).hurtTime == 0)
					DamageUtils.attackEntity(attacker, target, AvatarDamageSource.causeFireDamage(target, attacker), damage, performance,
							new AbilityFlameStrike(), xp);
				else {
					//NOTE: Add velocity like this is great for stuff like a water blast!
					target.addVelocity(vel.x, vel.y + 0.15, vel.z);
					target.motionY = Math.min(0.15, target.motionY);
				}

			} else if (canCollideWithEntity(target, attacker)) {
				//NOTE: Add velocity like this is great for stuff like a water blast!
				target.addVelocity(vel.x, vel.y + 0.15, vel.z);
				target.motionY = Math.min(0.15, target.motionY);
			}
			target.setFire(fireTime);
			setTimesHit(target.getUniqueID(), 0);

		}
		return false;
	}

	private static boolean canCollideWithEntity(Entity entity, Entity owner) {
		if (entity instanceof AvatarEntity) {
			if (((AvatarEntity) entity).getOwner() == owner)
				return false;
			else if (!entity.canBeCollidedWith())
				return false;
			else if (entity instanceof EntityShield)
				return true;
		} else if (entity.getTeam() != null && owner.getTeam() != null && entity.getTeam() == owner.getTeam())
			return false;
		else if (entity instanceof EntityTameable && ((EntityTameable) entity).getOwner() == owner)
			return false;
		else if (entity.getRidingEntity() == owner)
			return false;
		return entity instanceof EntityLivingBase || entity instanceof EntityEnderCrystal || entity.canBeCollidedWith() || entity instanceof EntityArrow
				|| entity instanceof EntityThrowable;
	}

	private static boolean canDamageEntity(Entity entity, Entity owner) {
		if (entity instanceof AvatarEntity) {
			if (((AvatarEntity) entity).getOwner() == owner)
				return false;
			else if (!entity.canBeCollidedWith())
				return false;
			else if (entity instanceof EntityShield || entity instanceof IShieldEntity)
				return true;
		} else if (entity.getTeam() != null && owner.getTeam() != null && entity.getTeam() == owner.getTeam())
			return false;
		else if (entity instanceof EntityTameable && ((EntityTameable) entity).getOwner() == owner)
			return false;
		else if (entity.getRidingEntity() == owner)
			return false;
		return entity instanceof EntityLivingBase || entity instanceof EntityEnderCrystal || entity.canBeCollidedWith() && entity.canBeAttackedWithItem();
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getData().getAbilityData("flame_strike");

		if (!ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
			return true;
		if (!entity.getHeldItem(hand).isEmpty())
			return false;

		float size = STATS_CONFIG.flameStrikeSettings.size;
		float dist = STATS_CONFIG.flameStrikeSettings.maxDistance;
		float accuracyMult = 0.05F;
		int particleCount = 4;
		float mult = 0.4F;

		if (abilityData.getLevel() == 1) {
			particleCount += 2;
			size *= 1.25;
			mult += 0.05F;
		}
		if (abilityData.getLevel() == 2) {
			particleCount += 4;
			size *= 1.25;
			dist = 4;
			mult += 0.1F;
			accuracyMult *= 1.5F;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			size *= 0.4F;
			particleCount += 10;
			accuracyMult = 0.03F;
			dist = 7;
			mult = 0.7F;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			size *= 2.25F;
			particleCount -= 5;
			accuracyMult *= 2;
			mult = 0.8F;
		}

		int lifeTime = (int) dist * 2;


		Vec3d look = entity.getLookVec();
		double eyePos = entity.getEyeHeight() + entity.getEntityBoundingBox().minY;


		if (world.isRemote) {
			//Spawn particles
			if (CLIENT_CONFIG.fireRenderSettings.solidFireParticles) {
				for (int i = 0; i < 15 + particleCount; i++) {
					double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult;
					double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
					double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult;

					//Using the random function each time ensures a different number for every value, making the ability "feel" better.
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
							look.y * mult + world.rand.nextGaussian() * accuracyMult,
							look.z * mult + world.rand.nextGaussian() * accuracyMult)
							.element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
							.clr(255, 15, 5).collide(true).scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
					//Using the random function each time ensures a different number for every value, making the ability "feel" better.
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
							look.y * mult + world.rand.nextGaussian() * accuracyMult,
							look.z * mult + world.rand.nextGaussian() * accuracyMult)
							.element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
							.clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true)
							.scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
							look.y * mult + world.rand.nextGaussian() * accuracyMult,
							look.z * mult + world.rand.nextGaussian() * accuracyMult)
							.element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity).collide(true)
							.scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
				}
			} else {
				for (int i = 0; i < 30 + particleCount * 2; i++) {
					double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult;
					double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
					double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult;

					//Using the random function each time ensures a different number for every value, making the ability "feel" better.
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
							look.y * mult + world.rand.nextGaussian() * accuracyMult,
							look.z * mult + world.rand.nextGaussian() * accuracyMult)
							.element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
							.clr(255, 15, 5).collide(true).scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
					//Using the random function each time ensures a different number for every value, making the ability "feel" better.
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
							look.y * mult + world.rand.nextGaussian() * accuracyMult,
							look.z * mult + world.rand.nextGaussian() * accuracyMult)
							.element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
							.clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true)
							.scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
				}
			}
		}

		if (hand == EnumHand.OFF_HAND)
			entity.swingArm(hand);

		if (!world.isRemote)
			setTimesUsed(entity.getPersistentID(), getTimesUsed(entity.getPersistentID()) + 1);

		if (ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
			ctx.getData().addStatusControl(hand == EnumHand.MAIN_HAND ? FLAME_STRIKE_OFF : FLAME_STRIKE_MAIN);

		return true;
	}
}
