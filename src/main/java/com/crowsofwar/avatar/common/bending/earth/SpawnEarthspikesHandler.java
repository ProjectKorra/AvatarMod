package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SpawnEarthspikesHandler extends TickHandler {
	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase owner = ctx.getBenderEntity();
		AbilityData abilityData = AbilityData.get(owner, "earthspike");
		EntityEarthspikeSpawner entity = AvatarEntity.lookupControlledEntity(world, EntityEarthspikeSpawner.class, owner);

		float frequency = 8;

		if (abilityData.getLevel() == 1) {
			frequency = 6;
		}

		if (abilityData.getLevel() == 2) {
			frequency = 4;
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			frequency = 5;
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			frequency = 2;
		}

		if (entity != null) {
			if (entity.ticksExisted % frequency == 0) {
				EntityEarthspike earthspike = new EntityEarthspike(world);
				earthspike.posX = entity.posX;
				earthspike.posY = entity.posY;
				earthspike.posZ = entity.posZ;

			}
			return false;
		} else return true;
	}
}
