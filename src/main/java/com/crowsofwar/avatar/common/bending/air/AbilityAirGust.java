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
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;

/**
 * @author CrowsOfWar
 */
public class AbilityAirGust extends Ability {

	public AbilityAirGust() {
		super(Airbending.ID, "air_gust");
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		if (!bender.consumeChi(STATS_CONFIG.chiAirGust)) return;

		Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
				Math.toRadians(entity.rotationPitch));
		Vector pos = Vector.getEyePos(entity);
		if (bender.consumeChi(STATS_CONFIG.chiAirGust)) {

			float speed = 35;
			float size = 1.0F;
			int lifetime = 20;
			if (ctx.getLevel() == 1) {
				speed = 40;
				size = 1.25F;
				lifetime += 10;
			}
			if (ctx.getLevel() >= 2) {
				speed = 45;
				size = 1.5F;
				lifetime += 20;
			}
			if (ctx.isDynamicMasterLevel(FIRST)) {
				size = 0.75F;
				speed = 55;
				lifetime += 10;
			}
			if (ctx.isDynamicMasterLevel(SECOND)) {
				size = 2.25F;
				speed = 15;
				lifetime += 30;
			}

			size *= ctx.getPowerRatingDamageMod();
			speed += 5 * ctx.getPowerRatingDamageMod();

			EntityAirGust gust = new EntityAirGust(world);
			gust.setVelocity(look.times(speed));
			gust.setPosition(pos.minusY(0.5));
			gust.setOwner(entity);
			gust.setEntitySize(size);
			gust.setDamage(0);
			gust.setLifeTime(lifetime);
			gust.rotationPitch = entity.rotationPitch;
			gust.rotationYaw = entity.rotationYaw;
			gust.setPushStone(ctx.getLevel() >= 1);
			gust.setPushIronDoor(ctx.getLevel() >= 2);
			gust.setPushIronTrapDoor(ctx.getLevel() >= 2);
			gust.setDestroyProjectiles(ctx.isDynamicMasterLevel(FIRST));
			gust.setSlowProjectiles(ctx.isDynamicMasterLevel(SECOND));
			gust.setPiercesEnemies(ctx.getLevel() >= 1);
			gust.setAbility(this);
			gust.setLifeTime(30);
			gust.setTier(getCurrentTier(ctx.getLevel()));
			gust.setXp(SKILLS_CONFIG.airGustHit);
			gust.setBehaviour(new AirGustBehaviour());
			if (!world.isRemote)
				world.spawnEntity(gust);

			if (world.isRemote) {
				for (double angle = 0; angle < 360; angle += Math.max((int) (size * 15), 15)) {
					Vector position = Vector.getOrthogonalVector(entity.getLookVec(), angle, size / 20F);
					Vector velocity;
					//position = position.plus(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20);
					position = position.plus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity)));
					velocity = position.minus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity))).normalize();
					velocity = velocity.times(speed / 400);
					double spawnX = position.x();
					double spawnY = position.y();
					double spawnZ = position.z();
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
							world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
							.time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(entity)
							.scale(size * (1 / size)).element(new Airbending()).collide(true).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
							world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
							.time(10 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(entity)
							.scale(size * (1 / size)).element(new Airbending()).collide(true).spawn(world);
				}
			}
			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, entity.getSoundCategory(), 1.0F + Math.max(ctx.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
		}
		super.execute(ctx);
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirGust(this, entity, bender);
	}

	public static class AirGustBehaviour extends OffensiveBehaviour {

		@Override
		public Behavior<EntityOffensive> onUpdate(EntityOffensive entity) {
			if (entity != null) {
				World world = entity.world;
				if (world.isRemote && entity.getOwner() != null) {
					for (int i = 0; i < 4; i++) {
						Vec3d mid = AvatarEntityUtils.getMiddleOfEntity(entity);
						double spawnX = mid.x + world.rand.nextGaussian() / 20;
						double spawnY = mid.y + world.rand.nextGaussian() / 20;
						double spawnZ = mid.z + world.rand.nextGaussian() / 20;
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45,
								world.rand.nextGaussian() / 45).time(4 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.075F).spawnEntity(entity)
								.scale(entity.getAvgSize() * (1 / entity.getAvgSize() + 1)).element(entity.getElement()).collide(true).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45 + entity.motionX,
								world.rand.nextGaussian() / 45 + entity.motionY, world.rand.nextGaussian() / 45 + entity.motionZ)
								.time(14 + AvatarUtils.getRandomNumberInRange(0, 10)).clr(0.95F, 0.95F, 0.95F, 0.075F).spawnEntity(entity)
								.scale(entity.getAvgSize() * (1 / entity.getAvgSize() + 0.5F)).element(entity.getElement()).collide(true).spawn(world);
					}
					for (int i = 0; i < 2; i++) {
						Vec3d pos = Vector.getOrthogonalVector(entity.getLookVec(), i * 180 + (entity.ticksExisted % 360) * 20 *
								(1 / entity.getAvgSize()), entity.getAvgSize() / 1.5F).toMinecraft();
						Vec3d velocity;
						Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(entity);

						pos = pos.add(entityPos);
						velocity = pos.subtract(entityPos).normalize();
						velocity = velocity.scale(AvatarUtils.getSqrMagnitude(entity.getVelocity()) / 400000);
						double spawnX = pos.x;
						double spawnY = pos.y;
						double spawnZ = pos.z;
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
								world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
								.time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(entity)
								.scale(0.75F * entity.getAvgSize() * (1 / entity.getAvgSize())).element(new Airbending()).collide(true).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
								world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
								.time(10 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(entity)
								.scale(0.75F * entity.getAvgSize() * (1 / entity.getAvgSize())).element(new Airbending()).collide(true).spawn(world);

					}
					entity.setVelocity(entity.velocity().times(0.95));
					if (entity.velocity().sqrMagnitude() < 0.5 * 0.5)
						entity.Dissipate();

					float expansionRate = 1f / 80;
					entity.setEntitySize(entity.getAvgSize() + expansionRate);
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

}
