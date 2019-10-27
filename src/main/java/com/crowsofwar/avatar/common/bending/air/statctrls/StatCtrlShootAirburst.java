package com.crowsofwar.avatar.common.bending.air.statctrls;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.damageutils.DamageUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.AIRBURST_CHARGE_HANDLER;

public class StatCtrlShootAirburst extends StatusControl {

	private final int charge;

	public StatCtrlShootAirburst(int charge) {
		super(17, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
		this.charge = charge;
	}

	@Override
	public boolean execute(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(new AbilityAirBurst().getName());

		//Ensures that particles are spawned while properly removing the status control
		boolean spawnedParticles = false;

		float distance = STATS_CONFIG.airBurstSettings.beamRange;
		float damage = STATS_CONFIG.airBurstSettings.beamDamage;
		float knockback = STATS_CONFIG.airBurstSettings.beamPush;
		float size = STATS_CONFIG.airBurstSettings.beamSize;
		boolean piercing = false;

		if (data.hasTickHandler(AIRBURST_CHARGE_HANDLER)) {

			switch (abilityData.getLevel()) {
				case -1:
				case 0:
					break;
				case 1:
					damage += 2;
					distance += 3;
					knockback += 1;
					size += 0.5;
					break;
				case 2:
					damage += 6;
					distance += 6;
					knockback += 2;
					size += 1;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage += 10;
				knockback += 3;
				distance += 20;
				piercing = true;
				//Long, piercing damage beam.

			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage += 2;
				knockback += 2;
				piercing = true;
				size += 2.5;

				//Shorter, bigger beam with a path that charges faster.

			}

			if (charge == 4)
				piercing = true;

			damage *= (0.5 + 12.5 * charge);
			size *= (0.5 + 12.5 * charge);
			knockback *= (0.5 + 12.5 * charge);
			distance *= (0.8 + 0.05 * charge);

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND))
			if (!world.isRemote) {
				Vector start = Vector.getEyePos(entity);
				Vector direction = Vector.getLookRectangular(entity);
				RayTraceResult result;
				HashSet<Entity> excluded = new HashSet<>();

				excluded.add(entity);

				if (piercing) {
					List<Entity> targets = Raytrace.entityRaytrace(world, start, direction, distance, size);
					if (!targets.isEmpty()) {
						for (Entity nearby : targets) {
							if (nearby != entity) {
								if (nearby instanceof AvatarEntity && ((AvatarEntity) nearby).getOwner() != entity || (entity.getTeam() != null &&
										entity.getTeam() != nearby.getTeam()) && entity instanceof EntityLivingBase || entity.canBeCollidedWith() && entity.canBePushed()) {
									//DamageUtils.attackEntity(entity, nearby, AvatarDamageSource.causeAirDamage(nearby, entity), damage, );
								}
						}
					}
				}
				}
				else {
					result = Raytrace.standardEntityRayTrace(world, entity, null, start.toMinecraft(),
							start.plus(direction.times(distance)).toMinecraft(), size, false, excluded);

				}
			}

			if (world.isRemote) {
				//3 parts. The initial expansion into a cylinder, the circles flying away, and the trailing part.
			}
		}
		//world.playSound();
		return spawnedParticles;
	}
}
