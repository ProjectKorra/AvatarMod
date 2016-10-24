package com.crowsofwar.avatar.common.entity.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.avatar.AvatarLog;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * Describes a synced behavior. They follow the state design pattern, in that
 * each behavior should be switchable over an entity, and is responsible for an
 * update tick. Typically, behaviors are static inner classes, where the outer
 * class extends Behavior and is the superclass of the inner classes.
 * <p>
 * The custom behaviors must be registered via {@link #registerBehavior(Class)}.
 * There also is probably a {@link BehaviorSerializer data serializer} to
 * synchronize server and client, so the data serializer should be registed with
 * {@link DataSerializers#registerSerializer(DataSerializer)}.
 * 
 * @param E
 *            Type of entity this behavior is for
 * 
 * @author CrowsOfWar
 */
public abstract class Behavior<E extends Entity> {
	
	private static int nextId = 1;
	private static Map<Integer, Class<? extends Behavior>> behaviorIdToClass;
	private static Map<Class<? extends Behavior>, Integer> classToBehaviorId;
	
	protected static int registerBehavior(Class<? extends Behavior> behaviorClass) {
		if (behaviorIdToClass == null) {
			behaviorIdToClass = new HashMap<>();
			classToBehaviorId = new HashMap<>();
			nextId = 1;
		}
		int id = nextId++;
		behaviorIdToClass.put(id, behaviorClass);
		classToBehaviorId.put(behaviorClass, id);
		return id;
	}
	
	/**
	 * Looks up the behavior class by the given Id, then instantiates an
	 * instance with reflection.
	 */
	public static Behavior lookup(int id, Entity entity) {
		try {
			
			Behavior behavior = behaviorIdToClass.get(id).newInstance();
			behavior.entity = entity;
			return behavior;
			
		} catch (Exception e) {
			
			AvatarLog.error("Error constructing behavior...");
			e.printStackTrace();
			return null;
			
		}
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
	 * @return Next Behavior. Return <code>this</code> to continue the Behavior.
	 *         Never return null.
	 */
	public abstract Behavior onUpdate();
	
	public abstract void fromBytes(PacketBuffer buf);
	
	public abstract void toBytes(PacketBuffer buf);
	
	public abstract void load(NBTTagCompound nbt);
	
	public abstract void save(NBTTagCompound nbt);
	
	public static class BehaviorSerializer<B extends Behavior<? extends Entity>>
			implements DataSerializer<B> {
		
		// FIXME research- why doesn't read/write get called every time that
		// behavior changes???
		
		@Override
		public void write(PacketBuffer buf, B value) {
			buf.writeInt(value.getId());
			value.toBytes(buf);
		}
		
		@Override
		public B read(PacketBuffer buf) throws IOException {
			try {
				
				Behavior behavior = behaviorIdToClass.get(buf.readInt()).newInstance();
				behavior.fromBytes(buf);
				return (B) behavior;
				
			} catch (Exception e) {
				
				AvatarLog.error("Error reading Behavior from bytes");
				e.printStackTrace();
				return null;
				
			}
		}
		
		@Override
		public DataParameter<B> createKey(int id) {
			return new DataParameter<>(id, this);
		}
		
	}
	
}
