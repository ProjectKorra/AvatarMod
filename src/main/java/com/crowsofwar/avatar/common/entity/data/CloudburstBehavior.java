package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.air.AbilityCloudBurst;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public abstract class CloudburstBehavior extends Behavior<EntityCloudBall> {
	public static final DataSerializer<CloudburstBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(CloudburstBehavior.Idle.class);
		ID_PLAYER_CONTROL = registerBehavior(CloudburstBehavior.PlayerControlled.class);
		ID_THROWN = registerBehavior(CloudburstBehavior.Thrown.class);
	}

	public static class Idle extends CloudburstBehavior {

		@Override
		public CloudburstBehavior onUpdate(EntityCloudBall entity) {
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

	public static class Thrown extends CloudburstBehavior {

		int time = 0;

		@Override
		public CloudburstBehavior onUpdate(EntityCloudBall entity) {

			time++;

			if (entity.collided || (!entity.world.isRemote && time > 200)) {
				entity.cloudBurst();
				entity.setDead();
			}

			entity.addVelocity(0, -1F / 120, 0);

			World world = entity.world;
			if (!entity.isDead && !entity.world.isRemote) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
						entity.getExpandedHitbox());
				if (!collidedList.isEmpty()) {
					for (Entity collided : collidedList) {
						if (entity.canCollideWith(collided) && collided != entity.getOwner() && collided != entity) {
							collision(collided, entity);

						}
					}

				}
			}

			return this;

		}

		private void collision(Entity collided, EntityCloudBall entity) {

			if (collided.canBeCollidedWith() && entity.canCollideWith(collided) && !entity.world
			.isRemote) {
				if (collided.attackEntityFrom(AvatarDamageSource.causeAirDamage(collided, entity.getOwner()),
						entity.getDamage())) {
					BattlePerformanceScore.addMediumScore(entity.getOwner());
				}

				Vector motion = entity.velocity().dividedBy(80);
				motion = motion.times(STATS_CONFIG.cloudburstSettings.push).withY(0.05);
				collided.addVelocity(motion.x(), motion.y(), motion.z());

				BendingData data = Objects.requireNonNull(Bender.get(entity.getOwner())).getData();
				if (!collided.world.isRemote && data != null) {
					float xp = SKILLS_CONFIG.cloudburstHit;
					data.getAbilityData(entity.getAbility().getName()).addXp(xp);
				}

				entity.onCollideWithSolid();
				entity.setDead();

			}
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

	public static class PlayerControlled extends CloudburstBehavior {

		public PlayerControlled() {
		}

		@Override
		public CloudburstBehavior onUpdate(EntityCloudBall entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;
				BendingData data = Objects.requireNonNull(Bender.get(owner)).getData();

				Vector forward = Vector.getLookRectangular(owner);
				Vector eye = Vector.getEyePos(owner).minusY(0.5);
				Vector target = forward.times(1.5).plus(eye);
				Vector motion = target.minus(Vector.getEntityPos(entity)).times(6);
				entity.setVelocity(motion);

				if (entity.getAbility() instanceof AbilityCloudBurst && !entity.world.isRemote) {
					if (data.getAbilityData("cloudburst").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
						int size = entity.getSize();
						if (size < 60 && entity.ticksExisted % 4 == 0) {
							entity.setSize(size + 1);
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

}
