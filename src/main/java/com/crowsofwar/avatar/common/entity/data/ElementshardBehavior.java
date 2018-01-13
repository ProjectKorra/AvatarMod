package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityElementshard;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public abstract class ElementshardBehavior extends Behavior<EntityElementshard> {
	public static final DataSerializer<ElementshardBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(ElementshardBehavior.Idle.class);
		ID_PLAYER_CONTROL = registerBehavior(ElementshardBehavior.PlayerControlled.class);
		ID_THROWN = registerBehavior(ElementshardBehavior.Thrown.class);
	}

	public static class Idle extends ElementshardBehavior {

		@Override
		public ElementshardBehavior onUpdate(EntityElementshard entity) {
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

	public static class Thrown extends ElementshardBehavior {

		int time = 0;

		@Override
		public ElementshardBehavior onUpdate(EntityElementshard entity) {
			EntityLivingBase owner = entity.getOwner();


			time++;

			if (entity.isCollided || (!entity.world.isRemote && time > 100)) {
				entity.setDead();
				entity.onCollideWithSolid();
			}

			entity.addVelocity(Vector.DOWN.times(9.81 / 40));

			World world = entity.world;
			if (!entity.isDead) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
						entity.getExpandedHitbox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != entity.getOwner()) {
						collision((EntityLivingBase) collided, entity);
					} else if (collided != entity.getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(entity));
						motion = motion.times(0.3).withY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}

				}
			}

			return this;

		}

		private void collision(EntityLivingBase collided, EntityElementshard entity) {
			double speed = entity.velocity().magnitude();

			if (collided.attackEntityFrom(AvatarDamageSource.causeFireballDamage(collided, entity.getOwner()),
					entity.getDamage())) {
				BattlePerformanceScore.addMediumScore(entity.getOwner());
			}

			Vector motion = entity.velocity().dividedBy(20);
			motion = motion.times(STATS_CONFIG.fireballSettings.push).withY(0.08);
			collided.addVelocity(motion.x(), motion.y(), motion.z());

			BendingData data = Bender.get(entity.getOwner()).getData();
			if (!collided.world.isRemote && data != null) {
				float xp = SKILLS_CONFIG.fireballHit;
				data.getAbilityData("element_shard").addXp(xp);
			}

			// Remove the fireball & spawn particles
			if (!entity.world.isRemote) entity.setDead();
			entity.onCollideWithSolid();
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

	public static class PlayerControlled extends ElementshardBehavior {

		public PlayerControlled() {
		}

		@Override
		public ElementshardBehavior onUpdate(EntityElementshard entity) {
			EntityLivingBase owner = entity.getOwner();


			if (owner == null) return this;

			BendingData data = Bender.get(owner).getData();

			Vector forward = Vector.getLookRectangular(owner);
			Vector eye = Vector.getEyePos(owner);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(Vector.getEntityPos(entity)).times(5 /* <-- !! you can adjust that number to make the shards move faster */);
			entity.rotationPitch = entity.rotationPitch +3;
			entity.rotationYaw = entity.rotationYaw +3;

			entity.setVelocity(motion);

			if (data.getAbilityData("element_shard").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				int size = entity.getSize();
				if (size < 60 && entity.ticksExisted % 4 == 0) {
					entity.setSize(size + 1);
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


