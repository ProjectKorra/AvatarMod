package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WaterBubbleBehavior extends Behavior<EntityWaterBubble> {
	
	public static final DataSerializer<WaterBubbleBehavior> DATA_SERIALIZER = new BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Drop.class);
		registerBehavior(PlayerControlled.class);
		registerBehavior(Place.class);
	}
	
	protected WaterBubbleBehavior() {}
	
	protected WaterBubbleBehavior(EntityWaterBubble bubble) {
		super(bubble);
	}
	
	public static class Drop extends WaterBubbleBehavior {
		
		public Drop() {}
		
		public Drop(EntityWaterBubble bubble) {
			super(bubble);
		}
		
		@Override
		public Behavior onUpdate() {
			((AvatarEntity) entity).velocity().add(0, -9.81, 0);
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class PlayerControlled extends WaterBubbleBehavior {
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityWaterBubble bubble) {
			super(bubble);
		}
		
		@Override
		public Behavior onUpdate() {
			EntityPlayer player = entity.getOwner();
			
			if (player == null) return this;
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			
			double yaw = Math.toRadians(player.rotationYaw);
			double pitch = Math.toRadians(player.rotationPitch);
			Vector forward = Vector.fromYawPitch(yaw, pitch);
			Vector eye = Vector.getEyePos(player);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(new Vector(entity));
			motion.mul(5);
			entity.velocity().set(motion);
			// System.out.println(motion);
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class Place extends WaterBubbleBehavior {
		
		public Place() {}
		
		public Place(EntityWaterBubble bubble) {
			super(bubble);
		}
		
		@Override
		public Behavior onUpdate() {
			entity.velocity().add(0, -9.81, 0);
			if (entity.onGround) {
				entity.worldObj.setBlockState(entity.getPosition(), Blocks.WATER.getDefaultState());
			}
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
}
