package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.util.VectorUtils.times;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.gui.AvatarGuiIds;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.BlockPos;
import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Earthbending implements IBendingController {
	
	private final BendingMenuInfo menu;
	private final List<Block> bendableBlocks;
	
	Earthbending() {
		Color light = new Color(225, 225, 225);
		Color brown = new Color(107, 76, 47);
		Color gray = new Color(90, 90, 90);
		Color lightBrown = new Color(219, 201, 182);
		ThemeColor background = new ThemeColor(lightBrown, brown);
		ThemeColor edge = new ThemeColor(brown, brown);
		ThemeColor icon = new ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon), AvatarControl.KEY_EARTHBENDING,
				AvatarGuiIds.GUI_RADIAL_MENU_EARTH, AvatarAbility.ACTION_TOGGLE_BENDING, AvatarAbility.ACTION_THROW_BLOCK);
		
		bendableBlocks = new ArrayList<Block>();
		bendableBlocks.add(Blocks.stone);
		bendableBlocks.add(Blocks.sand);
		bendableBlocks.add(Blocks.sandstone);
		bendableBlocks.add(Blocks.cobblestone);
		bendableBlocks.add(Blocks.dirt);
		bendableBlocks.add(Blocks.gravel);
		bendableBlocks.add(Blocks.brick_block);
		bendableBlocks.add(Blocks.mossy_cobblestone);
		bendableBlocks.add(Blocks.nether_brick);
		bendableBlocks.add(Blocks.stonebrick);
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public int getID() {
		return BendingManager.BENDINGID_EARTHBENDING;
	}
	
	@Override
	public void onAbility(AvatarAbility ability, AvatarPlayerData data) {
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		World world = player.worldObj;
		EarthbendingState ebs = (EarthbendingState) data.getBendingState(this);
		
		if (ability == AvatarAbility.ACTION_TOGGLE_BENDING) {
			if (ebs.getPickupBlock() != null) {
				ebs.getPickupBlock().drop();
				ebs.setPickupBlock(null);
				AvatarMod.network.sendTo(new PacketCPlayerData(data), (EntityPlayerMP) player);
			} else {
				BlockPos target = state.verifyClientLookAtBlock(-1, 5);
				if (target != null) {
					Block block = world.getBlock(target.x, target.y, target.z);
					if (bendableBlocks.contains(block)) {
						world.setBlock(target.x, target.y, target.z, Blocks.air);
						
						EntityFloatingBlock floating = new EntityFloatingBlock(world, block);
						floating.setPosition(target.x + 0.5, target.y, target.z + 0.5);
						
						double dist = 2.5;
						Vec3 force = Vec3.createVectorHelper(0, Math.sqrt(19.62*dist), 0);
						floating.addForce(force);
						floating.setGravityEnabled(true);
						floating.setCanFall(false);
						floating.setDestroyable(false);
						floating.setOwner(player);
						
						world.spawnEntityInWorld(floating);
						
						ebs.setPickupBlock(floating);
						data.sendBendingState(ebs);
					} else {
						world.playSoundAtEntity(player, "random.click", 1.0f,
								(float) (random.nextGaussian() / 0.25 + 0.375));
					}
					
				}
			}
		}
		if (ability == AvatarAbility.ACTION_THROW_BLOCK) {
			EntityFloatingBlock floating = ebs.getPickupBlock();
			if (floating != null) {
				float yaw = (float) Math.toRadians(player.rotationYaw);
				float pitch = (float) Math.toRadians(player.rotationPitch);
				
				// Calculate force and everything
				Vec3 lookDir = VectorUtils.fromYawPitch(yaw, pitch);
				floating.addForce(times(lookDir, 20));
				
				floating.drop();
				ebs.setPickupBlock(null);
				AvatarMod.network.sendTo(new PacketCPlayerData(data), (EntityPlayerMP) player);
				
			}
		}
		if (ability == AvatarAbility.ACTION_PUT_BLOCK) {
			EntityFloatingBlock floating = ebs.getPickupBlock();
			if (floating != null) {
				// TODO Verify look at block
				BlockPos looking = state.getClientLookAtBlock();
				ForgeDirection lookingSide = state.getLookAtSide();
				if (looking != null && lookingSide != null) {
					int x = looking.x + lookingSide.offsetX;
					int y = looking.y + lookingSide.offsetY;
					int z = looking.z + lookingSide.offsetZ;
					if (world.getBlock(x, y, z) == Blocks.air) {
						world.setBlock(x, y, z, floating.getBlock());
						floating.setDead();
					}
				}
			}
		}
		
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new EarthbendingState(data);
	}
	
	@Override
	public void onUpdate(AvatarPlayerData data) {
		EarthbendingState state = (EarthbendingState) data.getBendingState(this);
		if (state != null) {
			EntityPlayer player = data.getState().getPlayerEntity();
			EntityFloatingBlock floating = state.getPickupBlock();
//			System.out.println(floating.ticksExisted);
			
			if (floating != null && floating.ticksExisted > 20) {
				floating.setOwner(player);
				
				if (floating.isGravityEnabled()) {
					floating.setGravityEnabled(false);
				}
				
				double yaw = Math.toRadians(player.rotationYaw);
				double pitch = Math.toRadians(player.rotationPitch);
				Vec3 forward = VectorUtils.fromYawPitch(yaw, pitch);
				Vec3 eye = VectorUtils.getEyePos(player);
				Vec3 target = VectorUtils.plus(VectorUtils.times(forward, 2), eye);
				Vec3 motion = VectorUtils.minus(target, VectorUtils.getEntityPos(floating));
//				System.out.println(VectorUtils.getEntityPos(floating));
//				motion.normalize();
				VectorUtils.mult(motion, 5);
//				System.out.println(VectorUtils.getEyePos(player).toString());
//				if (motion.squareDistanceTo(0, 0, 0) > 3) {
//					motion.normalize();
//					VectorUtils.mult(motion, 3);
//				}
				
//				motion = Vec3.createVectorHelper(-1, 0, 0);
				
//				floating.setVelocity(motion);
				
//				if (floating.isGravityEnabled() || floating.canFall()) {
//					floating.setGravityEnabled(false);
//					floating.setCanFall(true);
//				}
				
//				floating.moveEntity(target.xCoord - floating.posX, target.yCoord - floating.posY, target.zCoord - floating.posZ);
//				floating.moveEntity(motion.xCoord, motion.yCoord, motion.zCoord);
				floating.setVelocity(motion);
//				floating.setPositionAndRotation(target.xCoord, target.yCoord, target.zCoord, 0, 0);
				
			}
			
		}
	}

	@Override
	public AvatarAbility getAbility(AvatarPlayerData data, AvatarControl input) {
		PlayerState state = data.getState();
		EarthbendingState ebs = (EarthbendingState) data.getBendingState(this);
		
		if (ebs.getPickupBlock() != null) {
			if (input == AvatarControl.CONTROL_LEFT_CLICK_DOWN) return AvatarAbility.ACTION_THROW_BLOCK;
			if (input == AvatarControl.CONTROL_RIGHT_CLICK_DOWN) return AvatarAbility.ACTION_PUT_BLOCK;
		}
		
		return AvatarAbility.NONE;
	}

	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
}
