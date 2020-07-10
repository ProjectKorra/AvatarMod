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

package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.blocks.BlockTemp;
import com.crowsofwar.avatar.common.blocks.BlockUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireShot extends Ability {

	//TODO: Use a new entity for this ability, spawn particles along it.

	public AbilityFireShot() {
		super(Firebending.ID, "fire_shot");
		requireRaytrace(-1, false);
	}


	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		AbilityData abilityData = ctx.getAbilityData();

		float speed = 0.5F;
		double damageMult = bender.getDamageMult(Firebending.ID);
		float damage = STATS_CONFIG.fireShotSetttings.damage;
		float chi = STATS_CONFIG.chiFireShot;
		float xp = SKILLS_CONFIG.fireShotHit;
		if (ctx.getLevel() == 1) {
			speed += 0.25F;
			chi += 0.5F;
			damage += 2;
		}
		if (ctx.getLevel() == 2) {
			speed += 0.5F;
			chi += 1;
			damage += 4;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			speed += 0.75F;
			chi += 1.5F;
			damage += 7;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi += 2F;
		}
		damage += abilityData.getTotalXp() / 50;

		if (bender.consumeChi(chi)) {
			if (!ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				Vector pos = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(0.05));
				EntityFlames flames = new EntityFlames(world);
				flames.setPosition(Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(0.05)));
				flames.setOwner(entity);
				flames.setEntitySize(0.1F, 0.1F);
				flames.setReflect(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				flames.setAbility(this);
				flames.setTier(getCurrentTier(ctx));
				flames.setXp(xp);
				flames.setVelocity(entity.getLookVec().scale(speed));
				flames.setLifeTime((int) abilityData.getTotalXp() + 60);
				flames.setTrailingFire(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				flames.setFireTime((int) (4F * 1 + abilityData.getTotalXp() / 50f));
				flames.setDamage(damage * (float) damageMult);
				flames.setElement(new Firebending());
				flames.setPowerRating(10);
				world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.75F +
						world.rand.nextFloat(), 0.5F + world.rand.nextFloat(), false);
				pos = pos.minus(Vector.getLookRectangular(entity).times(0.05));
				if (!world.isRemote)
					world.spawnEntity(flames);
				if (world.isRemote) {
					for (double angle = 0; angle < 360; angle += 8) {
						Vector position = Vector.getOrthogonalVector(entity.getLookVec(), angle, 0.01f);
						Vector velocity;
						//position = position.plus(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20);
						position = position.plus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity).times(0.85)));
						velocity = position.minus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity).times(0.85))).normalize();
						velocity = velocity.times(speed / 15);
						double spawnX = position.x();
						double spawnY = position.y();
						double spawnZ = position.z();
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
								world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
								.time(8 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(1F, 10 / 255F, 5 / 255F, 0.75F).spawnEntity(entity)
								.scale(0.125F).element(new Firebending()).collide(true).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
								world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
								.time(12 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(1F, (40 + AvatarUtils.getRandomNumberInRange(0, 60)) / 255F,
								10 / 255F, 0.75F).spawnEntity(entity)
								.scale(0.125F).element(new Firebending()).collide(true).spawn(world);
					}
				}
			} else {
				EntityShockwave wave = new EntityShockwave(world);
				wave.setOwner(entity);
				wave.rotationPitch = entity.rotationPitch;
				wave.rotationYaw = entity.rotationYaw;
				wave.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0));
				wave.setFireTime(10);
				wave.setElement(new Firebending());
				wave.setAbility(this);
				wave.setParticle(AvatarParticles.getParticleFlames());
				wave.setDamage(5F);
				wave.setPerformanceAmount(15);
				wave.setBehaviour(new FireShockwaveBehaviour());
				wave.setSpeed(0.4F);
				wave.setKnockbackMult(new Vec3d(1.5, 1, 1.5));
				wave.setKnockbackHeight(0.15);
				wave.setParticleSpeed(0.18F);
				wave.setParticleWaves(2);
				wave.setParticleAmount(10);
				world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.75F +
						world.rand.nextFloat(), 0.5F + world.rand.nextFloat(), false);
				if (!world.isRemote)
					world.spawnEntity(wave);
			}
		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireShot(this, entity, bender);
	}

	public static class FireShockwaveBehaviour extends OffensiveBehaviour {

		@Override
		public Behavior onUpdate(EntityOffensive entity) {
			if (entity.getOwner() != null) {
				if (entity instanceof EntityShockwave) {
				for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (entity.ticksExisted * 3)) {
					int x = entity.posX < 0 ? (int) (entity.posX + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed())) * Math.sin(angle) - 1)
							: (int) (entity.posX + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle)));
					int z = entity.posZ < 0 ? (int) (entity.posZ + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle) - 1))
							: (int) (entity.posZ + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed())) * Math.cos(angle));

					BlockPos spawnPos = new BlockPos(x, (int) (entity.posY), z);
					if (BlockUtils.canPlaceFireAt(entity.world, spawnPos)) {
						if (spawnPos != entity.getPosition()) {
							int time = entity.ticksExisted * ((EntityShockwave) entity).getSpeed() >= ((EntityShockwave) entity).getRange() - 0.2 ? 120 : 10;
							BlockTemp.createTempBlock(entity.world, spawnPos, time, Blocks.FIRE.getDefaultState());
						}
					}
				}

				}
			}
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {

		}

		@Override
		public void toBytes(PacketBuffer buf) {

		}

		@Override
		public void load(NBTTagCompound nbt) {

		}

		@Override
		public void save(NBTTagCompound nbt) {

		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}
}
