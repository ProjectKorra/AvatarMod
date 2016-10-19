package com.crowsofwar.avatar.common.entity.data;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.fire.FirebendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class FireArcBehavior extends Behavior<EntityFireArc> {
	
	public static final DataSerializer<FireArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
		registerBehavior(Idle.class);
		
	}
	
	public FireArcBehavior() {}
	
	public FireArcBehavior(EntityFireArc water) {
		super(water);
	}
	
	protected void applyGravity() {
		entity.velocity().add(0, -9.81 / 4, 0);
	}
	
	public static class PlayerControlled extends FireArcBehavior {
		
		private String playerName;
		private EntityPlayer internalPlayer;
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityFireArc arc, EntityPlayer player) {
			super(arc);
			this.internalPlayer = player;
		}
		
		private EntityPlayer getPlayer() {
			return entity.getOwner();
		}
		
		@Override
		public FireArcBehavior onUpdate() {
			
			EntityPlayer player = getPlayer();
			if (player == null) {
				return this;
			}
			World world = player.worldObj;
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(player);
			
			if (data != null) {
				FirebendingState bendingState = (FirebendingState) data
						.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
				
				if (bendingState != null && bendingState.isManipulatingFire()) {
					
					EntityFireArc fire = bendingState.getFireArc();
					if (fire != null) {
						
						Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
								Math.toRadians(player.rotationPitch));
						Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
						Vector motion = lookPos.minus(new Vector(fire));
						motion.mul(.3);
						fire.moveEntity(motion.x(), motion.y(), motion.z());
						
					} else {
						if (!world.isRemote) bendingState.setFireArc(null);
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
	
	public static class Thrown extends FireArcBehavior {
		
		public Thrown() {}
		
		public Thrown(EntityFireArc arc) {
			super(arc);
		}
		
		@Override
		public FireArcBehavior onUpdate() {
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
		
	}
	
	public static class Idle extends FireArcBehavior {
		
		public Idle() {}
		
		public Idle(EntityFireArc arc) {
			super(arc);
		}
		
		@Override
		public FireArcBehavior onUpdate() {
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
}
