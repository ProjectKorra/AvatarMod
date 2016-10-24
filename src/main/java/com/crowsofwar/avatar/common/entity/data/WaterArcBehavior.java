package com.crowsofwar.avatar.common.entity.data;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WaterArcBehavior extends Behavior<EntityWaterArc> {
	
	public static final DataSerializer<WaterArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
		registerBehavior(Idle.class);
		
	}
	
	public WaterArcBehavior() {}
	
	public WaterArcBehavior(EntityWaterArc water) {
		super(water);
	}
	
	protected void applyGravity() {
		entity.velocity().add(0, -9.81 / 50, 0);
	}
	
	public static class PlayerControlled extends WaterArcBehavior {
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityWaterArc arc, EntityPlayer player) {
			super(arc);
		}
		
		private EntityPlayer getPlayer() {
			return entity.getOwner();
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			
			EntityPlayer player = getPlayer();
			if (player == null) {
				return this;
			}
			World world = player.worldObj;
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(player);
			
			if (data != null) {
				WaterbendingState bendingState = (WaterbendingState) data
						.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
				
				if (bendingState != null && bendingState.isBendingWater()) {
					
					EntityWaterArc water = bendingState.getWaterArc();
					if (water != null) {
						Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
								Math.toRadians(player.rotationPitch));
						Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
						Vector motion = lookPos.minus(new Vector(water));
						motion.mul(.3);
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
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class Thrown extends WaterArcBehavior {
		
		public Thrown() {}
		
		public Thrown(EntityWaterArc arc) {
			super(arc);
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			applyGravity();
			
			List<EntityLivingBase> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					EntityLivingBase.class, entity.getEntityBoundingBox().expandXyz(0.9),
					collided -> collided != entity.getOwner());
			
			for (EntityLivingBase collided : collidedList) {
				if (collided != entity.getOwner()) return this;
				collided.addVelocity(entity.motionX, 0.4, entity.motionZ);
				collided.attackEntityFrom(AvatarDamageSource.causeWaterDamage(collided, entity.getOwner()),
						6);
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
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
}
