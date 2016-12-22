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

import java.util.List;

import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AvatarEntity extends Entity {
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(AvatarEntity.class,
			DataSerializers.VARINT);
	
	protected boolean putsOutFires;
	
	/**
	 * @param world
	 */
	public AvatarEntity(World world) {
		super(world);
		this.internalVelocity = createInternalVelocity();
		this.internalPosition = new BackedVector(//
				x -> setPosition(x, posY, posZ), //
				y -> setPosition(posX, y, posZ), //
				z -> setPosition(posX, posY, z), //
				() -> posX, () -> posY, () -> posZ);
		this.putsOutFires = false;
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_ID,
				worldObj.isRemote ? -1 : AvatarWorldData.getDataFromWorld(worldObj).nextEntityId());
	}
	
	/**
	 * Get the velocity of this entity in m/s. Changes to this vector will be
	 * reflected in the entity's actual velocity.
	 */
	public Vector velocity() {
		return internalVelocity;
	}
	
	/**
	 * Get the position of this entity. Changes to this vector will be reflected
	 * in the entity's actual position.
	 */
	public Vector position() {
		return internalPosition;
	}
	
	public int getAvId() {
		return dataManager.get(SYNC_ID);
	}
	
	private void setAvId(int id) {
		dataManager.set(SYNC_ID, id);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setAvId(nbt.getInteger("AvId"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("AvId", getAvId());
	}
	
	//@formatter:off
	protected Vector createInternalVelocity() {
		return new BackedVector(
				x -> this.motionX = x / 20,
				y -> this.motionY = y / 20,
				z -> this.motionZ = z / 20,
				() -> this.motionX * 20,
				() -> this.motionY * 20,
				() -> this.motionZ * 20);
	}
	//@formatter:on
	
	/**
	 * Looks up an entity from the world, given its {@link #getAvId() synced id}
	 * . Returns null if not found.
	 */
	public static <T extends AvatarEntity> T lookupEntity(World world, int id) {
		List<AvatarEntity> entities = world.getEntities(AvatarEntity.class, ent -> ent.getAvId() == id);
		return entities.isEmpty() ? null : (T) entities.get(0);
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		collideWithNearbyEntities();
		if (putsOutFires && ticksExisted % 2 == 0) {
			setFire(0);
			for (int x = 0; x <= 1; x++) {
				for (int z = 0; z <= 1; z++) {
					BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
					if (worldObj.getBlockState(pos).getBlock() == Blocks.FIRE) {
						worldObj.setBlockToAir(pos);
						worldObj.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
								SoundCategory.PLAYERS, 1, 1, false);
					}
				}
			}
		}
	}
	
	// copied from EntityLivingBase -- mostly
	protected void collideWithNearbyEntities() {
		List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(),
				ent -> EntitySelectors.<Entity> getTeamCollisionPredicate(this).apply(ent)
						&& canCollideWith(ent));
		
		if (!list.isEmpty()) {
			int i = this.worldObj.getGameRules().getInt("maxEntityCramming");
			
			if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0) {
				int j = 0;
				
				for (int k = 0; k < list.size(); ++k) {
					if (!((Entity) list.get(k)).isRiding()) {
						++j;
					}
				}
				
				if (j > i - 1) {
					this.attackEntityFrom(DamageSource.field_191291_g, 6.0F);
				}
			}
			
			for (int l = 0; l < list.size(); ++l) {
				Entity entity = (Entity) list.get(l);
				entity.applyEntityCollision(this);
			}
		}
	}
	
	protected boolean canCollideWith(Entity entity) {
		return entity instanceof AvatarEntity;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return getEntityBoundingBox();
	}
	
	@Override
	public boolean canBePushed() {
		return true;
	}
	
	@Override
	public boolean canRenderOnFire() {
		return !putsOutFires && super.canRenderOnFire();
	}
	
	@Override
	public void setFire(int seconds) {
		if (!putsOutFires) super.setFire(seconds);
	}
	
}
