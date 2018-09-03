package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class InfernoPunchParticleSpawner extends TickHandler {
	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = AbilityData.get(entity, "inferno_punch");

		int particleCount = 5;
		if (abilityData.getLevel() == 1) {
			particleCount = 8;
		}
		if (abilityData.getLevel() == 2) {
			particleCount = 10;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			particleCount = 20;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			particleCount = 15;
		}
		if (data.hasStatusControl(StatusControl.INFERNO_PUNCH) && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			if (data.getTickHandlerDuration(this) % 2 == 0) { ;
				Vector pos = AvatarUtils.getRightSide(entity, 0.55).plus(0, 1.2,0);
				Vector direction = Vector.getLookRectangular(entity);

				if (entity instanceof EntityPlayer &&  entity.getPrimaryHand() == EnumHandSide.LEFT) {
					pos = AvatarUtils.getLeftSide(entity, 0.55).plus(0, 3, 0);
				}
				Vector hand = pos.plus(direction.times(0.1));
				World.spawnParticle(EnumParticleTypes.FLAME, hand.x(), hand.y(), hand.z(), particleCount, 0, 0, 0, 0.015);

			}

			return false;
		} else return true;
	}
}
