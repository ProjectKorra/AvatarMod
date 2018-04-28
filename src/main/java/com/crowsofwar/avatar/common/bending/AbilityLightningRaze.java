package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.common.entity.EntityLightningSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityLightningRaze extends Ability {
	public AbilityLightningRaze() {
	super(Lightningbending.ID, "lightning_raze");
}

	@Override
	public void execute(AbilityContext ctx) {

		AbilityData abilityData = ctx.getAbilityData();
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();


		float xp = abilityData.getTotalXp();
		float ticks = 20;
		double speed = 8;
		float chi = STATS_CONFIG.chiEarthspike;

		if (ctx.getLevel() >= 1) {
			ticks = 40;
		}
		if (ctx.getLevel() >= 2) {
			speed = 14;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {

			ticks = 40;
			speed = 40;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {

			ticks = 60;
			speed = 5;
		}

		if (bender.consumeChi(chi)) {

				Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

				EntityLightningSpawner boltSpawner = new EntityLightningSpawner(world);
				boltSpawner.setOwner(entity);
				boltSpawner.setPosition(entity.posX, entity.posY, entity.posZ);
				boltSpawner.setVelocity(look.times(speed));
				boltSpawner.setDuration(ticks);
				world.spawnEntity(boltSpawner);

			}

		}
	}

