package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntitySandPrison;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilitySandPrison extends Ability {

	public AbilitySandPrison() {
		super(Sandbending.ID, "sand_prison");
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();

		if (bender.consumeChi(ConfigStats.STATS_CONFIG.chiSandPrison)) {

			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			Vector start = Vector.getEyePos(entity);
			Vector direction = Vector.getLookRectangular(entity);

			Predicate<Entity> filter = candidate -> candidate != entity && candidate instanceof
					EntityLivingBase;
			List<Entity> hit = Raytrace.entityRaytrace(world, start, direction, 10, filter);

			if (!hit.isEmpty()) {

				boolean allowEarthbendable = ctx.getAbilityData().getLevel() >= 1;

				EntityLivingBase prisoner = (EntityLivingBase) hit.get(0);
				if (canImprison(prisoner, allowEarthbendable)) {
					EntitySandPrison.imprison(prisoner, entity, this);
					world.playSound(null, prisoner.getPosition(), SoundEvents.BLOCK_SAND_STEP,
							SoundCategory.PLAYERS, 1, 1);

					ctx.getAbilityData().addXp(SKILLS_CONFIG.sandPrisoned);

				} else {
					String suffix = allowEarthbendable ? "bendable" : "sand";
					bender.sendMessage("avatar.sandPrisonDisabled." + suffix);
				}
			}

		}

	}

	/**
	 * @param allowEarthbendable Whether to allow all earthbendable blocks (grass, dirt, etc) to
	 *                           be used. If false, only sand can be used.
	 */
	private boolean canImprison(EntityLivingBase target, boolean allowEarthbendable) {
		BlockPos pos = target.getPosition().down();
		World world = target.world;
		Block standingOn = world.getBlockState(pos).getBlock();

		List<Block> blocksList;
		if (allowEarthbendable) {
			blocksList = ConfigStats.STATS_CONFIG.bendableBlocks;
		} else {
			blocksList = ConfigStats.STATS_CONFIG.sandBlocks;
		}

		return blocksList.contains(standingOn);

	}


	@Override
	public int getBaseParentTier() {
		return 3;
	}
}
