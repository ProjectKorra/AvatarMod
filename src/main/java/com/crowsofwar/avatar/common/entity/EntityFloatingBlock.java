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

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;
import static net.minecraft.network.datasync.EntityDataManager.createKey;

public class EntityFloatingBlock extends AvatarEntity {

	public static final Block DEFAULT_BLOCK = Blocks.STONE;

	private static final DataParameter<Integer> SYNC_ENTITY_ID = createKey(EntityFloatingBlock.class,
			DataSerializers.VARINT);
	private static final DataParameter<Vector> SYNC_VELOCITY = createKey(EntityFloatingBlock.class,
			AvatarDataSerializers.SERIALIZER_VECTOR);
	private static final DataParameter<Float> SYNC_FRICTION = createKey(EntityFloatingBlock.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Optional<IBlockState>> SYNC_BLOCK = createKey(
			EntityFloatingBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);

	private static final DataParameter<FloatingBlockBehavior> SYNC_BEHAVIOR = createKey(
			EntityFloatingBlock.class, FloatingBlockBehavior.DATA_SERIALIZER);

	private static int nextBlockID = 0;

	/**
	 * Cached owner of this floating block. May not be accurate- use
	 * {@link #getOwner()} to use updated version.
	 */
	private EntityPlayer ownerCached;

	/**
	 * Whether or not to drop an ItemBlock when the floating block has been
	 * destroyed. Does not matter on client.
	 */
	private boolean enableItemDrops;

	/**
	 * The hitbox for this floating block, but slightly expanded to give more
	 * room for killing things with.
	 */
	private AxisAlignedBB expandedHitbox;

	private float damageMult;

	public EntityFloatingBlock(World world) {
		super(world);
		float size = .9f;
		setSize(size, size);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			setID(nextBlockID++);
		}
		this.enableItemDrops = true;
		this.damageMult = 1;

	}

	public EntityFloatingBlock(World world, IBlockState blockState) {
		this(world);
		setBlockState(blockState);
	}

	public EntityFloatingBlock(World world, IBlockState blockState, EntityPlayer owner) {
		this(world, blockState);
		setOwner(owner);
	}

	public static EntityFloatingBlock getFromID(World world, int id) {
		for (int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity e = world.loadedEntityList.get(i);
			if (e instanceof EntityFloatingBlock && ((EntityFloatingBlock) e).getID() == id)
				return (EntityFloatingBlock) e;
		}
		return null;
	}

