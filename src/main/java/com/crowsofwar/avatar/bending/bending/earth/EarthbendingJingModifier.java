package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * Power rating modifier for earthbending which increases power rating if the earthbender has
 * higher health than nearby opponents.
 */
public class EarthbendingJingModifier extends PowerRatingModifier {

	/**
	 * The value of the modifier the last time it was calculated. Since calculating this modifier
	 * is a bit expensive, when it has been calculated recently, it just uses that old value in
	 * the cache instead of re-calculating again.
	 */
	private float cachedValue;

	/**
	 * The "timestamp" of the last time this modifier was calculated, is actually the entity's
	 * ticksExisted
	 */
	private float cachedTime = -1;

	@Override
	public double get(BendingContext ctx) {

		float currentTime = ctx.getBenderEntity().ticksExisted;

		if (currentTime - cachedTime > 40 || cachedTime == -1) {
			cachedTime = currentTime;
			cachedValue = calculateValue(ctx);
		}

		return cachedValue;

	}

	private float calculateValue(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();

		float userHealth = entity.getHealth() / entity.getMaxHealth();
		float opponentHealth = getNearbyHealth(ctx);

		// No opponents nearby
		if (opponentHealth == 0) {
			return 0;
		}
		// Consider full health in creative mdoe
		if (ctx.getBender().isCreativeMode()) {
			userHealth = 1;
		}

		float diff = userHealth - opponentHealth;
		return diff * 50;

	}

	/**
	 * Looks around for other benders/mobs and finds their health percentage. Gets the average of
	 * the nearby opponents' health (0-1).
	 * <p>
	 * This is used to simulate "jing" or whether it is a good opportunity for the
	 * earthbender to strike. Low health of nearby opponents means it is a good time for the
	 * earthbender to attack.
	 */
	private float getNearbyHealth(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		int size = 10;

		AxisAlignedBB aabb = new AxisAlignedBB(
				entity.posX - size, entity.posY - size, entity.posZ - size,
				entity.posX + size, entity.posY + size, entity.posZ + size);

		List<EntityLivingBase> opponents = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
				candidate -> candidate != ctx.getBenderEntity() && (Bender.isBenderSupported
						(candidate) || candidate instanceof EntityMob));

		float average = 0;
		for (EntityLivingBase opponent : opponents) {

			// Ignore players on creative mode
			if (Bender.isBenderSupported(opponent)) {
				Bender bender = Bender.get(opponent);
				if (bender.isCreativeMode()) {
					continue;
				}
			}

			float healthPercent = opponent.getHealth() / opponent.getMaxHealth();
			average += healthPercent / opponents.size();
		}

		return average;

	}

}
