package com.crowsofwar.avatar.common.bending.ability;

import java.util.Random;
import java.util.function.Function;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.EarthbendingState;
import com.crowsofwar.avatar.common.bending.IBendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock.OnBlockLand;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
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
public class AbilityPickupBlock implements IBendingAbility {
	
	private final BendingController controller;
	private final Function<IBlockState, Boolean> bendableCallback;
	private final Random random;
	
	public AbilityPickupBlock(BendingController controller,
			Function<IBlockState, Boolean> bendableCallback) {
		this.controller = controller;
		this.bendableCallback = bendableCallback;
		this.random = new Random();
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return false;
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
				if (bendableCallback.apply(ibs)) {
					
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
						world.playSound(target.x() + 0.5, target.y() + 0.5, target.z() + 0.5,
								sound.getBreakSound(), SoundCategory.PLAYERS, sound.getVolume(),
								sound.getPitch(), false);
					}
					
					world.setBlockState(target.toBlockPos(), Blocks.AIR.getDefaultState());
					
				} else {
					world.playSound(player, player.getPosition(), SoundEvents.BLOCK_LEVER_CLICK,
							SoundCategory.PLAYERS, 1, (float) (random.nextGaussian() / 0.25 + 0.375));
				}
				
			}
		}
	}
	
	@Override
	public int getId() {
		return 1;
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
}
