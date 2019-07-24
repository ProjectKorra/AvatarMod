package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import static com.crowsofwar.avatar.common.bending.StatusControl.*;

public class InfernoPunchParticleSpawner extends TickHandler {

	public InfernoPunchParticleSpawner(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = AbilityData.get(entity, "inferno_punch");

		int particleCount = 1;
		int level = abilityData.getLevel();
		if (level == 1) {
			particleCount = 2;

		}
		if (level == 2) {
			particleCount = 3;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			particleCount = 5;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			particleCount = 3;
		}
		if ((data.hasStatusControl(INFERNO_PUNCH_MAIN) || data.hasStatusControl(INFERNO_PUNCH_FIRST) || data.hasStatusControl(INFERNO_PUNCH_SECOND)) && !world.isRemote) {
			WorldServer World = (WorldServer) world;

			Vector pos = Vector.getRightSide(entity, 0.55).plus(0, 0.8, 0);
			Vector direction = Vector.getLookRectangular(entity);

			if (entity instanceof EntityPlayer && entity.getPrimaryHand() == EnumHandSide.LEFT) {
				pos = Vector.getLeftSide(entity, 0.55).plus(0, 1.8, 0);
			}
			Vector hand = pos.plus(direction.times(0.6));
			World.spawnParticle(AvatarParticles.getParticleFlames(),
					hand.x(), hand.y(), hand.z(), particleCount, 0, 0, 0, 0.015);

			return false;
		} else return true;
	}

}
