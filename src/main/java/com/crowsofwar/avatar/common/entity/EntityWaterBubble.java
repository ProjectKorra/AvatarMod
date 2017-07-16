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

import static com.crowsofwar.avatar.common.bending.StatusControl.THROW_BUBBLE;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
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
	private static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityWaterBubble.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private final OwnerAttribute ownerAttrib;
	
	/**
	 * Whether the water bubble will get a water source upon landing. Only
	 * set on server-side.
	 */
	private boolean sourceBlock;
	
	/**
	 * @param world
	 */
	public EntityWaterBubble(World world) {
		super(world);
		this.ownerAttrib = new OwnerAttribute(this, SYNC_OWNER);
		setSize(.8f, .8f);
		this.putsOutFires = true;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new WaterBubbleBehavior.Drop());
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		velocity().mul(0.9);
		
		WaterBubbleBehavior currentBehavior = getBehavior();
		WaterBubbleBehavior nextBehavior = (WaterBubbleBehavior) currentBehavior.onUpdate(this);
		if (currentBehavior != nextBehavior) setBehavior(nextBehavior);
		
		if (ticksExisted % 5 == 0) {
			BlockPos down = getPosition().down();
			IBlockState downState = world.getBlockState(down);
			if (downState.getBlock() == Blocks.FARMLAND) {
				int moisture = downState.getValue(BlockFarmland.MOISTURE);
				if (moisture < 7) world.setBlockState(down,
						Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, moisture + 1));
			}
		}
		
		boolean inWaterSource = false;
		if (!world.isRemote && ticksExisted % 2 == 1 && ticksExisted > 10) {
			for (int x = 0; x <= 1; x++) {
				for (int z = 0; z <= 1; z++) {
					BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
					IBlockState state = world.getBlockState(pos);
					if (state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0) {
						inWaterSource = true;
						break;
					}
				}
			}
		}
		
		if (!world.isRemote && inWaterSource) {
			setDead();
			if (getOwner() != null) {
				BendingData data = Bender.get(getOwner()).getData();
				if (data != null) {
					data.removeStatusControl(StatusControl.THROW_BUBBLE);
				}
			}
		}
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		ownerAttrib.load(compound);
		setBehavior((WaterBubbleBehavior) Behavior.lookup(compound.getInteger("Behavior"), this));
		getBehavior().load(compound);
		setSourceBlock(compound.getBoolean("SourceBlock"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		ownerAttrib.save(compound);
		compound.setInteger("Behavior", getBehavior().getId());
		getBehavior().save(compound);
		compound.setBoolean("SourceBlock", sourceBlock);
	}
	
	public WaterBubbleBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WaterBubbleBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	@Override
	public EntityLivingBase getOwner() {
		return ownerAttrib.getOwner();
	}
	
	public void setOwner(EntityLivingBase player) {
		ownerAttrib.setOwner(player);
	}
	
	public boolean isSourceBlock() {
		return sourceBlock;
	}
	
	public void setSourceBlock(boolean sourceBlock) {
		this.sourceBlock = sourceBlock;
	}
	
	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof WaterBubbleBehavior.PlayerControlled ? getOwner() : null;
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
	@Override
	public boolean tryDestroy() {
		setBehavior(new WaterBubbleBehavior.Drop());
		if (getOwner() != null) {
			Bender.get(getOwner()).getData().removeStatusControl(THROW_BUBBLE);
		}
		return false;
	}
	
}
