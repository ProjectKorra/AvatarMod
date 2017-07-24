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

import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.entity.data.SyncedEntity;
import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AvatarEntity extends Entity {
	
	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(AvatarEntity.class,
			DataSerializers.VARINT);

	private static final DataParameter<Optional<UUID>> SYNC_OWNER = EntityDataManager.createKey
			(AvatarEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private final Vector internalVelocity;
	private final Vector internalPosition;
	
	protected boolean putsOutFires;
	protected boolean flammable;

	private SyncedEntity<EntityLivingBase> ownerRef;

	/**
	 * @param world
	 */
	public AvatarEntity(World world) {
		super(world);

		this.ownerRef = new SyncedEntity<>(this, SYNC_OWNER);

		//@formatter:off
		this.internalVelocity = new BackedVector(
				x -> this.motionX = x / 20,
				y -> this.motionY = y / 20,
				z -> this.motionZ = z / 20,
				() -> this.motionX * 20,
				() -> this.motionY * 20,
				() -> this.motionZ * 20);
		this.internalPosition = new BackedVector(//
				x -> setPosition(x, posY, posZ), //
				y -> setPosition(posX, y, posZ), //
				z -> setPosition(posX, posY, z), //
				() -> posX, () -> posY, () -> posZ);
		//@formatter:on

		this.putsOutFires = false;
		this.flammable = false;

	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_ID,
				world.isRemote ? -1 : AvatarWorldData.getDataFromWorld(world).nextEntityId());
		dataManager.register(SYNC_OWNER, Optional.absent());
	}
	
	/**
	 * Get the "owner", or the creator, of this entity. Most AvatarEntities have
	 * an owner, though some do not.
	 */
	@Nullable
	public EntityLivingBase getOwner() {
		return ownerRef.getEntity();
	}

	/**
	 * Set the owner of the entity. Passing null will cause the entity to have no owner.
	 */
	public void setOwner(@Nullable EntityLivingBase owner) {
		ownerRef.setEntity(owner);
	}

	/**
	 * Get whether an owner is set. Note that {@link #getOwner()} can sometimes return null while
	 * this returns true; that's because there was no owner entity found but the entity still has
	 * its owner set to something that couldn't be found.
	 */
	public boolean hasOwner() {
		return ownerRef.getEntityId() != null;
	}

	/**
	 * Get whoever is currently controlling the movement of this entity, or null
	 * if nobody is controlling it.
	 * <p>
	 * While most AvatarEntities have an {@link #getOwner() owner} during their
	 * whole existence, controlling this entity is only when the bender can
	 * control the movement of the entity. When it is, for example, thrown, the
	 * entity won't be considered "controlled" anymore so this will return null.
	 */
	public EntityLivingBase getController() {
		return null;
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
		ownerRef.readFromNbt(nbt);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("AvId", getAvId());
		ownerRef.writeToNbt(nbt);
	}

	/**
	 * Looks up an entity from the world, given its {@link #getAvId() synced id}
	 * . Returns null if not found.
	 */
	public static <T extends AvatarEntity> T lookupEntity(World world, int id) {
		List<AvatarEntity> entities = world.getEntities(AvatarEntity.class, ent -> ent.getAvId() == id);
		return entities.isEmpty() ? null : (T) entities.get(0);
	}
	
	public static <T extends AvatarEntity> T lookupEntity(World world, Class<T> cls, Predicate<T> predicate) {
		List<Entity> entities = world.loadedEntityList;
		for (Entity ent : entities) {
			if (ent.getClass().isAssignableFrom(cls) && predicate.test((T) ent)) {
				return (T) ent;
			}
		}
		
		return null;
	}
	
	/**
	 * Find the entity controlled by the given player.
	 */
	public static <T extends AvatarEntity> T lookupControlledEntity(World world, Class<T> cls,
			EntityLivingBase controller) {
		List<T> list = world.getEntities(cls, ent -> ent.getController() == controller);
		return list.isEmpty() ? null : list.get(0);
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public boolean canBeAttackedWithItem() {
		return false;
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
					if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
						world.setBlockToAir(pos);
						world.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
								SoundCategory.PLAYERS, 1, 1, false);
					}
				}
			}
		}
		
		if (isCollided) {
			onCollideWithSolid();
		}
		if (inWater) {
			onMajorWaterContact();
		}
		if (world.isRainingAt(getPosition())) {
			onMinorWaterContact();
		}
		
		Vector v = velocity().dividedBy(20);
		move(MoverType.SELF, v.x(), v.y(), v.z());
		
	}
	
	// copied from EntityLivingBase -- mostly
	protected void collideWithNearbyEntities() {
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(),
				EntitySelectors.<Entity>getTeamCollisionPredicate(this));
		
		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt("maxEntityCramming");
			
			if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0) {
				int j = 0;
				
				for (int k = 0; k < list.size(); ++k) {
					if (!list.get(k).isRiding()) {
						++j;
					}
				}
				
				if (j > i - 1) {
					this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
				}
			}
			
			for (int l = 0; l < list.size(); ++l) {
				Entity entity = list.get(l);
				if (canCollideWith(entity)) {
					entity.applyEntityCollision(this);
					onCollideWithEntity(entity);
				}
			}
		}
	}
	
	/**
	 * Dictates whether this entity will be aware of the collision. However, the
	 * other entity will still execute the collision logic.
	 * <p>
	 * This affects the {@link #onCollideWithEntity(Entity)} hook. Also prevents
	 * {@link #applyEntityCollision(Entity) vanilla logic} from occurring which
	 * pushes the entities away.
	 */
	protected boolean canCollideWith(Entity entity) {
		return entity instanceof AvatarEntity;
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		if (canCollideWith(entity)) {
			super.applyEntityCollision(entity);
			onCollideWithEntity(entity);
		}
	}
	
	/**
	 * Called when this AvatarEntity collides with another entity. Not to be
	 * confused with the vanilla {@link #applyEntityCollision(Entity)}, which is
	 * where another entity is pushing this one.
	 */
	protected void onCollideWithEntity(Entity entity) {}
	
	/**
	 * Called when the entity collides with blocks or a wall. Returns whether the entity was
	 * destroyed.
	 */
	public boolean onCollideWithSolid() {
		return false;
	}

	/**
	 * Called when small sources of water hit the entity, such as rain. Larger sources, like
	 * hitting a water block, should be handled in {@link #onMajorWaterContact()}. Returns
	 * whether the entity was destroyed.
	 */
	public boolean onMinorWaterContact() {
		return false;
	}

	/**
	 * Called when the entity comes into contact with large sources of water, like water blocks.
	 * Other sources of wetness, like rain, should be handled in {@link #onMinorWaterContact()}.
	 * Returns whether the entity was destroyed.
	 */
	public boolean onMajorWaterContact() {
		return false;
	}
	
	/**
	 * Break the block at the given position, playing sound/particles, and
	 * dropping item
	 */
	protected void breakBlock(BlockPos position) {
		
		IBlockState blockState = world.getBlockState(position);
		
		Block destroyed = blockState.getBlock();
		SoundEvent sound;
		if (destroyed == Blocks.FIRE) {
			sound = SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE;
		} else {
			sound = destroyed.getSoundType().getBreakSound();
		}
		world.playSound(null, position, sound, SoundCategory.BLOCKS, 1, 1);
		
		// Spawn particles
		
		for (int i = 0; i < 7; i++) {
			world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY, posZ,
					3 * (rand.nextGaussian() - 0.5), rand.nextGaussian() * 2 + 1,
					3 * (rand.nextGaussian() - 0.5), Block.getStateId(blockState));
		}
		world.setBlockToAir(position);
		
		// Create drops
		
		if (!world.isRemote) {
			List<ItemStack> drops = blockState.getBlock().getDrops(world, position, blockState, 0);
			for (ItemStack stack : drops) {
				EntityItem item = new EntityItem(world, posX, posY, posZ, stack);
				item.setDefaultPickupDelay();
				item.motionX *= 2;
				item.motionY *= 1.2;
				item.motionZ *= 2;
				world.spawnEntity(item);
			}
		}
		
	}

	/**
	 * Spawns smoke particles and plays sounds to indicate that the entity is being extinguished
	 */
	protected void spawnExtinguishIndicators() {
		int particles = rand.nextInt(4) + 5;
		for (int i = 0; i < particles; i++) {
			world.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, rand.nextGaussian() * .05,
					rand.nextDouble() * .2, rand.nextGaussian() * .05);
		}
		world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
				SoundCategory.PLAYERS, 1, rand.nextFloat() * 0.3f + 1.1f, false);
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
		return !putsOutFires && flammable && super.canRenderOnFire();
	}
	
	@Override
	public void setFire(int seconds) {
		if (!putsOutFires && flammable) super.setFire(seconds);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return super.shouldRenderInPass(pass);
	}
	
	// disable stepping sounds
	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {}
	
}
