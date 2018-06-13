package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityEarthspikes extends Ability {

	public AbilityEarthspikes() {
		super(Earthbending.ID, "earthspike");
	}

	@Override
	public void execute(AbilityContext ctx) {

		AbilityData abilityData = ctx.getAbilityData();
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();

		float damage = 1;
		float xp = abilityData.getTotalXp();
		float ticks = 20;
		double speed = 8;
		float chi = STATS_CONFIG.chiEarthspike;

		if (ctx.getLevel() >= 1) {
			damage = 1.5f;
			ticks = 40;
		}
		if (ctx.getLevel() >= 2) {
			speed = 14;
			damage = 2;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			damage = 1.25f;
			ticks = 30;
			speed = 12;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			damage = 3;
			ticks = 60;
			speed = 20;
		}

		if (bender.consumeChi(chi)) {

			if (!abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {

				Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

				EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
				earthspike.setOwner(entity);
				earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
				earthspike.setVelocity(look.times(speed));
				earthspike.setDamageMult((float) (damage * ctx.getPowerRatingDamageMod()));
				//For Earthspike's damagemult.
				earthspike.setDuration(ticks);
				earthspike.setUnstoppable(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
				world.spawnEntity(earthspike);

			} else {

				for (int i = 0; i < 8; i++) {

					Vector direction1 = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							i * 45), 0);
					Vector velocity = direction1.times(speed);

					EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
					earthspike.setVelocity(velocity);
					earthspike.setDuration(ticks);
					earthspike.setOwner(entity);
					earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
					earthspike.setDamageMult(damage + xp / 100);
					world.spawnEntity(earthspike);
				}
			}

		}
	}
}
