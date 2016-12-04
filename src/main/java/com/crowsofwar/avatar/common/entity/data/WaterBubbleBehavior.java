package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;

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
public abstract class WaterBubbleBehavior extends Behavior {
	
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
		
		@Override
		public Behavior onUpdate() {
			System.out.println("Player controlled");
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
		
		@Override
		public Behavior onUpdate() {
			((AvatarEntity) entity).velocity().add(0, -9.81, 0);
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
