/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending.air.statctrls;

import com.crowsofwar.avatar.common.bending.air.AbilityAirJump;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.air.powermods.AirJumpPowerModifier;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.crowsofwar.avatar.api.helper.GliderHelper.getIsGliderDeployed;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.AIR_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.data.TickHandlerController.SMASH_GROUND;

/**
 * @author CrowsOfWar
 */
public class StatCtrlAirJump extends StatusControl {

	public static Map<String, Integer> timesJumped = new HashMap<>();

	public StatCtrlAirJump() {
		super(0, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		String uuid = Objects.requireNonNull(bender.getInfo().getId()).toString();


		if (!timesJumped.containsKey(uuid)) timesJumped.put(uuid, 0);

		AbilityData abilityData = data.getAbilityData("air_jump");
		boolean allowDoubleJump = abilityData.getLevel() == 3 && abilityData.getPath() == AbilityTreePath.FIRST && timesJumped.get(uuid) < 2;


		int jumps = timesJumped.get(uuid);
		// Figure out whether entity is on ground by finding collisions with
		// ground - if found a collision box, then is not on ground
		List<AxisAlignedBB> collideWithGround = world.getCollisionBoxes(entity, entity.getEntityBoundingBox().grow(0.25, 0.625, 0.25));
		boolean onGround = !collideWithGround.isEmpty() || entity.collidedVertically || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WEB;

		if (onGround || entity instanceof EntityPlayer && getIsGliderDeployed((EntityPlayer) entity) || (allowDoubleJump && bender.consumeChi(STATS_CONFIG.chiAirJump))) {

			int lvl = abilityData.getLevel();
			double multiplier = 0.65;
			double powerModifier = 10;
			double powerDuration = 3;
			int numberOfParticles = 10;
			double particleSpeed = 0.2;
			if (lvl >= 1) {
				multiplier = 0.75;
				powerModifier = 15;
				powerDuration = 4;
				numberOfParticles = 15;
				particleSpeed = 0.3;
			}
			if (lvl >= 2) {
				multiplier = 0.875;
				powerModifier = 20;
				powerDuration = 5;
				numberOfParticles = 20;
				particleSpeed = 0.35;
			}
			if (lvl >= 3) {
				powerModifier = 25;
				powerDuration = 6;
			}
			if (abilityData.isMasterPath(AbilityTreePath.SECOND)) {
				numberOfParticles = 35;
				particleSpeed = 0.5;
				multiplier = 1.2;
			}
			if (abilityData.isMasterPath(AbilityTreePath.FIRST))
				multiplier = 1.0F;

			if (world.isRemote) {
				for (int i = 0; i < numberOfParticles; i++)
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).collide(true).pos(AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, 0.1, 0))
							.clr(0.8F, 0.8F, 0.8F).time(14).vel(world.rand.nextGaussian() * particleSpeed / 10, world.rand.nextGaussian() * particleSpeed / 20,
							world.rand.nextGaussian() * particleSpeed / 10).scale(1F + (float) particleSpeed).spawn(world);
			}

			Vector velocity = Vector.getLookRectangular(entity);
			velocity = velocity.times(multiplier * 1.25);
			velocity = velocity.withY(Math.max(velocity.y(), 0.15));
			if (!onGround) {
				velocity = velocity.times(0.875);
				entity.setVelocity(velocity.x(), velocity.y(), velocity.z());
			} else
				entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
			AvatarUtils.afterVelocityAdded(entity);

			float fallAbsorption = 0;
			float xVel = 0, yVel = 0, zVel = 0;
			if (lvl <= 0) {
				fallAbsorption = 8;
				xVel = zVel = 0.4F;
				yVel = 1F;
			} else if (lvl == 1) {
				fallAbsorption = 13;
				xVel = zVel = 0.6F;
				yVel = 1.4F;
			} else if (lvl == 2) {
				fallAbsorption = 16;
				xVel = zVel = 0.8F;
				yVel = 1.8F;
			} else if (lvl == 3) {
				fallAbsorption = 19;
				xVel = zVel = 1F;
				yVel = 2.0F;
			}

			data.getMiscData().setFallAbsorption(fallAbsorption);

			data.addTickHandler(AIR_PARTICLE_SPAWNER);
			if (abilityData.getLevel() == 3 && abilityData.getPath() == AbilityTreePath.SECOND) {
				data.addTickHandler(SMASH_GROUND);
			}

			abilityData.addXp(SKILLS_CONFIG.airJump);

			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 1, .7f);

			PowerRatingModifier powerRatingModifier = new AirJumpPowerModifier(powerModifier);
			powerRatingModifier.setTicks((int) (powerDuration * 20));
			//noinspection ConstantConditions
			data.getPowerRatingManager(Airbending.ID).addModifier(powerRatingModifier, ctx);


			EntityShockwave wave = new EntityShockwave(world);
			wave.setDamage(0);
			wave.setFireTime(0);
			wave.setRange(lvl > 0 ? 2.25F + lvl / 4F : 2.25F);
			wave.setSpeed(lvl > 0 ? 0.5F + lvl / 30F : 0.5f);
			wave.setKnockbackHeight(lvl > 0 ? 0.0125F + lvl / 80F : 0.0125F);
			wave.setParticle(EnumParticleTypes.EXPLOSION_NORMAL);
			wave.setPerformanceAmount(10);
			wave.setKnockbackMult(new Vec3d(0.5, 0.2, 0.5));
			wave.setParticleAmount(2);
			wave.setDamageSource(AvatarDamageSource.AIR);
			wave.setAbility(new AbilityAirJump());
			wave.setElement(new Airbending());
			wave.setParticleSpeed(lvl > 0 ? 0.02F + lvl / 40F : 0.02F);
			wave.setPosition(entity.getPositionVector().add(0, 0.5, 0));
			wave.setOwner(entity);
			wave.setKnockbackMult(new Vec3d(xVel, yVel, zVel));
			if (!world.isRemote) {
				world.spawnEntity(wave);
				jumps++;
			}
			timesJumped.replace(uuid, jumps);
			//If you return when it's greater than 1, it resets, and you can double jump infinitely.
			boolean isDone = jumps > 2;
			if (isDone) timesJumped.replace(uuid, 0);
			return true;

		}

		return false;

	}

}
