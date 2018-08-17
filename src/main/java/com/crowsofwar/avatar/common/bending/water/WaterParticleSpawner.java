package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class WaterParticleSpawner extends TickHandler {

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = AbilityData.get(entity, "water_cannon");
		int maxDuration = 40;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			maxDuration = 60;
		}
		int duration = data.getTickHandlerDuration(this);
		double radius =  ((float) maxDuration - duration) / 10;



		if (data.hasTickHandler(WATER_CHARGE) && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			for (int i = 0; i < 180; i++) {
				Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
						i * 2), 0).times(radius).withY(entity.getEyeHeight() / 2);
				World.spawnParticle(EnumParticleTypes.WATER_SPLASH, lookpos.x() + entity.posX, lookpos.y() + entity.getEntityBoundingBox().minY,
						lookpos.z() + entity.posZ, 1, 0, 0, 0, 0.05);
			}
			AxisAlignedBB box = new AxisAlignedBB(entity.posX + (1 * radius) , entity.posY + 2, entity.posZ + (1 * radius),
					entity.posX - (1 * radius), entity.posY - 2, entity.posZ - (1 * radius));
			List<EntityThrowable> projectiles = world.getEntitiesWithinAABB(EntityThrowable.class, box);
			if (!projectiles.isEmpty()) {
				for (Entity e : projectiles) {
					e.applyEntityCollision(e);
					e.setDead();
				}
			}
			if (abilityData.getLevel() >= 2) {
				List<EntityArrow> arrows = world.getEntitiesWithinAABB(EntityArrow.class, box);
				if (!arrows.isEmpty()) {
					for (Entity e : arrows) {
						e.applyEntityCollision(e);
						e.setDead();
					}
				}
			}
			return false;


		}
		else return true;
	}
}
