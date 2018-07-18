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

		if (entity != null) {
			EntityEarthspike earthspike = new EntityEarthspike(world);
			return false;
		}
		else return true;
	}
}
