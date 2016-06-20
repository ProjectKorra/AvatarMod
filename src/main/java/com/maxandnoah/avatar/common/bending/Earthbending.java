package com.maxandnoah.avatar.common.bending;

import javax.vecmath.Vector3d;

import com.maxandnoah.avatar.common.AvatarControlList;
import com.maxandnoah.avatar.common.ability.AbilityPickupRock;
import com.maxandnoah.avatar.common.ability.IAbility;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.util.BlockPos;
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
	
	private IAbility currentAbility;
	private IAbility abilityPickupRock;
	
	Earthbending() {
		currentAbility = null;
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
		World world = player.worldObj;
		
		if (key.equals(AvatarControlList.CONTROL_TOGGLE_BENDING)) {
			if (currentAbility == null) {
				currentAbility = abilityPickupRock;
				currentAbility.onAbilityActive(player, data);
			} else {
				currentAbility = null;
			}
		}
		
	}

	@Override
	public IAbility getCurrentAbility() {
		return currentAbility;
	}
	
}
