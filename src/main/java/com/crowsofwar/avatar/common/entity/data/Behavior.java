package com.crowsofwar.avatar.common.entity.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.avatar.AvatarLog;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * Describes a synced behavior. See state design pattern
 * 
 * @param E
 *            Type of entity this behavior is for
 * 
 * @author CrowsOfWar
 */
public abstract class Behavior<E extends Entity> {
	
	public static final DataSerializer<Behavior> DATA_SERIALIZER = new DataSerializer<Behavior>() {
		
		// FIXME research- why doesn't read/write get called every time that behavior changes???
		
		@Override
		public void write(PacketBuffer buf, Behavior value) {
			buf.writeInt(value.getId());
			value.toBytes(buf);
		}
		
		@Override
		public Behavior read(PacketBuffer buf) throws IOException {
			try {
				
				Behavior behavior = behaviorIdToClass.get(buf.readInt()).newInstance();
				behavior.fromBytes(buf);
				return behavior;
				
			} catch (Exception e) {
				
				AvatarLog.error("Error reading Behavior from bytes");
				e.printStackTrace();
				return null;
				
			}
		}
		
		@Override
		public DataParameter<Behavior> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	
	private static int nextId = 1;
	private static Map<Integer, Class<? extends Behavior>> behaviorIdToClass;
	private static Map<Class<? extends Behavior>, Integer> classToBehaviorId;
	
	static {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		behaviorIdToClass = new HashMap<>();
		classToBehaviorId = new HashMap<>();
	}
	
	protected static void registerBehavior(Class<Behavior<?>> behaviorClass) {
		if (behaviorIdToClass == null) {
			DataSerializers.registerSerializer(DATA_SERIALIZER);
			behaviorIdToClass = new HashMap<>();
			classToBehaviorId = new HashMap<>();
			nextId = 1;
		}
		int id = nextId++;
		behaviorIdToClass.put(id, behaviorClass);
		classToBehaviorId.put(behaviorClass, id);
	}
	
	/**
	 * The entity that this Behavior is attached to.
	 * <p>
	 * NOTE: Is null during client-side construction from packet buffer.
	 */
	protected E entity;
	
	public Behavior() {}
	
	public Behavior(E entity) {
		setEntity(entity);
	}
	
	public void setEntity(E entity) {
		this.entity = entity;
	}
	
	public int getId() {
		return classToBehaviorId.get(getClass());
	}
	
	/**
	 * Called every update tick.
	 * 
	 * @return Next Behavior. Return <code>this</code> to continue the Behavior. Never return null.
	 */
	public abstract Behavior onUpdate();
	
	public abstract void fromBytes(PacketBuffer buf);
	
	public abstract void toBytes(PacketBuffer buf);
	
}
