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

package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.util.data.AvatarWorldData;
import com.crowsofwar.avatar.entity.data.SyncedEntity;
import com.crowsofwar.avatar.client.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static net.minecraft.block.BlockTNT.EXPLODE;

/**
 * @author CrowsOfWar
 */
public abstract class AvatarEntity extends Entity {

	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(AvatarEntity.class,
			DataSerializers.VARINT);

	private static final DataParameter<Optional<UUID>> SYNC_OWNER = EntityDataManager.createKey
			(AvatarEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private static final DataParameter<String> SYNC_ABILITY = EntityDataManager.createKey(AvatarEntity.class,
			DataSerializers.STRING);


	protected boolean putsOutFires, flammable;
	protected boolean pushStoneButton, pushTrapDoor, pushDoor, pushRedstone;
	protected boolean setsFires, lightTnt;
	private double powerRating;
	private BendingStyle element;
	private int tier;
	private final SyncedEntity<EntityLivingBase> ownerRef;
	private BlockPos prevLeverPos = null, prevDoorPos = null, prevTrapdoorPos = null,
			prevButtonPos = null, prevGatePos = null;
	private IBlockState prevLeverState = null, prevDoorState = null, prevTrapdoorState = null, prevButtonState = null, prevGateState = null;

	/**
	 * @param world
	 */
	public AvatarEntity(World world) {
		super(world);

		this.ownerRef = new SyncedEntity<>(this, SYNC_OWNER);
		this.putsOutFires = false;
		this.flammable = false;
		this.element = null;
		this.setsFires = false;
		this.lightTnt = false;
		this.tier = 1;
		this.pushRedstone = false;
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

	/**
	 * Find the entity owned by the given entity.
	 */
	public static <T extends AvatarEntity> T lookupOwnedEntity(World world, Class<T> cls,
															   EntityLivingBase owner) {
		List<T> list = world.getEntities(cls, ent -> ent.getOwner() == owner);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	protected void entityInit() {
		dataManager.register(SYNC_ID,
				world.isRemote ? -1 : AvatarWorldData.getDataFromWorld(world).nextEntityId());
		dataManager.register(SYNC_OWNER, Optional.absent());
		dataManager.register(SYNC_ABILITY, "earth_control");
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
	 * Get the velocity of this entity in m/s.
	 */
	public Vector velocity() {
		return Vector.getVelocity(this);
	}

	public void addVelocity(Vector velocity) {
		motionX += velocity.x() / 20;
		motionY += velocity.y() / 20;
		motionZ += velocity.z() / 20;
	}

	public void addVelocity(Vec3d velocity) {
		motionX += velocity.x;
		motionY += velocity.y;
		motionZ += velocity.z;
	}

	public Vec3d getVelocity() {
		return new Vec3d(motionX, motionY, motionZ);
	}

	public void setVelocity(Vector velocity) {
		motionX = velocity.x() / 20;
		motionY = velocity.y() / 20;
		motionZ = velocity.z() / 20;
	}

	public void setVelocity(Vec3d velocity) {
		motionX = velocity.x;
		motionY = velocity.y;
		motionZ = velocity.z;
	}

	/**
	 * Get the position of this entity. Changes to this vector will be reflected
	 * in the entity's actual position.
	 */
	public Vector position() {
		return Vector.getEntityPos(this);
	}

	public void setPosition(Vector position) {
		setPosition(position.x(), position.y(), position.z());
	}

	public void setPosition(Vec3d position) {
		setPosition(position.x, position.y, position.z);
	}

	public int getAvId() {
		return dataManager.get(SYNC_ID);
	}

	private void setAvId(int id) {
		dataManager.set(SYNC_ID, id);
	}

	public double getPowerRating() {
		return powerRating;
	}

	//why
	public void setPowerRating(double powerRating) {
		this.powerRating = powerRating;
	}

	public Ability getAbility() {
		return Abilities.get(dataManager.get(SYNC_ABILITY));
	}

	public void setAbility(Ability ability) {
		dataManager.set(SYNC_ABILITY, ability.getName());
	}

	public void setPushRedstone(boolean push) {
		this.pushRedstone = push;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setAvId(nbt.getInteger("AvId"));
		pushStoneButton = nbt.getBoolean("PushStoneButton");
		pushTrapDoor = nbt.getBoolean("PushIronTrapDoor");
		pushDoor = nbt.getBoolean("PushIronDoor");
		tier = nbt.getInteger("Tier");
		ownerRef.readFromNbt(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("AvId", getAvId());
		nbt.setBoolean("PushIronDor", pushDoor);
		nbt.setBoolean("PushIronTrapDoor", pushTrapDoor);
		nbt.setBoolean("PushStoneButton", pushStoneButton);
		nbt.setInteger("Tier", tier);
		ownerRef.writeToNbt(nbt);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	public void Extinguish() {
		setFire(0);
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				for (int y = 0; y <= 1; y++) {
					double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
					double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
					double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
					BlockPos pos = new BlockPos(xPos + x * width / 2, yPos + y * height / 2, zPos + z * width / 2);
					if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
						world.setBlockToAir(pos);
						world.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
								SoundCategory.PLAYERS, 1, 1, false);
					}
					else if (world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
						Block lava = world.getBlockState(pos).getBlock();
						if (lava == Blocks.LAVA)
							world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
						else if (lava == Blocks.FLOWING_LAVA)
							world.setBlockState(pos, Blocks.STONE.getDefaultState());
					}
				}
			}
		}
		for (int x = 0; x >= -1; x--) {
			for (int z = 0; z >= -1; z--) {
				for (int y = 0; y >= -1; y--) {
					double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
					double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
					double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
					BlockPos pos = new BlockPos(xPos + x * width / 2, yPos + y * height / 2, zPos + z * width / 2);
					if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
						world.setBlockToAir(pos);
						world.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
								SoundCategory.PLAYERS, 1, 1, false);
					}
					else if (world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
						Block lava = world.getBlockState(pos).getBlock();
						if (lava == Blocks.LAVA)
							world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
						else if (lava == Blocks.FLOWING_LAVA)
							world.setBlockState(pos, Blocks.STONE.getDefaultState());
					}
				}
			}
		}
	}

