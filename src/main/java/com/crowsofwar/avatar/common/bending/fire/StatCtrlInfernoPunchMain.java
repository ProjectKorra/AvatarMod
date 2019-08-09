package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.damageutils.DamageUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
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
	//private ParticleSpawner particleSpawner;

	public StatCtrlInfernoPunchMain() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		//	particleSpawner = new NetworkParticleSpawner();
	}

	@Override
	public boolean execute(BendingContext ctx) {
		//TODO: Raytrace instead of event
		EntityLivingBase entity = ctx.getBenderEntity();
		HashSet<Entity> excluded = new HashSet<>();
		double reach = Raytrace.getReachDistance(entity);
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		BendingData data = ctx.getData();
		float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
		float damage = STATS_CONFIG.InfernoPunchDamage * powerModifier;
		int performance = 15;
		float knockBack = 1 * powerModifier;
		int fireTime = 5 + (int) (powerModifier * 10);
		float xp = SKILLS_CONFIG.infernoPunchHit;

		if (abilityData.getLevel() == 1) {
			damage = STATS_CONFIG.InfernoPunchDamage * 4 / 3 * powerModifier;
			knockBack = 1.125F * powerModifier;
			fireTime = 6;
			performance += 5;
			xp -= 1;
		}
		if (abilityData.getLevel() >= 2) {
			damage = STATS_CONFIG.InfernoPunchDamage * 5 / 3 * powerModifier;
			knockBack = 1.25F + powerModifier;
			fireTime = 8 + (int) (powerModifier * 10);
			performance = 20;
			xp -= 2;
		}
		if (data.hasStatusControl(INFERNO_PUNCH_FIRST)) {
			damage = STATS_CONFIG.InfernoPunchDamage * 7 / 3 * powerModifier;
			knockBack = 1.5F + powerModifier;
			fireTime = 15 + (int) (powerModifier * 10);
			performance = 30;
		}

		if (entity.isPotionActive(MobEffects.STRENGTH)) {
			damage += (Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
		}

		excluded.add(entity);
		Vec3d startPos = entity.getPositionVector().add(0, entity.getEyeHeight(), 0);

		if (!entity.world.isRemote) {
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
							DamageUtils.attackEntity(entity, hit, AvatarDamageSource.causeInfernoPunchDamage(entity), damage,
									performance, new AbilityInfernoPunch(), xp);
							Vec3d direction = entity.getLookVec();
							double x = 0.5 * direction.x * knockBack;
							double y = 0.5 * direction.y * knockBack + 0.15;
							double z = 0.5 * direction.z * knockBack;
							hit.setFire(fireTime);
							hit.addVelocity(x, y, z);
							AvatarUtils.afterVelocityAdded(hit);
							Vec3d particlePos = hit.getPositionVector().add(0, hit.height / 2, 0);
							if (entity.world instanceof WorldServer) {
								WorldServer world = (WorldServer) entity.world;
								world.spawnParticle(AvatarParticles.getParticleFlames(), true, particlePos.x, particlePos.y, particlePos.z, 60,
										0, 0, 0, 0.02);
							}

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
