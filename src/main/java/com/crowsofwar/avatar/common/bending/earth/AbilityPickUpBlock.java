package com.crowsofwar.avatar.common.bending.earth;

import java.util.Random;
import java.util.function.Predicate;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock.OnBlockLand;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityPickUpBlock extends BendingAbility<EarthbendingState> {
	
	private final Predicate<IBlockState> bendableCallback;
	private final Random random;
	private final Raytrace.Info raytrace;
	
	public AbilityPickUpBlock(BendingController<EarthbendingState> controller,
			Predicate<IBlockState> bendableCallback) {
		super(controller);
		this.bendableCallback = bendableCallback;
		this.random = new Random();
		this.raytrace = new Raytrace.Info(-1, true);
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return true;
	}
	
	@Override
	public void update(AvatarPlayerData data) {
		EarthbendingState state = data.getBendingState(controller);
		if (state != null) {
			EntityPlayer player = data.getPlayerEntity();
			EntityFloatingBlock floating = state.getPickupBlock();
			
			if (floating != null && floating.ticksExisted > 20) {
				floating.setOwner(player);
				
				if (floating.isGravityEnabled()) {
					floating.setGravityEnabled(false);
				}
				
				double yaw = Math.toRadians(player.rotationYaw);
				double pitch = Math.toRadians(player.rotationPitch);
				Vector forward = Vector.fromYawPitch(yaw, pitch);
				Vector eye = Vector.getEyePos(player);
				Vector target = forward.times(2).plus(eye);
				Vector motion = target.minus(new Vector(floating));
				motion.mul(5);
				floating.setVelocity(motion);
				
			}
			
		}
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		
		EarthbendingState ebs = (EarthbendingState) data.getBendingState(controller);
		EntityPlayer player = data.getPlayerEntity();
		World world = data.getWorld();
		PlayerState state = data.getState();
		
		if (ebs.getPickupBlock() != null) {
			ebs.getPickupBlock().drop();
			ebs.setPickupBlock(null);
			AvatarMod.network.sendTo(new PacketCPlayerData(data), (EntityPlayerMP) player);
		} else {
			VectorI target = state.verifyClientLookAtBlock(-1, 5);
			if (target != null) {
				IBlockState ibs = world.getBlockState(target.toBlockPos());
				Block block = ibs.getBlock();
				if (bendableCallback.test(ibs)) {
					
					EntityFloatingBlock floating = new EntityFloatingBlock(world, ibs);
					floating.setPosition(target.x() + 0.5, target.y(), target.z() + 0.5);
					floating.setItemDropsEnabled(!player.capabilities.isCreativeMode);
					
					double dist = 2.5;
					Vector force = new Vector(0, Math.sqrt(19.62 * dist), 0);
					floating.addVelocity(force);
					floating.setGravityEnabled(true);
					floating.setCanFall(false);
					floating.setOnLandBehavior(OnBlockLand.DO_NOTHING);
					floating.setOwner(player);
					
					world.spawnEntityInWorld(floating);
					
					ebs.setPickupBlock(floating);
					data.sendBendingState(ebs);
					
					SoundType sound = block.getSoundType();
					if (sound != null) {
						world.playSound(null, target.toBlockPos(), sound.getBreakSound(),
								SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
					}
					
					world.setBlockState(target.toBlockPos(), Blocks.AIR.getDefaultState());
					
				} else {
					world.playSound(null, player.getPosition(), SoundEvents.BLOCK_LEVER_CLICK,
							SoundCategory.PLAYERS, 1, (float) (random.nextGaussian() / 0.25 + 0.375));
				}
				
			}
		}
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
