package com.maxandnoah.avatar.common.bending;

import javax.vecmath.Vector3d;

import com.maxandnoah.avatar.common.AvatarControlList;
import com.maxandnoah.avatar.common.ability.AbilityPickupRock;
import com.maxandnoah.avatar.common.ability.IAbility;
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
	
	private IAbility abilityPickupRock;
	
	Earthbending() {
		abilityPickupRock = new AbilityPickupRock();
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
		
		
		
//		System.out.println(offset.toString());
		//Minecraft Entity EntityArrow ItemBow
		
	}
	
}
