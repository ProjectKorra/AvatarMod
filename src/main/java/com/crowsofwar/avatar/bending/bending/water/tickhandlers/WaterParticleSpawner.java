package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
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

import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_CHARGE;

public class WaterParticleSpawner extends TickHandler {

	public WaterParticleSpawner(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = AbilityData.get(entity, "water_blast");
		int maxDuration = 40;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			maxDuration = 60;
		}
		int duration = data.getTickHandlerDuration(this);
		double radius = ((float) maxDuration - duration) / 10;

		if (data.hasTickHandler(WATER_CHARGE) && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			for (int j = 1; j < 4; j++) {
				for (int i = 0; i < 90; i++) {
					double rScale = (radius / 90) * i;
					Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw + j * (i * 4)), 0).times(rScale).withY(entity.getEyeHeight() / 2);
					World.spawnParticle(EnumParticleTypes.WATER_SPLASH, lookpos.x() + entity.posX, lookpos.y() + entity.getEntityBoundingBox().minY,
							lookpos.z() + entity.posZ, 1, 0, 0, 0, 0.05);
				}
			}
			AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + entity.getEyeHeight() / 2 + radius / 4, entity.posZ + radius,
					entity.posX - radius, entity.posY + entity.getEyeHeight() / 2 - radius / 4, entity.posZ - radius);
			List<EntityThrowable> projectiles = world.getEntitiesWithinAABB(EntityThrowable.class, box);
			if (!projectiles.isEmpty()) {
				for (Entity e : projectiles) {
					Vector vel = Vector.getVelocity(e).times(-1);
					e.addVelocity(vel.x(), 0, vel.z());
				}
			}
			if (abilityData.getLevel() >= 2) {
				List<Entity> bolts = world.getEntitiesWithinAABB(Entity.class, box);
				if (!bolts.isEmpty()) {
					for (Entity e : bolts) {
						if (e instanceof EntityArrow) {
							Vector vel = Vector.getVelocity(e).times(-1);
							e.addVelocity(vel.x(), 0, vel.z());
						}
						if (e instanceof AvatarEntity && ((AvatarEntity) e).isProjectile()) {
							((AvatarEntity) e).onMajorWaterContact();
							((AvatarEntity) e).setVelocity(((AvatarEntity) e).velocity().times(0.5F));
						}
					}
				}
			}
			return false;

		} else return true;
	}
}
