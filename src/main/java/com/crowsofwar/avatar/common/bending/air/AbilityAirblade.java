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
package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;
import static java.lang.Math.abs;

/**
 * @author CrowsOfWar
 */
public class AbilityAirblade extends Ability {

	public AbilityAirblade() {
		super(Airbending.ID, "airblade");
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		if (!bender.consumeChi(STATS_CONFIG.chiAirblade)) return;



		AbilityData abilityData = ctx.getData().getAbilityData(this);
		float sizeMult = 1.0F;
		float damage = STATS_CONFIG.airbladeSettings.damage;
		damage *= abilityData.getXpModifier();
		damage *= ctx.getPowerRatingDamageMod();

		switch (ctx.getLevel()) {
			case -1:
			case 0:
				break;
			case 1:
				damage += 0.5F;
				sizeMult = 1.25F;
				break;
			case 2:
				damage += 1f;
				sizeMult = 1.5F;
				break;
		}
		if (ctx.isMasterLevel(SECOND)) {
			sizeMult = 4.0F;
			damage += 2F;
		}
		if (ctx.isMasterLevel(FIRST)) {
			damage += 2.5F;
			sizeMult = 1.25F;
		}

		float chopBlocks = -1;
		if (abilityData.getLevel() >= 1) {
			chopBlocks = 0;
		}
		if (ctx.isMasterLevel(SECOND)) {
			chopBlocks = 4;
		}

		Vector spawnAt = Vector.getEyePos(entity);

		if (ctx.isMasterLevel(FIRST)) {
			for (int i = 0; i < 5; i++) {
				float yaw = entity.rotationYaw - 30 + i * 15;
				Vector direction = Vector.toRectangular(Math.toRadians(yaw), Math.toRadians(entity.rotationPitch));
				EntityAirGust airblade = new EntityAirGust(world);
				airblade.setPosition(spawnAt.x(), spawnAt.y(), spawnAt.z());
				airblade.setAbility(new AbilityAirblade());
				airblade.setVelocity(direction.times(50));
				airblade.setDamage(damage);
				airblade.setElement(new Airbending());
				airblade.setEntitySize(sizeMult, 0.25F * sizeMult);
				airblade.rotationPitch = entity.rotationPitch;
				airblade.rotationYaw = yaw;
				airblade.setPiercesEnemies(true);
				airblade.setOwner(entity);
				airblade.setAbility(this);
				//airblade.setPierceArmor(true);
				//airblade.setChopBlocksThreshold(chopBlocks);
				airblade.setBehaviour(new AirBladeBehaviour());
				if (!world.isRemote)
					world.spawnEntity(airblade);
			}
		} else {
			double pitchDeg = entity.rotationPitch;
			if (abs(pitchDeg) > 30) {
				pitchDeg = pitchDeg / abs(pitchDeg) * 30;
			}
			float pitch = (float) Math.toRadians(pitchDeg);

			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), Math.toRadians(entity.rotationPitch));
			if (!world.isRemote)
				look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), Math.toRadians(entity.rotationPitch));
			EntityAirGust airblade = new EntityAirGust(world);
			airblade.setPosition(spawnAt.minusY(0.5));
			airblade.setVelocity(look.times(30));
			airblade.setDamage(damage);
			airblade.setTier(getCurrentTier(ctx));
			airblade.setXp(SKILLS_CONFIG.airBladeHit);
			if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				airblade.setEntitySize(0.5F, 0.5F);
				airblade.setExpandedHeight(sizeMult / 10);
				airblade.setExpandedWidth(sizeMult / 20);
				airblade.setLifeTime(40);
				airblade.setPiercesEnemies(true);
			}
			else {
				airblade.setEntitySize(sizeMult, 0.25F * sizeMult);
				airblade.setExpandedWidth(sizeMult / 10);
				airblade.setExpandedHeight(sizeMult / 20);
				airblade.setLifeTime(30);
			}
			airblade.rotationPitch = entity.rotationPitch;
			airblade.rotationYaw = entity.rotationYaw;
			airblade.setOwner(entity);
			airblade.setElement(new Airbending());
			airblade.setAbility(this);
			airblade.setBehaviour(new AirBladeBehaviour());
			airblade.setTier(getCurrentTier(ctx));
			if (!world.isRemote)
				world.spawnEntity(airblade);
		}
		super.execute(ctx);

	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirblade(this, entity, bender);
	}


	public static class AirBladeBehaviour extends OffensiveBehaviour {

		@Override
		public Behavior<EntityOffensive> onUpdate(EntityOffensive entity) {
			if (entity instanceof EntityAirGust && entity.getOwner() != null) {
				World world = entity.world;

				if (AbilityData.get(entity.getOwner(), entity.getAbility().getName()).isDynamicMasterLevel(SECOND)) {
					if (entity.ticksExisted > 8 && entity.ticksExisted < 25) {
						entity.motionX *= 0.75;
						entity.motionY *= 0.75;
						entity.motionZ *= 0.75;
					}
					if (entity.ticksExisted > 25) {
						entity.setVelocity(AvatarEntityUtils.getMiddleOfEntity(entity.getOwner()).subtract(AvatarEntityUtils.getMiddleOfEntity(entity)).scale(0.25));
					}
					if (world.isRemote) {
						for (double i = 0; i < 20; i += 1 / entity.getWidth()) {
							double spawnX = AvatarEntityUtils.getMiddleOfEntity(entity).x;
							double spawnY = AvatarEntityUtils.getMiddleOfEntity(entity).y;
							double spawnZ = AvatarEntityUtils.getMiddleOfEntity(entity).z;
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
									world.rand.nextGaussian() / 60).collide(true).time(2 + AvatarUtils.getRandomNumberInRange(0, 1)).clr(1F, 1F, 1F, 0.075F)
									.scale(entity.getAvgSize() / 4).element(entity.getElement()).spawnEntity(entity).spawn(world);
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfEntity(entity)).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
									world.rand.nextGaussian() / 60).collide(true).time(2 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(0.8F, 0.8F, 0.8F, 0.075F)
									.scale(entity.getAvgSize() / 2).element(entity.getElement()).spin(entity.getWidth() * 2, 0.1).spawnEntity(entity).spawn(world);
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfEntity(entity)).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
									world.rand.nextGaussian() / 60).collide(true).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(1F, 1F, 1F, 0.1F)
									.scale(entity.getAvgSize()).element(entity.getElement()).spin(entity.getWidth() * 2, 0.1).spawnEntity(entity).spawn(world);
						}

					}
				} else {
					if (world.isRemote) {
						for (double i = 0; i < 0.75; i += 1 / entity.getHeight()) {
							AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
							double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
							double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
							double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
									world.rand.nextGaussian() / 60).collide(true).time(6 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(0.96F, 0.96F, 0.96F, 0.075F)
									.scale(entity.getAvgSize() / 5).element(entity.getElement()).spawn(world);
						}

						for (double i = -90; i <= 90; i += 5) {
							Vec3d pos = AvatarEntityUtils.getMiddleOfEntity(entity);
							Vec3d newDir = entity.getLookVec().scale(entity.getHeight() / 1.75 * Math.cos(Math.toRadians(i)));
							pos = pos.add(newDir);
							pos = new Vec3d(pos.x, pos.y + (entity.getHeight() / 1.75 * Math.sin(Math.toRadians(i))), pos.z);
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
									world.rand.nextGaussian() / 60).collide(true).time(1 + AvatarUtils.getRandomNumberInRange(0, 1)).clr(0.95F, 095F, 0.95F, 0.05F)
									.scale(entity.getWidth()).element(entity.getElement()).spawnEntity(entity).spawn(world);
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).vel(entity.motionX * 0.98, entity.motionY * 0.98, entity.motionZ * 0.98).collide(true)
									.time(8 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.075F)
									.scale(entity.getWidth() * 2).spawnEntity(entity).element(entity.getElement()).spawn(world);
						}
					}
					entity.motionX *= 0.975;
					entity.motionY *= 0.975;
					entity.motionZ *= 0.975;
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
