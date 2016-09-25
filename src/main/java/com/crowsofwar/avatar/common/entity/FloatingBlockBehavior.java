package com.crowsofwar.avatar.common.entity;

import static com.crowsofwar.avatar.common.config.AvatarConfig.blockDamage;
import static com.crowsofwar.avatar.common.config.AvatarConfig.blockPush;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.earth.FloatingBlockEvent;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class FloatingBlockBehavior {
	
	public static final DataSerializer<FloatingBlockBehavior> DATA_SERIALIZER = new DataSerializer<FloatingBlockBehavior>() {
		
		@Override
		public void write(PacketBuffer buf, FloatingBlockBehavior value) {
			buf.writeInt(value.getId());
			value.toBytes(buf);
		}
		
		@Override
		public FloatingBlockBehavior read(PacketBuffer buf) throws IOException {
			try {
				
				FloatingBlockBehavior behavior = behaviorIdToClass.get(buf.readInt()).newInstance();
				behavior.fromBytes(buf);
				return behavior;
				
			} catch (Exception e) {
				
				AvatarLog.error("Error reading FloatingBlockBehavior from bytes");
				e.printStackTrace();
				return null;
				
			}
		}
		
		@Override
		public DataParameter<FloatingBlockBehavior> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	
	private static int nextId = 1;
	private static final Map<Integer, Class<? extends FloatingBlockBehavior>> behaviorIdToClass;
	private static final Map<Class<? extends FloatingBlockBehavior>, Integer> classToBehaviorId;
	
	private static void registerBehavior(int id, Class<? extends FloatingBlockBehavior> behaviorClass) {
		behaviorIdToClass.put(id, behaviorClass);
		classToBehaviorId.put(behaviorClass, id);
	}
	
	static {
		System.out.println("==========registering serializer=============");
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		behaviorIdToClass = new HashMap<>();
		classToBehaviorId = new HashMap<>();
		registerBehavior(1, Fall.class);
		registerBehavior(2, Thrown.class);
		registerBehavior(3, DoNothing.class);
		registerBehavior(4, PickUp.class);
		registerBehavior(5, Place.class);
		registerBehavior(6, PlayerControlled.class);
	}
	
	/**
	 * The floating block that this block belongs to.
	 * <p>
	 * NOTE: Is null during client-side construction from packet buffer.
	 */
	protected EntityFloatingBlock floating;
	
	public FloatingBlockBehavior() {}
	
	public FloatingBlockBehavior(EntityFloatingBlock floating) {
		setFloatingBlock(floating);
	}
	
	public void setFloatingBlock(EntityFloatingBlock floating) {
		this.floating = floating;
	}
	
	public int getId() {
		return classToBehaviorId.get(getClass());
	}
	
	/**
	 * Called every update tick.
	 * 
	 * @return Next FloatingBlockBehavior. Return <code>this</code> to continue the
	 *         FloatingBlockBehavior.
	 */
	public abstract FloatingBlockBehavior onUpdate();
	
	public abstract void fromBytes(PacketBuffer buf);
	
	public abstract void toBytes(PacketBuffer buf);
	
	protected void applyGravity() {
		floating.addVelocity(new Vector(0, -9.81 / 20, 0));
	}
	
	public static class DoNothing extends FloatingBlockBehavior {
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public static class Place extends FloatingBlockBehavior {
		
		private BlockPos placeAt;
		
		public Place() {}
		
		public Place(BlockPos placeAt) {
			this.placeAt = placeAt;
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			Vector placeAtVec = new Vector(placeAt.getX() + 0.5, placeAt.getY(), placeAt.getZ() + 0.5);
			Vector thisPos = new Vector(floating);
			Vector force = placeAtVec.minus(thisPos);
			force.normalize();
			force.mul(3);
			floating.setVelocity(force);
			if (!floating.worldObj.isRemote && placeAtVec.sqrDist(thisPos) < 0.01) {
				
				floating.setDead();
				floating.worldObj.setBlockState(new BlockPos(floating), floating.getBlockState());
				
				BendingManager.getBending(BendingType.EARTHBENDING)
						.post(new FloatingBlockEvent.BlockPlacedReached(floating));
				
			}
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			placeAt = buf.readBlockPos();
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeBlockPos(placeAt);
		}
		
	}
	
	public static class Thrown extends FloatingBlockBehavior {
		
		public Thrown() {}
		
		/**
		 * @param floating
		 */
		public Thrown(EntityFloatingBlock floating) {
			super(floating);
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			
			if (floating.isCollided) {
				if (!floating.worldObj.isRemote) floating.setDead();
				floating.onCollision();
				BendingManager.getBending(BendingType.EARTHBENDING)
						.post(new FloatingBlockEvent.BlockThrownReached(floating));
			}
			
			applyGravity();
			
			World world = floating.worldObj;
			if (!floating.isDead) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(floating,
						floating.getExpandedHitbox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != floating.getOwner()) {
						double speed = floating.getVelocity().magnitude();
						collided.attackEntityFrom(
								AvatarDamageSource.causeFloatingBlockDamage(floating, floating.getOwner()),
								(float) (speed * blockDamage.currentValue()));
						
						Vector motion = new Vector(collided).minus(new Vector(floating));
						motion.mul(blockPush.currentValue());
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
						if (!world.isRemote) floating.setDead();
						floating.onCollision();
					} else if (collided != floating.getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(floating));
						motion.mul(0.3);
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}
				}
			}
			
			return this;
			
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public static class PickUp extends FloatingBlockBehavior {
		
		public PickUp() {}
		
		/**
		 * @param floating
		 */
		public PickUp(EntityFloatingBlock floating) {
			super(floating);
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			applyGravity();
			
			Vector velocity = floating.getVelocity();
			if (velocity.y() <= 0) {
				velocity.setY(0);
				floating.setVelocity(velocity);
				return new PlayerControlled(floating, floating.getOwner());
			}
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public static class PlayerControlled extends FloatingBlockBehavior {
		
		private String playerName;
		private EntityPlayer player;
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityFloatingBlock floating, EntityPlayer player) {
			super(floating);
			this.player = player;
		}
		
		private EntityPlayer getControllingPlayer() {
			if (player != null) {
				return player;
			} else {
				return player = floating.worldObj.getPlayerEntityByName(playerName);
			}
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			EntityPlayer controller = getControllingPlayer();
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(controller,
					"Could not get player data to update PlayerControlled Floating Block");
			
			double yaw = Math.toRadians(controller.rotationYaw);
			double pitch = Math.toRadians(controller.rotationPitch);
			Vector forward = Vector.fromYawPitch(yaw, pitch);
			Vector eye = Vector.getEyePos(controller);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(new Vector(floating));
			motion.mul(5);
			floating.setVelocity(motion);
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			playerName = buf.readStringFromBuffer(16);
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeString(player.getName());
		}
		
	}
	
	public static class Fall extends FloatingBlockBehavior {
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			applyGravity();
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
}
