package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class ImmolatePowerModifier extends BuffPowerModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData("immolate");

		double modifier = 20;
		if (abilityData.getLevel() >= 1) {
			modifier = 25;
		}
		if (abilityData.getLevel() == 3) {
			modifier = 40;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			modifier = 60;
		}
		return modifier;

	}

	@Override
	public boolean onUpdate(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		AbilityData abilityData = AbilityData.get(entity, "immolate");

		// Intermittently light on fire
		if (entity.ticksExisted % 20 == 0) {
			double chance = 0.3;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				chance = 0.6;
			}

			// 30% chance per second to be lit on fire
			if (Math.random() < chance && !abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.setFire(2);
			}
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			AxisAlignedBB box = new AxisAlignedBB(entity.posX - 2, entity.posY, entity.posZ - 2, entity.posX + 2, entity.posY + 3, entity.posZ + 2);
			List<Entity> targets = entity.world.getEntitiesWithinAABB(Entity.class, box);
			if (!entity.world.isRemote) {
				if (!targets.isEmpty()) {
					for (Entity e : targets) {
						if (e != entity || (e instanceof AvatarEntity && ((AvatarEntity) e).getOwner() != e)) {
							e.setFire(5);
							if (e instanceof EntityThrowable || e instanceof EntityItem) {
								e.setFire(1);
								e.setDead();
							}
							if (e instanceof EntityArrow) {
								e.setFire(1);
								e.setDead();
							}
						}
					}
				}
			}
		}

		return super.onUpdate(ctx);
	}

	@Override
	protected Vision[] getVisions() {
		return new Vision[]{Vision.IMMOLATE_WEAK, Vision.IMMOLATE_MEDIUM, Vision.IMMOLATE_POWERFUL};
	}

	@Override
	protected String getAbilityName() {
		return new AbilityImmolate().getName();
	}

}

