package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import static com.crowsofwar.avatar.common.bending.earth.SpawnEarthspikesHandler.SPAWN_EARTHSPIKES_HANDLER;
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
		BendingData data = ctx.getData();

		float ticks = 20;
		double speed = 10;
		float chi = STATS_CONFIG.chiEarthspike;
		//3.5 (by default)

		if (ctx.getLevel() >= 1) {
			ticks = 40;
			speed = 13;
			chi = STATS_CONFIG.chiEarthspike + 0.5F;
			//4
		}
		if (ctx.getLevel() >= 2) {
			speed = 16;
			chi = STATS_CONFIG.chiEarthspike + 2F;
			//5.5
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			ticks = 30;
			speed = 14;
			chi = STATS_CONFIG.chiEarthspike * 2.5F;
			//8.75
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			ticks = 60;
			speed = 22;
			chi = STATS_CONFIG.chiEarthspike * 2;
			//7
		}

		if (bender.consumeChi(chi)) {

			if (!abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {

				Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

				EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
				earthspike.setOwner(entity);
				earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
				earthspike.setVelocity(look.times(speed));
				earthspike.setDuration(ticks);
				earthspike.setAbility(this);
				earthspike.setUnstoppable(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
				world.spawnEntity(earthspike);

			} else {
				if (entity.onGround) {
					for (int i = 0; i < 8; i++) {
						Vector direction1 = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
								i * 45), 0).times(1.4).withY(0);
						EntityEarthspike earthspike = new EntityEarthspike(world);
						earthspike.setPosition(direction1.x() + entity.posX, entity.posY, direction1.z() + entity.posZ);
						earthspike.setDamage(STATS_CONFIG.earthspikeSettings.damage * 2.5);
						earthspike.setSize(STATS_CONFIG.earthspikeSettings.size * 1.25F);
						earthspike.setOwner(entity);
						earthspike.setAbility(this);
						world.spawnEntity(earthspike);
						//Ring of instantaneous earthspikes.
						if (!world.isRemote) {
							WorldServer World = (WorldServer) world;
							for (int degree = 0; degree < 360; degree++) {
								double radians = Math.toRadians(degree);
								double x = Math.cos(radians) / 2 + earthspike.posX;
								double y = earthspike.posY;
								double z = Math.sin(radians) / 2 + earthspike.posZ;
								World.spawnParticle(EnumParticleTypes.CRIT, x, y, z, 1, 0, 0, 0, 0.5);

							}
						}

					}
				}
			}
			data.addTickHandler(SPAWN_EARTHSPIKES_HANDLER);


		}
	}
}
