package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.PlayerViewRegistry;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
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

			Vec3d height, rightSide;
			if (entity instanceof EntityPlayer) {
				if (PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) <= -1) {
					height = entity.getPositionVector().add(0, 1.6, 0);
					height = height.add(entity.getLookVec().scale(0.8));
					//Right
					if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
						rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
						rightSide = rightSide.add(height);
					}
					//Left
					else {
						rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
						rightSide = rightSide.add(height);
					}
				} else {
					height = entity.getPositionVector().add(0, 0.84, 0);
					if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
						rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.385).withY(0).toMinecraft();
						rightSide = rightSide.add(height);
					} else {
						rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.385).withY(0).toMinecraft();
						rightSide = rightSide.add(height);
					}
				}
			} else {
				height = entity.getPositionVector().add(0, 0.84, 0);
				if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.385).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				} else {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.385).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				}

			}
			World.spawnParticle(AvatarParticles.getParticleFlames(),
					rightSide.x, rightSide.y, rightSide.z, particleCount, 0, 0, 0, 0.015);
			return false;
		} else return true;
	}

}
