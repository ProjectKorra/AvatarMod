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

package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.AvatarLog;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes a synced behavior. They follow the state design pattern, in that
 * each behavior should be switchable over an entity, and is responsible for an
 * update tick. Typically, behaviors are static inner classes, where the outer
 * class extends Behavior and is the superclass of the inner classes.
 * <p>
 * All custom behaviors must be registered via {@link #registerBehavior(Class)}.
 * As Behaviors are most commonly synced with DataManager, a
 * {@link BehaviorSerializer data serializer} is needed to synchronize server
 * and client. It should be registered with
 * {@link DataSerializers#registerSerializer(DataSerializer)}.
 * <p>
 * Make sure that subclasses receive the instance of entity. For server-side
 * changing of behavior, call {@link #Behavior(Entity) the constructor with
 * Entity argument}. Client-side
 *
 * @param E Type of entity this behavior is for
 * @author CrowsOfWar
 */
public abstract class Behavior<E extends Entity> {

	private static int nextId = 1;
	private static Map<Integer, Class<? extends Behavior>> behaviorIdToClass;
	private static Map<Class<? extends Behavior>, Integer> classToBehaviorId;

	public Behavior() {
	}

	// Static method called from preInit
	public static void registerBehaviours() {
		FloatingBlockBehavior.register();
		WaterArcBehavior.register();
		WaterBubbleBehavior.register();
		WallBehavior.register();
		EarthspikesBehavior.register();
		FireballBehavior.register();
		CloudburstBehavior.register();
		LightningSpearBehavior.register();
		LightOrbBehavior.register();
		LightCylinderBehaviour.register();
		OffensiveBehaviour.register();
		ShockwaveBehaviour.register();
	}

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
	 * Looks up the behavior class by the given Id, then instantiates an instance
	 * with reflection.
	 */
	public static Behavior lookup(int id, Entity entity) {
		try {

			Behavior behavior = behaviorIdToClass.get(id).newInstance();
			return behavior;

		} catch (Exception e) {

			AvatarLog.error("Error constructing behavior...");
			e.printStackTrace();
			return null;

		}
	}

	public int getId() {
		return classToBehaviorId.get(getClass());
	}

	/**
	 * Called every update tick.
	 *
	 * @return Next Behavior. Return <code>this</code> to continue the Behavior.
	 * May never return null.
	 */
	public abstract Behavior onUpdate(E entity);

	public void renderUpdate(E entity) {

	}

	public abstract void fromBytes(PacketBuffer buf);

	public abstract void toBytes(PacketBuffer buf);

	public abstract void load(NBTTagCompound nbt);

	public abstract void save(NBTTagCompound nbt);


	public static class BehaviorSerializer<B extends Behavior<? extends Entity>> implements DataSerializer<B> {

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

		@Override
		public B copyValue(B behavior) {
			return behavior;
		}

	}

}
