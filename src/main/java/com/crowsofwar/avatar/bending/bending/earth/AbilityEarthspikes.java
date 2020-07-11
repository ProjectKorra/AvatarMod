package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.EntityEarthspikeSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

public class AbilityEarthspikes extends Ability {

	public AbilityEarthspikes() {
		super(Earthbending.ID, "earth_spikes");
	}

	@Override
	public void execute(AbilityContext ctx) {

		AbilityData abilityData = ctx.getAbilityData();
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();

		float ticks = 20;
		double speed = 10;
		float chi = STATS_CONFIG.chiEarthspike;
		// 3.5 (by default)

		if (ctx.getLevel() >= 1) {
			ticks = 40;
			speed = 13;
			chi = STATS_CONFIG.chiEarthspike + 0.5F;
			// 4
		}
		if (ctx.getLevel() >= 2) {
			speed = 16;
			chi = STATS_CONFIG.chiEarthspike + 2F;
			// 5.5
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			ticks = 30;
			speed = 14;
			chi = STATS_CONFIG.chiEarthspike * 2.5F;
			// 8.75
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			ticks = 60;
			speed = 22;
			chi = STATS_CONFIG.chiEarthspike * 2;
			// 7
		}
		double damage = STATS_CONFIG.earthspikeSettings.damage * 2;
		// 6
		double size = STATS_CONFIG.earthspikeSettings.size * 1.25F;
		size += abilityData.getTotalXp() / 400;

		damage += abilityData.getTotalXp() / 400;
		damage *= ctx.getPowerRatingDamageMod();

		ticks += abilityData.getTotalXp() / 400;
		speed += abilityData.getTotalXp() / 400;
		chi -= abilityData.getTotalXp() / 400;

		if (bender.consumeChi(chi)) {

			if (!ctx.isDynamicMasterLevel(AbilityTreePath.FIRST)) {
				Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
				EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
				earthspike.setOwner(entity);
				earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
				earthspike.setVelocity(look.times(speed));
				earthspike.setDamage(damage);
				earthspike.setType(EntityEarthspikeSpawner.SpikesType.LINE);
				earthspike.setSize(size);
				earthspike.setDuration(ticks);
				earthspike.setAbility(this);
				earthspike.setUnstoppable(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND));
				world.spawnEntity(earthspike);
			} else if (entity.onGround) {
				EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
				earthspike.setOwner(entity);
				earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
				earthspike.setDamage(damage);
				earthspike.setType(EntityEarthspikeSpawner.SpikesType.OCTOPUS);
				earthspike.setSize(size);
				earthspike.setDuration(ticks);
				earthspike.setAbility(this);
				world.spawnEntity(earthspike);
			}
		}
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public int getBaseTier() {
		return 3;
	}
}
