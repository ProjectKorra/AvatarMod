package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.damageutils.DamageUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunchMain extends StatusControl {
	//NOTICE: Abilities and status controls don't allow you to perform stuff client-side! Only tick handlers do that. Stupid, I know.

	public StatCtrlInfernoPunchMain() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		//	particleSpawner = new NetworkParticleSpawner();
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		HashSet<Entity> excluded = new HashSet<>();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");

		double reach = Raytrace.getReachDistance(entity);
		float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
		float xpMod = abilityData.getTotalXp() / 100;

		float damage = STATS_CONFIG.infernoPunchSettings.damage;
		int performance = STATS_CONFIG.infernoPunchSettings.performanceAmount;
		float knockBack = STATS_CONFIG.infernoPunchSettings.knockbackMult;
		int fireTime = STATS_CONFIG.infernoPunchSettings.fireTime;
		float xp = SKILLS_CONFIG.infernoPunchHit;

		if (abilityData.getLevel() == 1) {
			damage *= 4 / 3F;
			knockBack *= 1.125F;
			fireTime += 2;
			performance += 2;
			xp -= 1;
		}
		if (abilityData.getLevel() >= 2) {
			damage *= 6 / 3F;
			knockBack *= 1.25F;
			fireTime += 4;
			performance += 5;
			xp -= 2;
		}

		damage *= powerModifier * xpMod;
		knockBack *= powerModifier * xpMod;
		fireTime *= powerModifier * xpMod;
		performance *= powerModifier * xpMod;

		if (entity.isPotionActive(MobEffects.STRENGTH)) {
			damage += (Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
		}

		excluded.add(entity);
		Vec3d startPos = entity.getPositionVector().add(0, entity.getEyeHeight(), 0);

		if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
			//Bounding Box to determine excluded entities
			AxisAlignedBB detectionBox = new AxisAlignedBB(entity.posX + reach, entity.posY + reach, entity.posZ + reach, entity.posX - reach,
					entity.posY - reach, entity.posZ - reach);
			List<Entity> exclude = entity.world.getEntitiesWithinAABB(Entity.class, detectionBox);
			if (!exclude.isEmpty()) {
				for (Entity detected : exclude) {
					if (detected instanceof AvatarEntity) {
						if (((AvatarEntity) detected).getOwner() == entity) {
							excluded.add(detected);
						}
					}
					if (detected.getTeam() != null && detected.getTeam() == entity.getTeam()) {
						excluded.add(detected);
					}
					if (detected.getControllingPassenger() == entity) {
						excluded.add(detected);
					}
				}
				RayTraceResult result = Raytrace.standardEntityRayTrace(entity.world, entity,
						null, startPos, startPos.add(entity.getLookVec().scale(5)), 0.2F, false, excluded);
				if (result != null) {
					if (result.entityHit != null) {
						Entity hit = result.entityHit;
						if (canCollideWith(entity)) {
							Vec3d particlePos = hit.getPositionVector().add(0, hit.height / 2, 0);
							if (!entity.world.isRemote) {
								DamageUtils.attackEntity(entity, hit, AvatarDamageSource.causeInfernoPunchDamage(entity), damage,
										performance, new AbilityInfernoPunch(), xp);
								Vec3d direction = entity.getLookVec();
								double x = 0.5 * direction.x * knockBack;
								double y = 0.5 * direction.y * knockBack + 0.15;
								double z = 0.5 * direction.z * knockBack;
								hit.setFire(fireTime);
								hit.addVelocity(x, y, z);
								AvatarUtils.afterVelocityAdded(hit);
								if (entity.world instanceof WorldServer) {
									WorldServer World = (WorldServer) entity.world;
									World.spawnParticle(AvatarParticles.getParticleFlames(), true, particlePos.x, particlePos.y, particlePos.z, 60,
											0, 0, 0, 0.02);
								}
							}
							hit.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F + performance / 30F, 0.8F + entity.world.rand.nextFloat() / 10);

							return true;
						}
					}

				}

			}
		}
		return false;
	}

	public boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityEnderCrystal) {
			return true;
		} else
			return (entity.canBePushed() && entity.canBeCollidedWith()) || entity instanceof EntityLivingBase;
	}
}
