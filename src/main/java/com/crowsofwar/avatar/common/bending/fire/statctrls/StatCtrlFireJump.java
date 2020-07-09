package com.crowsofwar.avatar.common.bending.fire.statctrls;

import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.*;

public class StatCtrlFireJump extends StatusControl {
	public StatCtrlFireJump() {
		super(15, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		AbilityData abilityData = data.getAbilityData("fire_jump");
		boolean allowDoubleJump = abilityData.getLevel() == 3 && abilityData.getPath() == AbilityData.AbilityTreePath.SECOND;

		// Figure out whether entity is on ground by finding collisions with
		// ground - if found a collision box, then is not on ground
		List<AxisAlignedBB> collideWithGround = world.getCollisionBoxes(entity, entity.getEntityBoundingBox().grow(0.2, 1, 0.2));
		boolean onGround = !collideWithGround.isEmpty() || entity.collidedVertically;

		if (onGround || (allowDoubleJump && bender.consumeChi(STATS_CONFIG.chiFireJump))) {

			int lvl = abilityData.getLevel();
			double jumpMultiplier = 0.4;
			float fallAbsorption = 3;
			double range = 2;
			double speed = 1;
			float damage = 1;
			int numberOfParticles = 5;
			double particleSpeed = 0.1;
			if (lvl >= 1) {
				jumpMultiplier = 0.5;
				fallAbsorption = 4;
				range = 2.5;
				damage = 1.5F;
				speed = 1.5;
				numberOfParticles = 7;
				particleSpeed = 0.125;
			}
			if (lvl >= 2) {
				jumpMultiplier = 0.65;
				fallAbsorption = 5;
				speed = 2;
				range = 3;
				damage = 2;
				numberOfParticles = 10;
				particleSpeed = 0.15;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				jumpMultiplier = 0.8;
				fallAbsorption = 8;
				speed = 4;
				range = 5;
				damage = 3;
				numberOfParticles = 12;
				particleSpeed = 0.175;

			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				jumpMultiplier = 0.4;
				fallAbsorption = 15;
				speed = 2.5;
				range = 3;
				damage = 2.5F;
			}

			if (abilityData.getLevel() == 2 || abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				data.addTickHandler(SMASH_GROUND_FIRE);
			} else if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				data.addTickHandler(SMASH_GROUND_FIRE_BIG);
			}

			// Calculate direction to jump -- in the direction the player is currently already going

			// For some reason, velocity is 0 here when player is walking, so must instead
			// calculate using delta position
			Vector deltaPos = new Vector(entity.posX - entity.lastTickPosX, 0, entity.posZ - entity.lastTickPosZ);
			double currentYaw = Vector.getRotationTo(Vector.ZERO, deltaPos).y();

			// Just go forwards if not moving right now
			if (deltaPos.sqrMagnitude() <= 0.001) {
				currentYaw = Math.toRadians(entity.rotationYaw);
			}

			float pitch = entity.rotationPitch;
			if (pitch < -45) {
				pitch = -45;
			}

			Vector rotations = new Vector(Math.toRadians(pitch), currentYaw, 0);

			// Calculate velocity to move bender

			Vector velocity = rotations.toRectangular();

			velocity = velocity.withX(velocity.x() * 2);
			velocity = velocity.withZ(velocity.z() * 2);

			velocity = velocity.times(jumpMultiplier);
			entity.onGround = false;
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

			Block currentBlock = world.getBlockState(entity.getPosition()).getBlock();
			if (currentBlock == Blocks.AIR) {
				damageNearbyEntities(ctx, range, speed, damage, numberOfParticles, particleSpeed);
			}

			for (int angle = 0; angle < 360; angle += 2) {
				if (world.isRemote) {
					double radians = Math.toRadians(angle);
					double x = 0.25 * Math.cos(radians);
					double z = 0.25 * Math.sin(radians);
					ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(Vector.getEntityPos(entity).toMinecraft().add(x, 0, z))
							.vel(x * range / 2.5 * world.rand.nextDouble(), world.rand.nextGaussian() / 15, z * range / 2.5 * world.rand.nextDouble())
							.time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).scale((float) range / 6F).element(new Firebending())
							.spawnEntity(entity).collide(true).spawn(world);
				}
			}
			data.addTickHandler(FIRE_PARTICLE_SPAWNER);
			data.getMiscData().setFallAbsorption(fallAbsorption);

			abilityData.addXp(ConfigSkills.SKILLS_CONFIG.fireJump);

			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1, .7f);

			return true;

		}

		return false;

	}

	private void damageNearbyEntities(BendingContext ctx, double range, double speed, float damage, int numberOfParticles, double particleSpeed) {

		EntityLivingBase entity = ctx.getBenderEntity();

		World world = entity.world;
		AxisAlignedBB box = new AxisAlignedBB(entity.posX - range, entity.getEntityBoundingBox().minY, entity.posZ - range, entity.posX + range,
											  entity.posY + entity.getEyeHeight(), entity.posZ + range);

		List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase target : nearby) {
			if (target != entity && target.canBeCollidedWith() && target.canBePushed()) {
				if (canDamageEntity(target)) {
					target.attackEntityFrom(AvatarDamageSource.causeShockwaveDamage(target, entity, AvatarDamageSource.FIRE), damage);
				}
				BattlePerformanceScore.addMediumScore(entity);

				Vector velocity = Vector.getEntityPos(target).minus(Vector.getEntityPos(entity));
				double distance = Vector.getEntityPos(target).dist(Vector.getEntityPos(entity));
				double direction = (range - distance) * (speed / 2) / range;
				velocity = velocity.times(direction).withY(speed / 10);
				target.addVelocity(velocity.x(), velocity.y(), velocity.z());

				target.setFire(3);

			}
		}

	}

	private boolean canDamageEntity(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() != entity) {
			return false;
		}
		if (entity instanceof EntityHanging || entity instanceof EntityXPOrb || entity instanceof EntityItem || entity instanceof EntityArmorStand
						|| entity instanceof EntityAreaEffectCloud) {
			return false;
		} else return entity.canBeCollidedWith() && entity.canBePushed();
	}

}

