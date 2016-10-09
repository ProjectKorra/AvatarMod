package com.crowsofwar.avatar.common.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WaterArcBehavior {
	
	public static final DataSerializer<WaterArcBehavior> DATA_SERIALIZER = new DataSerializer<WaterArcBehavior>() {
		
		@Override
		public void write(PacketBuffer buf, WaterArcBehavior value) {
			buf.writeInt(value.getId());
			value.toBytes(buf);
		}
		
		@Override
		public WaterArcBehavior read(PacketBuffer buf) throws IOException {
			try {
				
				WaterArcBehavior behavior = behaviorIdToClass.get(buf.readInt()).newInstance();
				behavior.fromBytes(buf);
				return behavior;
				
			} catch (Exception e) {
				
				AvatarLog.error("Error reading WaterArcBehavior from bytes");
				e.printStackTrace();
				return null;
				
			}
		}
		
		@Override
		public DataParameter<WaterArcBehavior> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	
	private static int nextId = 1;
	private static final Map<Integer, Class<? extends WaterArcBehavior>> behaviorIdToClass;
	private static final Map<Class<? extends WaterArcBehavior>, Integer> classToBehaviorId;
	
	private static void registerBehavior(int id, Class<? extends WaterArcBehavior> behaviorClass) {
		behaviorIdToClass.put(id, behaviorClass);
		classToBehaviorId.put(behaviorClass, id);
	}
	
	static {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		behaviorIdToClass = new HashMap<>();
		classToBehaviorId = new HashMap<>();
		
		registerBehavior(1, PlayerControlled.class);
		registerBehavior(2, Thrown.class);
		registerBehavior(3, Idle.class);
		
	}
	
	protected EntityWaterArc water;
	
	public WaterArcBehavior() {}
	
	public WaterArcBehavior(EntityWaterArc water) {
		setWaterArc(water);
	}
	
	public void setWaterArc(EntityWaterArc water) {
		this.water = water;
	}
	
	public int getId() {
		return classToBehaviorId.get(getClass());
	}
	
	/**
	 * Called every update tick.
	 * 
	 * @return Next WaterArcBehavior. Return <code>this</code> to continue the WaterArcBehavior.
	 */
	public abstract WaterArcBehavior onUpdate();
	
	public abstract void fromBytes(PacketBuffer buf);
	
	public abstract void toBytes(PacketBuffer buf);
	
	protected void applyGravity() {
		water.velocity().add(0, -9.81 / 20, 0);
	}
	
	public static class PlayerControlled extends WaterArcBehavior {
		
		private String playerName;
		private EntityPlayer internalPlayer;
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityWaterArc arc, EntityPlayer player) {
			super(arc);
			this.internalPlayer = player;
		}
		
		private EntityPlayer getPlayer() {
			if (internalPlayer == null) {
				internalPlayer = water.worldObj.getPlayerEntityByName(playerName);
			}
			return internalPlayer;
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			
			EntityPlayer player = getPlayer();
			World world = player.worldObj;
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(player);
			
			if (data != null) {
				WaterbendingState bendingState = (WaterbendingState) data
						.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
				
				if (bendingState.isBendingWater()) {
					
					EntityWaterArc water = bendingState.getWaterArc();
					if (water != null) {
						Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
								Math.toRadians(player.rotationPitch));
						Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
						Vector motion = lookPos.minus(new Vector(water));
						motion.normalize();
						motion.mul(.15);
						water.moveEntity(motion.x(), motion.y(), motion.z());
						
						if (water.worldObj.isRemote && water.canPlaySplash()) {
							if (motion.sqrMagnitude() >= 0.004) water.playSplash();
						}
					} else {
						if (!world.isRemote) bendingState.setWaterArc(null);
					}
					
				}
			}
			
			return this;
			
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			playerName = buf.readStringFromBuffer(16);
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeString(internalPlayer.getName());
		}
		
	}
	
	public static class Thrown extends WaterArcBehavior {
		
		public Thrown() {}
		
		public Thrown(EntityWaterArc arc) {
			super(arc);
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			applyGravity();
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public static class Idle extends WaterArcBehavior {
		
		public Idle() {}
		
		public Idle(EntityWaterArc arc) {
			super(arc);
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
}
