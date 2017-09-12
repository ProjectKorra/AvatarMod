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

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFireball extends AvatarEntity {
	
	public static final DataParameter<FireballBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireball.class, FireballBehavior.DATA_SERIALIZER);

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityFireball.class,
			DataSerializers.VARINT);
	
	private AxisAlignedBB expandedHitbox;
	
	private float damage;
	
	/**
	 * @param world
	 */
	public EntityFireball(World world) {
		super(world);
		setSize(.8f, .8f);
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireballBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		setBehavior((FireballBehavior) getBehavior().onUpdate(this));

		// TODO Temporary fix to avoid extra fireballs
		// Add hook or something
		if (getOwner() == null) {
			setDead();
		}
		
	}

	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		removeStatCtrl();
		setDead();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		spawnExtinguishIndicators();
		return false;
	}
	
	public FireballBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(FireballBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FireballBehavior.PlayerControlled ? getOwner() : null;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}
	
	public void setSize(int size) {
		dataManager.set(SYNC_SIZE, size);
	}
	
	@Override
	public boolean onCollideWithSolid() {
		
		float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;
		explosionSize *= getSize() / 30f;
		boolean destroyObsidian = false;
		
		if (getOwner() != null) {
			AbilityData abilityData = BendingData.get(getOwner())
					.getAbilityData("fireball");
			if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
				destroyObsidian = true;
			}
		}
		
		Explosion explosion = new Explosion(world, this, posX, posY, posZ, explosionSize,
				!world.isRemote, STATS_CONFIG.fireballSettings.damageBlocks);
		if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
			
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			
		}
		
		if (destroyObsidian) {
			for (EnumFacing dir : EnumFacing.values()) {
				BlockPos pos = getPosition().offset(dir);
				if (world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
					world.destroyBlock(pos, true);
				}
			}
		}

		setDead();
		return true;

	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((FireballBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}
	
	public AxisAlignedBB getExpandedHitbox() {
		return this.expandedHitbox;
	}
	
	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(0.35, 0.35, 0.35);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || pass == 1;
	}
	
	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_FIREBALL);
		}
	}

}
