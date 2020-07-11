package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.EntityAirGust;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler.AIRBURST_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_AIR_BURST;
import static com.crowsofwar.avatar.util.data.TickHandlerController.AIRBURST_CHARGE_HANDLER;

public class ShootAirBurstHandler extends TickHandler {

	public ShootAirBurstHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		AbilityData data = ctx.getData().getAbilityData(new AbilityAirBurst());
		int duration = ctx.getData().getTickHandlerDuration(this);
		//Only used for determining the charge amount
		int durationToFire = 20;

		Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
				Math.toRadians(entity.rotationPitch));
		Vector pos = Vector.getEyePos(entity);

		if (bender.consumeChi(STATS_CONFIG.chiAirBurst)) {

			float distance = STATS_CONFIG.airBurstSettings.beamRange;
			float damage = STATS_CONFIG.airBurstSettings.beamDamage;
			float speed = STATS_CONFIG.airBurstSettings.beamPush;
			float size = STATS_CONFIG.airBurstSettings.beamSize;
			int performance = 10;
			float xp = SKILLS_CONFIG.airBurstHit;
			int charge = 1;
			//Copies the charge calculations
			//Makes sure the charge is never 0.
			charge = Math.max((3 * (duration / durationToFire)) + 1, 1);
			charge = Math.min(charge, 4);
			boolean piercing = false;
			//We don't want the charge going over 4.


			switch (data.getLevel()) {
				case -1:
				case 0:
					break;
				case 1:
					damage += 2;
					distance += 3;
					speed += 5;
					size += 0.75;
					performance += 2;
					break;
				case 2:
					damage += 6;
					distance += 6;
					speed += 10;
					performance += 5;
					size += 1.5;
			}

			if (data.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage += 10;
				speed += 20;
				distance += 20;
				performance += 3;
				//Long, piercing damage beam.

			}

			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage += 2;
				speed += 10;
				size += 2.5;
				performance -= 2;

				//Shorter, bigger beam with a path that charges faster.

			}


			size *= ctx.getBender().getDamageMult(Airbending.ID);
			speed += 5 * ctx.getBender().getDamageMult(Airbending.ID);
			damage *= (0.5 + 0.125 * charge);
			size *= (0.5 + 0.125 * charge);
			speed *= (0.5 + 0.125 * charge);
			distance *= (0.8 + 0.05 * charge);

			EntityAirGust gust = new EntityAirGust(world);
			gust.setPosition(pos.minusY(0.5));
			gust.setOwner(entity);
			gust.setEntitySize(size);
			gust.setDamage(damage);
			gust.setPerformanceAmount(performance);
			gust.setXp(xp);
			gust.setLifeTime(40);
			gust.rotationPitch = entity.rotationPitch;
			gust.rotationYaw = entity.rotationYaw;
			gust.setPushStone(true);
			gust.setPushIronDoor(true);
			gust.setPushIronTrapDoor(true);
			gust.setDestroyProjectiles(true);
			gust.setPiercesEnemies(data.getLevel() >= 2);
			gust.setAbility(new AbilityAirBurst());
			gust.setTier(Math.min(new AbilityAirBurst().getCurrentTier(data), charge));
			gust.setVelocity(look.times(speed + 40));
			gust.setBehaviour(new AirBurstBeamBehaviour());
			world.spawnEntity(gust);

			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, entity.getSoundCategory(),
					1.0F + Math.max(data.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
			entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_LIGHTNING_IMPACT, entity.getSoundCategory(), 2.0F, 3.0F);

			AttributeModifier modifier = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(AIRBURST_MOVEMENT_MODIFIER_ID);
			if (modifier != null && entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(modifier)) {
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(modifier);
			}
		}
		ctx.getData().removeTickHandler(AIRBURST_CHARGE_HANDLER);
		ctx.getData().removeStatusControl(CHARGE_AIR_BURST);
		return true;
	}

	public static class AirBurstBeamBehaviour extends OffensiveBehaviour {

		@Override
		public Behavior<EntityOffensive> onUpdate(EntityOffensive entity) {
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
				for (double angle = 0; angle < 360; angle += Math.max((int) (entity.getAvgSize() * 25) + (entity.ticksExisted % 360) * 20, 20)) {
					Vector position = Vector.getOrthogonalVector(entity.getLookVec(), angle, entity.getAvgSize());
					position = position.plus(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20);
					position = position.plus(AvatarEntityUtils.getMiddleOfEntity(entity).x, AvatarEntityUtils.getMiddleOfEntity(entity).y,
							AvatarEntityUtils.getMiddleOfEntity(entity).z);
					double spawnX = position.x();
					double spawnY = position.y();
					double spawnZ = position.z();
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45,
							world.rand.nextGaussian() / 45).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.075F).spawnEntity(entity.getOwner())
							.scale(entity.getAvgSize() * (1 / entity.getAvgSize() + 0.25F)).element(entity.getElement()).collide(true).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45,
							world.rand.nextGaussian() / 45).time(10 + AvatarUtils.getRandomNumberInRange(0, 10)).clr(0.95F, 0.95F, 0.95F, 0.075F).spawnEntity(entity.getOwner())
							.scale(entity.getAvgSize() * (1 / entity.getAvgSize() + 0.25F)).element(entity.getElement()).collide(true).spawn(world);
				}
			}
			float expansionRate = 1f / 100;
			if (entity.ticksExisted % 4 == 0)
				entity.setEntitySize(entity.getAvgSize() + expansionRate);
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
