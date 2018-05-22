package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityBoulder;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public abstract class BoulderBehavior extends Behavior<EntityBoulder> {

	public static final DataSerializer<BoulderBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(DoNothing.class);
		ID_FALL = registerBehavior(Fall.class);
		ID_PICKUP = registerBehavior(PickUp.class);
		ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
		ID_THROWN = registerBehavior(Thrown.class);
	}

	public static class DoNothing extends BoulderBehavior {

		@Override
		public BoulderBehavior onUpdate(EntityBoulder entity) {
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


	public static class Thrown extends BoulderBehavior {

		@Override
		public BoulderBehavior onUpdate(EntityBoulder entity) {

			if (entity.isCollided) {
				if (!entity.world.isRemote) entity.setDead();
				entity.onCollideWithSolid();

				Block block = entity.getBlockState().getBlock();
				SoundType sound = block.getSoundType();
				if (sound != null) {
					entity.world.playSound(null, entity.getPosition(), sound.getBreakSound(),
							SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
				}

			}

			entity.addVelocity(Vector.DOWN.times(9.81 / 20));
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

	public static class PickUp extends FloatingBlockBehavior {

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {
			entity.addVelocity(Vector.DOWN.times(9.81 / 20));

			Vector velocity = entity.velocity();
			if (velocity.y() <= 0) {
				entity.setVelocity(velocity.withY(0));
				return new PlayerControlled();
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

	public static class PlayerControlled extends BoulderBehavior {

		public PlayerControlled() {
		}

		@Override
		public BoulderBehavior onUpdate(EntityBoulder entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;

			float Angle = entity.getSpeed() % 360;
			entity.posX = Math.cos(Math.toRadians(Angle)) * entity.getRadius();
			entity.posZ = Math.sin(Math.toRadians(Angle)) * entity.getRadius();
			entity.setSpeed(entity.getSpeed() + 1);
			//Need to make speed increase by whatever I set it too originally

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

	public static class Fall extends BoulderBehavior {

		@Override
		public BoulderBehavior onUpdate(EntityBoulder entity) {
			entity.addVelocity(Vector.DOWN.times(9.81 / 20));
			if (entity.isCollided) {
				if (!entity.world.isRemote) entity.setDead();
				entity.onCollideWithSolid();
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



