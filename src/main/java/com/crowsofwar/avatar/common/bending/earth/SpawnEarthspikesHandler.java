package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class SpawnEarthspikesHandler extends TickHandler {
	private final ParticleSpawner particles;

	public SpawnEarthspikesHandler() {
		particles = new NetworkParticleSpawner();
	}
	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase owner = ctx.getBenderEntity();
		AbilityData abilityData = AbilityData.get(owner, "earthspike");
		BendingData data = ctx.getData();

		float frequency = STATS_CONFIG.earthspikeSettings.frequency;
		//4 (by default)
		double damage = STATS_CONFIG.earthspikeSettings.damage;
		//3 (by default)
		float size = STATS_CONFIG.earthspikeSettings.size;
		//1 (by default)


		if (abilityData.getLevel() == 1) {
			damage = STATS_CONFIG.earthspikeSettings.damage * 1.33;
			//4
			size = STATS_CONFIG.earthspikeSettings.size * 1.25F;
			//1.25
		}

		if (abilityData.getLevel() == 2) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.75F;
			//3
			damage = STATS_CONFIG.earthspikeSettings.damage * 1.66;
			//5
			size = STATS_CONFIG.earthspikeSettings.size * 1.5F;
			//1.5

		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency;
			//4
			damage = STATS_CONFIG.earthspikeSettings.damage;
			//1.5
			size = STATS_CONFIG.earthspikeSettings.size * 1.25F;
			//1.25
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.5F;
			//2
			damage = STATS_CONFIG.earthspikeSettings.damage * 2;
			//6
			size = STATS_CONFIG.earthspikeSettings.size * 2F;
			//2
		}

		//For some reason using *= or += seems to glitch out everything- that's why
		//I'm using tedious equations.

		size += data.getTickHandlerDuration(this) / 20;
		EntityEarthspikeSpawner entity = AvatarEntity.lookupControlledEntity(world, EntityEarthspikeSpawner.class, owner);

		if (!abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			if (entity != null) {
				if (data.getTickHandlerDuration(this) % frequency == 0 && data.getTickHandlerDuration(this) > frequency/3) {
					EntityEarthspike earthspike = new EntityEarthspike(world);
					earthspike.posX = entity.posX;
					earthspike.posY = entity.posY;
					earthspike.posZ = entity.posZ;
					if (!world.isRemote) {
						particles.spawnParticles(world, EnumParticleTypes.CRIT, 50, 60, Vector.getEntityPos(earthspike).plusY(0.1),
								new Vector(0.1, 0.9, 0.1));
					}
					earthspike.setAbility(abilityData.getAbility());
					earthspike.setDamage(damage);
					earthspike.setSize(size);
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);
				}
				return false;
			}
		} else {
			for (int degree = 0; degree < 360; degree++) {
				//Ring of instantaneous earthspikes

			}

		}
		return entity == null;
	}
}