	public void setFires() {
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				for (int y = 0; y <= 1; y++) {
					BlockPos pos = new BlockPos(posX + x * width, posY + y * height, posZ + z * width);
					if (Blocks.FIRE.canPlaceBlockAt(world, pos) && world.getBlockState(pos).getBlock() == Blocks.AIR) {
						world.setBlockState(pos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
		for (int x = 0; x >= -1; x--) {
			for (int z = 0; z >= -1; z--) {
				for (int y = 0; y >= -1; y--) {
					BlockPos pos = new BlockPos(posX + x * width, posY + y * height, posZ + z * width);
					if (Blocks.FIRE.canPlaceBlockAt(world, pos) && world.getBlockState(pos).getBlock() == Blocks.AIR) {
						world.setBlockState(pos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
		if (lightTnt) {
			for (int x = 0; x <= 1; x++) {
				for (int z = 0; z <= 1; z++) {
					for (int y = 0; y <= 1; y++) {
						BlockPos pos = new BlockPos(posX + x * width, posY + y * height, posZ + z * width);
						if (world.getBlockState(pos).getBlock() == Blocks.TNT) {
							((BlockTNT) world.getBlockState(pos).getBlock()).explode(world, pos,
									world.getBlockState(pos).withProperty(EXPLODE, true), getOwner());
						}
					}
				}
			}
			for (int x = 0; x >= -1; x--) {
				for (int z = 0; z >= -1; z--) {
					for (int y = 0; y >= -1; y--) {
						BlockPos pos = new BlockPos(posX + x * width, posY + y * height, posZ + z * width);
						if (world.getBlockState(pos).getBlock() == Blocks.TNT) {
							((BlockTNT) world.getBlockState(pos).getBlock()).explode(world, pos,
									world.getBlockState(pos).withProperty(EXPLODE, true), getOwner());
						}
					}
				}
			}
		}
	}

	public boolean pushLevers(BlockPos pos) {
		if (pushLever()) {
			if (pos != prevLeverPos && prevLeverState != world.getBlockState(pos) || prevLeverPos == null) {
				if (AvatarUtils.pushLever(world, pos)) {
					prevLeverPos = pos;
					prevLeverState = world.getBlockState(pos);
					return true;
				}
			}
		}
		return false;
	}

	public boolean pushButtons(BlockPos pos) {
		if (pushButton(pushStoneButton)) {
			if (pos != prevButtonPos && prevButtonState != world.getBlockState(pos) || prevButtonState == null) {
				if ((AvatarUtils.pushButton(world, pushStoneButton, pos))) {
					prevButtonPos = pos;
					prevButtonState = world.getBlockState(pos);
					return true;
				}
			}
		}
		return false;
	}

	public boolean pushTrapDoors(BlockPos pos) {
		if (pushTrapdoor(pushTrapDoor)) {
			if (pos != prevTrapdoorPos && prevTrapdoorState != world.getBlockState(pos) || prevTrapdoorState == null) {
				if (AvatarUtils.pushTrapDoor(world, pushTrapDoor, pos)) {
					prevTrapdoorPos = pos;
					prevTrapdoorState = world.getBlockState(pos);
					return true;
				}
			}
		}
		return false;
	}

	public boolean pushDoors(BlockPos pos) {
		if (pushDoor(pushDoor)) {
			if (pos != prevDoorPos && prevDoorState != world.getBlockState(pos) || prevDoorState == null) {
				if (AvatarUtils.pushDoor(this, pushDoor, pos)) {
					prevDoorPos = pos;
					prevLeverState = world.getBlockState(pos);
					return true;
				}
			}
		}
		return false;
	}

	public boolean pushGates(BlockPos pos) {
		if (pushGate()) {
			if (pos != prevGatePos && prevGateState != world.getBlockState(pos) || prevGateState == null) {
				if (AvatarUtils.pushGate(this, pos)) {
					prevGatePos = pos;
					prevGateState = world.getBlockState(pos);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		collideWithNearbyEntities();

		if (putsOutFires && ticksExisted % 2 == 0)
			Extinguish();


		//Prev states are used to make sure each button isn't activated twice
		for (double x = 0; x <= 1; x++) {
			for (double z = 0; z <= 1; z++) {
				for (double y = 0; y <= 1; y++) {
					double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
					double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
					double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
					BlockPos pos = new BlockPos(xPos + x * width / 2, yPos + y * height / 2, zPos + z * width / 2);
					pushLevers(pos);
					pushTrapDoors(pos);
					pushButtons(pos);
					pushDoors(pos);
					pushGates(pos);
				}
			}
		}
		for (double x = 0; x >= -1; x--) {
			for (double z = 0; z >= -1; z--) {
				for (double y = 0; y >= -1; y--) {
					double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
					double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
					double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
					BlockPos pos = new BlockPos(xPos + x * width / 2, yPos + y * height / 2, zPos + z * width / 2);
					pushLevers(pos);
					pushTrapDoors(pos);
					pushButtons(pos);
					pushDoors(pos);
					pushGates(pos);
				}
			}
		}

		if (getOwner() == null) {
			this.setDead();
		}

		if (collided) {
			if (setsFires)
				setFires();
			onCollideWithSolid();
		}
		if (inWater) {
			onMajorWaterContact();
		}
		if (world.isRainingAt(getPosition())) {
			onMinorWaterContact();
		}
		if (world.isFlammableWithin(this.getEntityBoundingBox().shrink(0.001D))) {
			onFireContact();
		}

		//Breaks cobwebs
		IBlockState inBlock = world.getBlockState(getPosition());
		if (inBlock.getBlock() == Blocks.WEB && !inBlock.isFullBlock()) {
			breakBlock(getPosition());
		}

		Vector v = velocity().dividedBy(20);
		move(MoverType.SELF, v.x(), v.y(), v.z());


	}

	// copied from EntityLivingBase -- mostly
	protected void collideWithNearbyEntities() {
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this,
				this.getEntityBoundingBox());

		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt("maxEntityCramming");

			if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0) {
				int j = 0;

				for (Entity aList : list) {
					if (!aList.isRiding()) {
						if (canDamageEntity(aList)) {
							++j;
						}
					}
				}

				if (j > i - 1) {
					this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
				}
			}

			for (Entity entity : list) {
				if (canCollideWith(entity) && entity != getOwner()) {
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
	public boolean canCollideWith(Entity entity) {
		if (entity == getOwner()) {
			return false;
		} else if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() == getOwner()) {
			return false;
		} else if (entity instanceof EntityLivingBase && entity.getControllingPassenger() == getOwner()) {
			return false;
		} else if (getOwner() != null && getOwner().getTeam() != null && entity.getTeam() == getOwner().getTeam()) {
			return false;
		} else if (entity instanceof EntityEnderCrystal) {
			return true;
		} else if (entity == this) {
			return false;
		} else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).hurtTime > 0)
			return false;
		else
			return (entity.canBePushed() && entity.canBeCollidedWith()) || entity instanceof EntityLivingBase || entity instanceof AvatarEntity;
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
	public void onCollideWithEntity(Entity entity) {
		if (putsOutFires)
			entity.extinguish();
	}

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
	 * Called when a source of fire, a fire block itself, hits the entity. Returns whether the
	 * entity was destroyed.
	 */
	public boolean onFireContact() {
		return false;
	}

	/**
	 * Called when a lightning bolt or source of lightning hits the enitty
	 */
	public boolean onLightningContact() {
		return false;
	}

	/**
	 * Called when an airbending entity (such as an air gust) hits the entity. Returns whether
	 * the entity was destroyed.
	 */
	public boolean onAirContact() {
		return false;
	}

	/**
	 * Returns true if another AvatarEntity can push this one.
	 */
	public boolean canPush() {
		return true;
	}

	/**
	 * Returns whether the entity the avatar entity collided with can be damaged- useful for preventing crashes.
	 * Ex: You can collide with an armour stand, but you can't damage it.
	 */


	public boolean canDamageEntity(Entity entity) {
		return canCollideWith(entity) && entity.canBeAttackedWithItem() || entity instanceof EntityEnderCrystal;
	}

	/**
	 * Returns whether the entity can be considered a projectile. Generally, an entity is
	 * considered a projectile if it is meant to be thrown or flung some distance. Air entities
	 * can be projectiles as well.
	 */
	public boolean isProjectile() {
		return false;
	}

	/**
	 * Returns whether the entity can be considered a shield. Generally,
	 * an entity is considered a shield if it blocks other entities.
	 * Air Bubble and Wall are both shields.
	 */
	public boolean isShield() {
		return false;
	}

	public boolean pushButton(boolean pushStone) {
		return pushRedstone;
	}

	public boolean pushLever() {
		return pushRedstone;
	}

	public boolean pushTrapdoor(boolean pushIron) {
		return pushRedstone;
	}

	public boolean pushDoor(boolean pushIron) {
		return pushRedstone;
	}

	public boolean pushGate() {
		return pushRedstone;
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

		ParticleSpawner particleSpawner;
		if (world.isRemote) {
			particleSpawner = new ClientParticleSpawner();
		} else {
			particleSpawner = new NetworkParticleSpawner();
		}
		particleSpawner.spawnParticles(world, EnumParticleTypes.CLOUD, 4, 8, posX, posY, posZ,
				0.05, 0.2, 0.05, true);

		world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
				SoundCategory.PLAYERS, 1, rand.nextFloat() * 0.3f + 1.1f);

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
	protected void playStepSound(BlockPos pos, Block blockIn) {
	}

	//Used to determine what element the entity is
	public BendingStyle getElement() {
		return element;
	}

	public void setElement(BendingStyle element) {
		this.element = element;
	}

	//Used to determine what the tier of the entity is. Useful for better collision.
	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

}
