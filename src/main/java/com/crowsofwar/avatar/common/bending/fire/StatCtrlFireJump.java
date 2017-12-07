package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.config.ConfigSkills;
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

		if (onGround || (allowDoubleJump && bender.consumeChi(STATS_CONFIG.chiFireJump))) {

			int lvl = abilityData.getLevel();
			double jumpMultiplier = 0.2;
			if (lvl >= 1) {
				jumpMultiplier = 0.4;
			}
			if (lvl >= 2) {
				jumpMultiplier = 0.6;
			}
			if (lvl >= 3) {
				jumpMultiplier = 1.1;
			}

			// Calculate direction to jump -- in the direction the player is currently already going

			// For some reason, velocity is 0 here when player is walking, so must instead
			// calculate using delta position
			Vector deltaPos = new Vector(entity.posX - entity.lastTickPosX, 0, entity.posZ -
					entity.lastTickPosZ);
			double currentYaw = Vector.getRotationTo(Vector.ZERO, deltaPos).y();

			// Just go forwards if not moving right now
			if (deltaPos.sqrMagnitude() <= 0.001) {
				currentYaw = Math.toRadians(entity.rotationYaw);
			}

			Vector rotations = new Vector(Math.toRadians((entity.rotationPitch) / 1),
					currentYaw, 0);

			// Calculate velocity to move bender

			Vector velocity = rotations.toRectangular();

			velocity = velocity.withX(velocity.x() * 2);
			velocity = velocity.withZ(velocity.z() * 2);

			velocity = velocity.times(jumpMultiplier);
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
			data.addTickHandler(abilityData.getLevel() >= 2 ?
					TickHandler.SMASH_GROUND_FIRE_BIG : TickHandler.SMASH_GROUND_FIRE);

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damageNearbyEntities(ctx, 5, 3);
			}

			abilityData.addXp(ConfigSkills.SKILLS_CONFIG.fireJump);

			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_BLAZE_HURT,
					SoundCategory.PLAYERS, 1, .7f);

			return true;

		}

		return false;

	}

	private void damageNearbyEntities(BendingContext ctx, double range, double speed) {

		EntityLivingBase entity = ctx.getBenderEntity();

		World world = entity.world;
		AxisAlignedBB box = new AxisAlignedBB(entity.posX - range, entity.posY - range,
				entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range);

		List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase target : nearby) {
			if (target != entity) {
				target.attackEntityFrom(AvatarDamageSource.causeSmashDamage(target, entity), 5);

				Vector velocity = Vector.getEntityPos(target).minus(Vector.getEntityPos(entity));
				velocity = velocity.withY(1).times(speed / 20);
				target.addVelocity(velocity.x(), velocity.y(), velocity.z());

				target.setFire(3);

			}
		}

	}

}

