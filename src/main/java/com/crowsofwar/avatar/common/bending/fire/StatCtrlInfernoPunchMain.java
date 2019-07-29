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
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunchMain extends StatusControl {
	private ParticleSpawner particleSpawner;

	public StatCtrlInfernoPunchMain() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		particleSpawner = new NetworkParticleSpawner();
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

		if (abilityData.getLevel() == 1) {
			damage = STATS_CONFIG.InfernoPunchDamage * 4 / 3 * powerModifier;
			knockBack = 1.125F * powerModifier;
			fireTime = 6;
			performance += 5;
		}
		if (abilityData.getLevel() >= 2) {
			damage = STATS_CONFIG.InfernoPunchDamage * 5 / 3 * powerModifier;
			knockBack = 1.25F + powerModifier;
			fireTime = 8 + (int) (powerModifier * 10);
			performance = 20;
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
					null, startPos, startPos.add(entity.getLookVec().scale(5)), 1.0F, false, excluded);
			if (result != null) {
				if (result.entityHit != null) {
					Entity hit = result.entityHit;
					if (canCollideWith(entity)) {
						DamageUtils.attackEntity(entity, hit, AvatarDamageSource.causeInfernoPunchDamage(hit, entity), damage, performance);
						Vec3d direction = entity.getLookVec();
						double x = direction.x * knockBack;
						double y = direction.y * knockBack;
						double z = direction.z * knockBack;
						entity.setFire(fireTime);
						entity.addVelocity(x, y, z);
						AvatarUtils.afterVelocityAdded(hit);
						particleSpawner.spawnParticles(entity.world, AvatarParticles.getParticleFlames(), 10, 20,
								new Vector(result.hitVec.x, result.hitVec.y, result.hitVec.z), new Vector(1, 0.4, 1));
						return true;
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
