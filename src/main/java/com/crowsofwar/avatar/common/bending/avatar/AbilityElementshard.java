package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityElementshard;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.UUID;

public class AbilityElementshard extends Ability {

	public AbilityElementshard(UUID bendingType, String name) {
		super(Avatarbending.ID, "element_shard");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		float chi = 4F;
		int shardsleft = 5;
		if (ctx.getLevel() == 1 ){
			shardsleft = 8;
		}
		if (ctx.getLevel() == 2){
			shardsleft = 10;
		}
		if (bender.consumeChi(chi)){
			EntityElementshard elementshard = new EntityElementshard(world);
			for (int i = 0; i< shardsleft;){
				elementshard.posX = entity.posX;
				elementshard.posY = entity.posY;

			}
		}

	}
}
