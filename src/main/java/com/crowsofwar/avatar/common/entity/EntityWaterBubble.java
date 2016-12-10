/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityWaterBubble extends AvatarEntity {
	
	private static final DataParameter<WaterBubbleBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWaterBubble.class, WaterBubbleBehavior.DATA_SERIALIZER);
	private static final DataParameter<String> SYNC_OWNER = EntityDataManager
			.createKey(EntityWaterBubble.class, DataSerializers.STRING);
	
	private final OwnerAttribute ownerAttrib;
	
	/**
	 * @param world
	 */
	public EntityWaterBubble(World world) {
		super(world);
		this.ownerAttrib = new OwnerAttribute(this, SYNC_OWNER, newOwner -> {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(newOwner);
			if (data != null)
				((WaterbendingState) data.getBendingState(BendingType.WATERBENDING)).setBubble(this);
		});
		setSize(1, 1);
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_BEHAVIOR, new WaterBubbleBehavior.Drop());
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		moveEntity(MoverType.SELF, velocity().x() / 20, velocity().y() / 20, velocity().z() / 20);
		velocity().mul(0.9);
		
		WaterBubbleBehavior currentBehavior = getBehavior();
		currentBehavior.setEntity(this); // CRITICAL: Otherwise the behavior
											// does not use correct instance of
											// entity
		WaterBubbleBehavior nextBehavior = (WaterBubbleBehavior) currentBehavior.onUpdate();
		if (currentBehavior != nextBehavior) setBehavior(nextBehavior);
		
		BlockPos down = getPosition().down();
		IBlockState downState = worldObj.getBlockState(down);
		if (downState.getBlock() == Blocks.FARMLAND && downState == Blocks.FARMLAND.getDefaultState()) {
			worldObj.setBlockState(down, Blocks.FARMLAND.getStateById(7));
		}
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		ownerAttrib.load(compound);
		setBehavior((WaterBubbleBehavior) Behavior.lookup(compound.getInteger("Behavior"), this));
		getBehavior().load(compound);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		ownerAttrib.save(compound);
		compound.setInteger("Behavior", getBehavior().getId());
		getBehavior().save(compound);
	}
	
	public WaterBubbleBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WaterBubbleBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	public EntityPlayer getOwner() {
		return ownerAttrib.getOwner();
	}
	
	public void setOwner(EntityPlayer player) {
		ownerAttrib.setOwner(player);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
}