	// Called from constructor of Entity class
	@Override
	protected void entityInit() {

		super.entityInit();
		dataManager.register(SYNC_ENTITY_ID, 0);
		dataManager.register(SYNC_VELOCITY, Vector.ZERO);
		dataManager.register(SYNC_FRICTION, 1f);
		dataManager.register(SYNC_BLOCK, Optional.of(DEFAULT_BLOCK.getDefaultState()));
		dataManager.register(SYNC_BEHAVIOR, new FloatingBlockBehavior.DoNothing());

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setBlockState(
				Block.getBlockById(nbt.getInteger("BlockId")).getStateFromMeta(nbt.getInteger("Metadata")));
		setVelocity(new Vector(nbt.getDouble("VelocityX"), nbt.getDouble("VelocityY"), nbt.getDouble
				("VelocityZ")));
		setFriction(nbt.getFloat("Friction"));
		setItemDropsEnabled(nbt.getBoolean("DropItems"));
		setBehavior((FloatingBlockBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
		getBehavior().load(nbt.getCompoundTag("BehaviorData"));
		damageMult = nbt.getFloat("DamageMultiplier");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("BlockId", Block.getIdFromBlock(getBlock()));
		nbt.setInteger("Metadata", getBlock().getMetaFromState(getBlockState()));
		nbt.setDouble("VelocityX", velocity().x());
		nbt.setDouble("VelocityY", velocity().y());
		nbt.setDouble("VelocityZ", velocity().z());
		nbt.setFloat("Friction", getFriction());
		nbt.setBoolean("DropItems", areItemDropsEnabled());
		nbt.setInteger("Behavior", getBehavior().getId());
		getBehavior().save(nestedCompound(nbt, "BehaviorData"));
		nbt.setFloat("DamageMultiplier", damageMult);
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	public Block getBlock() {
		return getBlockState().getBlock();
	}

	public void setBlock(Block block) {
		setBlockState(block.getDefaultState());
	}

	public IBlockState getBlockState() {
		Optional<IBlockState> obs = dataManager.get(SYNC_BLOCK);
		return obs.get();
	}

	public void setBlockState(IBlockState state) {
		dataManager.set(SYNC_BLOCK, Optional.of(state));
	}

	/**
	 * Get the ID of this floating block. Each instance has its own unique ID.
	 * Synced between client and server.
	 */
	public int getID() {
		return dataManager.get(SYNC_ENTITY_ID);
	}

	public void setID(int id) {
		if (!world.isRemote) dataManager.set(SYNC_ENTITY_ID, id);
	}

	/**
	 * Returns whether the floating block drops the block as an item when it is
	 * destroyed. Only used on server-side. By default, is true.
	 */
	public boolean areItemDropsEnabled() {
		return enableItemDrops;
	}

	/**
	 * Set whether the block should be dropped when it is destroyed.
	 */
	public void setItemDropsEnabled(boolean enable) {
		this.enableItemDrops = enable;
	}

	public float getDamageMult() {
		return damageMult;
	}

	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}

	private void spawnCrackParticle(double x, double y, double z, double mx, double my, double mz) {
		world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, x, y, z, mx, my, mz,
				Block.getStateId(getBlockState()));
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		extinguish();

		if (ticksExisted == 1) {

			for (int i = 0; i < 10; i++) {
				double spawnX = posX + (rand.nextDouble() - 0.5);
				double spawnY = posY - 0;
				double spawnZ = posZ + (rand.nextDouble() - 0.5);
				spawnCrackParticle(spawnX, spawnY, spawnZ, 0, -0.1, 0);
			}

		}

		setVelocity(velocity().times(getFriction()));

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		FloatingBlockBehavior nextBehavior = (FloatingBlockBehavior) getBehavior().onUpdate(this);
		if (nextBehavior != getBehavior()) setBehavior(nextBehavior);

	}

	@Override
	public boolean onCollideWithSolid() {

		FloatingBlockBehavior behavior = getBehavior();
		if (!(behavior instanceof FloatingBlockBehavior.Fall || behavior instanceof
				FloatingBlockBehavior.Thrown)) {

			return false;

		}

		// Spawn particles
		Random random = new Random();
		for (int i = 0; i < 7; i++) {
			spawnCrackParticle(posX, posY + 0.3, posZ, random.nextGaussian() * 0.1,
					random.nextGaussian() * 0.1, random.nextGaussian() * 0.1);
		}
		AbilityData data = BendingData.get(getOwner()).getAbilityData("pickup_block");

		if (!world.isRemote && areItemDropsEnabled()) {
			List<ItemStack> drops = getBlock().getDrops(world, new BlockPos(this), getBlockState(), 0);
			for (ItemStack is : drops) {
				EntityItem ei = new EntityItem(world, posX, posY, posZ, is);
				world.spawnEntity(ei);
			}
		}


		if (data.isMasterPath(AbilityTreePath.SECOND) && rand.nextBoolean()) {

			Explosion explosion = new Explosion(world, this, posX, posY, posZ, 2, false, false);
			if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
				explosion.doExplosionA();
				explosion.doExplosionB(true);
			}

		}

		return true;

	}

	@Override
	public void setDead() {
		super.setDead();
		if (this.isDead && !world.isRemote) {
			Thread.dumpStack();
		}
	}

	public float getFriction() {
		return dataManager.get(SYNC_FRICTION);
	}

	public void setFriction(float friction) {
		if (!world.isRemote) dataManager.set(SYNC_FRICTION, friction);
	}

	public void drop() {
		setBehavior(new FloatingBlockBehavior.Fall());
	}

	public FloatingBlockBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(FloatingBlockBehavior behavior) {
		// FIXME research: why doesn't sync_Behavior cause an update to client?
		if (behavior == null) throw new IllegalArgumentException("Cannot have null behavior");
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FloatingBlockBehavior.PlayerControlled ? getOwner() : null;
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
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double d) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}
