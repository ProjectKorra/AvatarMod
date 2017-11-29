package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class StatCtrlFireJump extends StatusControl {
	public StatCtrlFireJump() {
		super(0, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		AbilityData abilityData = data.getAbilityData("fire_jump");
		boolean allowDoubleJump = abilityData.getLevel() == 3
				&& abilityData.getPath() == AbilityData.AbilityTreePath.FIRST;

		// Figure out whether entity is on ground by finding collisions with
		// ground - if found a collision box, then is not on ground
		List<AxisAlignedBB> collideWithGround = world.getCollisionBoxes(entity,
				entity.getEntityBoundingBox().grow(0, 0.5, 0));
		boolean onGround = !collideWithGround.isEmpty();

		if (onGround || (allowDoubleJump && bender.consumeChi(STATS_CONFIG.chiAirJump))) {

			int lvl = abilityData.getLevel();
			double multiplier = 0.75;
			if (lvl >= 1) {
				multiplier = 1.1;
			}
			if (lvl >= 2) {
				multiplier = 1.3;
			}
			if (lvl >= 3) {
				multiplier = 1.5;
			}

			Vector rotations = new Vector(Math.toRadians((entity.rotationPitch) / 1),
					Math.toRadians(entity.rotationYaw), 0);

			Vector velocity = rotations.toRectangular();
			velocity = velocity.withY(Math.pow(velocity.y(), .1));
			velocity = velocity.times(multiplier);
			if (!onGround) {
				velocity = velocity.times(1);
				entity.motionX = 0;
				entity.motionY = 0;
				entity.motionZ = 0;
			}
			entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
			}

			ParticleSpawner spawner = new NetworkParticleSpawner();
			spawner.spawnParticles(entity.world, AvatarParticles.getParticleFlames(), 15, 20,
					new Vector(entity), new Vector(1, 0, 1));

			float fallAbsorption = 0;
			if (lvl == 0) {
				fallAbsorption = 6;
			} else if (lvl == 1) {
				fallAbsorption = 11;
			} else if (lvl == 2) {
				fallAbsorption = 14;
			} else if (lvl == 3) {
				fallAbsorption = 17;
			}

			data.getMiscData().setFallAbsorption(fallAbsorption);

			data.addTickHandler(TickHandler.FIRE_PARTICLE_SPAWNER);
			if (abilityData.getLevel() == 3 && abilityData.getPath() == AbilityData.AbilityTreePath.SECOND) {
				data.addTickHandler(TickHandler.SMASH_GROUND);
			}
			abilityData.addXp(STATS_CONFIG.chiAirJump);

			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_BLAZE_HURT,
					SoundCategory.PLAYERS, 1, .7f);

			return true;

		}

		return false;

	}

	/**
	 * Lights nearby entities on fire
	 */
	private void attackNearbyEntities(BendingContext ctx, double radius, double speed) {

		EntityLivingBase entity = ctx.getBenderEntity();
		AxisAlignedBB aabb = new AxisAlignedBB(
				entity.posX - radius, entity.posY - radius, entity.posZ - radius,
				entity.posX + radius, entity.posY + radius, entity.posZ + radius);

		List<EntityLivingBase> targets = ctx.getWorld().getEntitiesWithinAABB(EntityLivingBase
				.class, aabb);

		for (EntityLivingBase target : targets) {

			// Filter out targets that aren't in the desired radius
			// AABB does this sorta but it's a box shape instead of circle shape
			if (target.getDistanceSqToEntity(entity) > radius * radius) {
				continue;
			}

			target.setFire(3);

			Vector velocity = Vector.getEntityPos(target).minus(Vector.getEntityPos(entity));
			velocity = velocity.times(speed / 20);
			target.addVelocity(velocity.x(), velocity.y(), velocity.z());

		}

	}

}

