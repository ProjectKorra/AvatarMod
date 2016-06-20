package com.maxandnoah.avatar.common.bending;

import javax.vecmath.Vector3d;

import com.maxandnoah.avatar.common.AvatarControlList;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.util.VectorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.toRadians;
import static com.maxandnoah.avatar.common.util.VectorUtils.*;

public class Earthbending implements BendingController {
	
	Earthbending() {
		
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
	public void onUpdate() {
		
	}

	@Override
	public void onKeypress(String key, EntityPlayer player, AvatarPlayerData data) {
		System.out.println("Key pressed: " + key);
		World world = player.worldObj;
		
		/**
		 * 
		 * 
		 * XXX Please read on 6/20
		 * XXX Please read on 6/20
		 * XXX Please read on 6/20
		 * 
		 * The following code only works on CLIENT SIDE.
		 * It works PERFECTLY on client side. It works LESS PERFECT here.
		 * Paste the code into ClientInput.
		 * 
		 * For earthbending, create a new packet. Referred to as PacketSPickupBlock.
		 * PickupBlock is containing the coordinates of the block to pickup.
		 * On server side, to process
		 * - make sure player is using earthbending
		 * - make sure requested position is relatively close to the predicted position
		 *   (just use the code below to get a predicted position. The client's position may be
		 *   2-3 blocks off, but if it's close enough, accept it)
		 * - Pick up block, or do something with it. That's for 6/20 max to decide.
		 * 
		 * P,s get noah to do something!!
		 * 
		 */
		
		double yaw = toRadians(player.rotationYaw);
		double pitch = toRadians(player.rotationPitch);
//		Vec3 lookDir = Vec3.createVectorHelper(cos(yaw) * sin(pitch), sin(yaw) * cos(pitch), sin(pitch));
		
		Vec3 pos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);	
//		Vec3 look = fromYawPitch(yaw, pitch);
//		mult(look, 5);
//		Vec3 offset = copy(pos);
//		add(offset, look);
		Vec3 look = player.getLookVec();
		mult(look, 7);
		
		Vec3 combined = copy(pos);
		add(combined, look);
		
//		System.out.println("Pos: " + pos);
//		System.out.println("Look: " + look);
//		System.out.println("offset: " + offset);
		
//		Vec3 hit = raytrace(player.worldObj, pos, look, 0.2); // Parameters: START, END
//		System.out.println("yaw: " + player.rotationYaw);//ItemFlintAndSteel
//		System.out.println("Hit: " + hit);
		
		System.out.println("Player pos: " + pos);
		System.out.println("Adjusted pos: " + combined);
		
//		MovingObjectPosition hit = world.rayTraceBlocks(pos, combined);
		/*Vec3 hit = raytrace(world, pos, player.getLookVec(), 0.25, 10);
		if (hit != null)  {
			System.out.println("Hit: " + hit);
			System.out.println("Block: " + world.getBlock((int) hit.xCoord, (int) hit.yCoord, (int) hit.zCoord));
			world.setBlock((int) hit.xCoord, (int) hit.yCoord, (int) hit.zCoord, Blocks.stone);
		}*/
//		Minecraft.getMinecraft().entityRenderer.getMouseOver(1);
//		MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
//		MovingObjectPosition mop = world.rayTraceBlocks(pos, combined, false);
		
		double dist = 6;
		Vec3 vec3 = pos;
        Vec3 vec31 = look;
        Vec3 vec32 = vec3.addVector(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist);
        MovingObjectPosition mop = world.func_147447_a(vec3, vec32, false, false, true);
		//Minecraft
        
        
		System.out.println("MOP: " + mop);
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			System.out.println("Hit: " + mop.hitVec);
			Vec3 hit = mop.hitVec;
			System.out.println("Block: " + world.getBlock((int) hit.xCoord, (int) hit.yCoord, (int) hit.zCoord));
//			world.setBlock((int) hit.xCoord, (int) hit.yCoord, (int) hit.zCoord, Blocks.stone);
			world.setBlock(mop.blockX, mop.blockY, mop.blockZ, Blocks.stone);
			
//			mop.
			
		}
		
//		System.out.println(offset.toString());
		//Minecraft Entity EntityArrow ItemBow
		
	}
	
}
