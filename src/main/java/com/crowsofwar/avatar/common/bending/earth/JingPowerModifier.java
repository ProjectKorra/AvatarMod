package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * Power rating modifier for earthbending which increases power rating if the earthbender has
 * higher health than nearby opponents.
 */
public class JingPowerModifier extends PowerRatingModifier {

	/**
	 * The value of the modifier the last time it was calculated. Since calculating this modifier
	 * is a bit expensive, when it has been calculated recently, it just uses that old value in
	 * the cache instead of re-calculating again.
	 */
	float cachedValue;

	/**
	 * The "timestamp" of the last time this modifier was calculated, is actually the entity's
	 * ticksExisted
	 */
	float cachedTime;

	@Override
	public double get(BendingContext ctx) {



		return 0;

	}

	/**
	 * Looks around for other benders and finds their health percentage. Gets the average of the
	 * nearby benders' health (0-1).
	 * <p>
	 * This is used to simulate "jing" or whether it is a good opportunity for the
	 * earthbender to strike. Low health of nearby benders means it is a good time for the
	 * earthbender to attack.
	 */
	private float getNearbyHealth(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		int size = 10;

		AxisAlignedBB aabb = new AxisAlignedBB(
				entity.posX - size, entity.posY - size, entity.posZ - size,
				entity.posX + size, entity.posY + size, entity.posZ + size);

		List<EntityLivingBase> benders = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
				candidate -> candidate != ctx.getBenderEntity() && Bender.isBenderSupported
						(candidate));

		float average = 0;
		for (EntityLivingBase benderEntity : benders) {
			float healthPercent = benderEntity.getHealth() / benderEntity.getMaxHealth();
			average += healthPercent / benders.size();
		}

		return average;

	}


}
