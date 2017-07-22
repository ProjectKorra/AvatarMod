package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntitySandPrison;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author CrowsOfWar
 */
public class AbilitySandPrison extends Ability {

	public static final UUID ID = UUID.fromString("b7066c8b-e46b-4f0f-ade8-3654ee51decb");

	public AbilitySandPrison() {
		super(Sandbending.ID, "sand_prison");
	}

	@Override
	public void execute(AbilityContext ctx) {

		if (ctx.consumeChi(ConfigStats.STATS_CONFIG.chiPrison)) {

			EntityLivingBase caster = ctx.getBenderEntity();
			World world = ctx.getWorld();
			Vector start = Vector.getEyePos(caster);
			Vector direction = Vector.getLookRectangular(caster);

			Predicate<Entity> filter = entity -> entity != caster && entity instanceof EntityLivingBase;
			List<Entity> hit = Raytrace.entityRaytrace(world, start, direction, 10, filter);

			if (!hit.isEmpty()) {
				EntityLivingBase prisoner = (EntityLivingBase) hit.get(0);
				EntitySandPrison.imprison(prisoner);

				world.playSound(null, prisoner.posX, prisoner.posY, prisoner.posZ,
						SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 2, 2);

				world.playSound(null, prisoner.posX, prisoner.posY, prisoner.posZ,
						SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1, 1);

			}

		}

	}

	@Override
	public UUID getId() {
		return ID;
	}
}
