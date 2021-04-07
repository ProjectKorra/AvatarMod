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

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.util.AvatarDataSerializers;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.PLACE_BLOCK;
import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_BLOCK;
import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;
import static net.minecraft.network.datasync.EntityDataManager.createKey;

public class EntityFloatingBlock extends EntityOffensive {

    public static final Block DEFAULT_BLOCK = Blocks.STONE;

    private static final DataParameter<Integer> SYNC_ENTITY_ID = createKey(EntityFloatingBlock.class,
            DataSerializers.VARINT);
    private static final DataParameter<Vector> SYNC_VELOCITY = createKey(EntityFloatingBlock.class,
            AvatarDataSerializers.SERIALIZER_VECTOR);
    private static final DataParameter<Float> SYNC_FRICTION = createKey(EntityFloatingBlock.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Optional<IBlockState>> SYNC_BLOCK = createKey(
            EntityFloatingBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    private static final DataParameter<Integer> SYNC_HITS_LEFT = createKey(EntityFloatingBlock.class,
            DataSerializers.VARINT);
    private static final DataParameter<FloatingBlockBehavior> SYNC_BEHAVIOR = createKey(
            EntityFloatingBlock.class, FloatingBlockBehavior.DATA_SERIALIZER);
    private static final DataParameter<Boolean> SYNC_BOOMERANG = createKey(
            EntityFloatingBlock.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_TURN_SOLID = createKey(
            EntityFloatingBlock.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_EXPLOSION = createKey(
            EntityFloatingBlock.class, DataSerializers.BOOLEAN);


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
    private int ticksGround;
    private int ticksPlaced;

    public EntityFloatingBlock(World world) {
        super(world);
        float size = 0.9f;
        setEntitySize(size, size);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            setID(nextBlockID++);
        }
        this.enableItemDrops = true;
        this.damageMult = 1;
        this.ticksGround = 0;
        this.width = size;
        this.height = size;
        this.ignoreFrustumCheck = true;
        this.ticksPlaced = 0;
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
        dataManager.register(SYNC_BEHAVIOR, new FloatingBlockBehavior.Idle());
        dataManager.register(SYNC_HITS_LEFT, 3);
        dataManager.register(SYNC_BOOMERANG, false);
        dataManager.register(SYNC_TURN_SOLID, false);
        dataManager.register(SYNC_EXPLOSION, false);

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setBlockState(
                Block.getBlockById(nbt.getInteger("BlockId")).getStateFromMeta(nbt.getInteger("Metadata")));
        setFriction(nbt.getFloat("Friction"));
        setItemDropsEnabled(nbt.getBoolean("DropItems"));
        setBehavior((FloatingBlockBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
        getBehavior().load(nbt.getCompoundTag("BehaviorData"));
        setBoomerang(nbt.getBoolean("Boomerang"));
        setHitsLeft(nbt.getInteger("HitsLeft"));
        damageMult = nbt.getFloat("DamageMultiplier");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("BlockId", Block.getIdFromBlock(getBlock()));
        nbt.setInteger("Metadata", getBlock().getMetaFromState(getBlockState()));
        nbt.setFloat("Friction", getFriction());
        nbt.setBoolean("DropItems", areItemDropsEnabled());
        nbt.setInteger("Behavior", getBehavior().getId());
        getBehavior().save(nestedCompound(nbt, "BehaviorData"));
        nbt.setBoolean("Boomerang", shouldBoomerang());
        nbt.setInteger("HitsLeft", getHitsLeft());
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

    public void setExplosion(boolean explosion) {
        this.noClip = explosion;
        dataManager.set(SYNC_EXPLOSION, explosion);
    }

    public boolean doesExplode() {
        return dataManager.get(SYNC_EXPLOSION);
    }

    public void setTurnSolid(boolean solid) {
        dataManager.set(SYNC_TURN_SOLID, solid);
    }

    public boolean shouldTurnSolid() {
        return dataManager.get(SYNC_TURN_SOLID);
    }

    public void setBoomerang(boolean boomerang) {
        dataManager.set(SYNC_BOOMERANG, boomerang);
    }

    public boolean shouldBoomerang() {
        return dataManager.get(SYNC_BOOMERANG);
    }

    public int getHitsLeft() {
        return dataManager.get(SYNC_HITS_LEFT);
    }

    public void setHitsLeft(int hits) {
        dataManager.set(SYNC_HITS_LEFT, hits);
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

    public boolean placeBlock() {
        if (!STATS_CONFIG.preventEarthGriefing) {
            Vec3d middle = AvatarEntityUtils.getBottomMiddleOfEntity(this);
            BlockPos pos = new BlockPos(middle.x, middle.y, middle.z);
            BlockPos pos2 = new BlockPos(middle.x, middle.y, middle.z).down();
            //Using getPosition sometimes results in weird stuff
            return world.setBlockState(pos, getBlockState()) || world.setBlockState(pos2, getBlockState());
        }
        return true;
    }

    @Override
    public void onUpdate() {

        super.onUpdate();
        setBehavior((FloatingBlockBehavior) getBehavior().onUpdate(this));

        extinguish();

        if ((onGround || doesExplode() && onCollideWithSolid()) && getBehavior() instanceof FloatingBlockBehavior.Thrown)
            ticksGround++;

        if ((ticksGround > 1 || velocity().magnitude() < 10) && doesExplode() && onCollideWithSolid())
            Explode();

        if (ticksGround > 13 && velocity().magnitude() < 0.5) {
            setPosition(getPositionVector());
            placeBlock();
            world.scheduleBlockUpdate(getPosition(), getBlock(), 0, 1);
        }

        if (getBehavior() instanceof FloatingBlockBehavior.Place) {
            ticksPlaced++;
        }

        if (ticksPlaced > 60)
            if (!placeBlock())
                Dissipate();

        if (ticksGround > 17 && velocity().magnitude() < 0.5)
            Dissipate();

        if (ticksExisted == 1) {
            for (int i = 0; i < 10; i++) {
                double spawnX = posX + (rand.nextDouble() - 0.5);
                double spawnY = posY - 0;
                double spawnZ = posZ + (rand.nextDouble() - 0.5);
                spawnCrackParticle(spawnX, spawnY, spawnZ, 0, -0.1, 0);
            }
        }

        if (getBehavior() != null && getBehavior() instanceof FloatingBlockBehavior.Thrown) {
            if (onGround)
                setVelocity(velocity().times(getFriction() / 1.25));
            else setVelocity(velocity().times(getFriction()));
        }

    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        return new SoundEvent[]{
                world.getBlockState(getPosition().down()).getBlock().getSoundType().getBreakSound()
        };
    }

    @Override
    public void playPiercingSounds(Entity entity) {

    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);

        if (canCollideWith(entity)) {
            if (shouldBoomerang() && getOwner() != null) {
                setBehavior(new FloatingBlockBehavior.PlayerControlled());
                Vec3d forward = getOwner().getLook(1.0F);
                Vec3d eye = getOwner().getPositionEyes(1.0F);
                Vec3d target = forward.scale(2.5).add(eye);
                Vec3d motion = target.subtract(entity.getPositionVector()).scale(0.05);
                setVelocity(motion);
            }
            setHitsLeft(getHitsLeft() - 1);
        }

    }

    @Override
    public int getFireTime() {
        return 0;
    }

    @Override
    public boolean onCollideWithSolid() {

        if (super.onCollideWithSolid() && getBehavior() instanceof FloatingBlockBehavior.Thrown)
            setPosition(getPositionVector());


        if (getBehavior() instanceof FloatingBlockBehavior.Fall)
            placeBlock();


        boolean collide = getBehavior() instanceof FloatingBlockBehavior.Thrown || getBehavior() instanceof FloatingBlockBehavior.Fall && velocity().magnitude() < 0.5;
        return super.onCollideWithSolid() && collide;

    }


    @Override
    public boolean shouldDissipate() {
        return (getHitsLeft() == 0 || !shouldTurnSolid()) && getBehavior() instanceof FloatingBlockBehavior.Thrown && !doesExplode();
    }

    @Override
    public boolean shouldExplode() {
        return false;
    }

    @Override
    public boolean isPiercing() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return getBehavior() instanceof FloatingBlockBehavior.Thrown;
    }


    @Override
    public void Dissipate() {
        super.Dissipate();
        if (!shouldTurnSolid() && areItemDropsEnabled()) {
            EntityItem entity = new EntityItem(world, posX, posY, posZ, new ItemStack(Item.getItemFromBlock(getBlock()), 1));
            entity.setDefaultPickupDelay();
            if (getOwner() != null)
                entity.setOwner(getOwner().getName());
            if (!world.isRemote)
                world.spawnEntity(entity);
        }
    }

    @Override
    public boolean canBePushed() {
        return !doesExplode();
    }


    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnExplosionParticles(World world, Vec3d pos) {
        if (world.isRemote)
            for (int i = 0; i < 50; i++)
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + world.rand.nextGaussian() * 0.75,
                        posY + world.rand.nextGaussian() * 0.875, posZ + world.rand.nextGaussian() * 0.75,
                        world.rand.nextGaussian() * 0.75, world.rand.nextDouble() * 0.75, world.rand.nextGaussian() * 0.75,
                        Block.getStateId(getBlockState()));
    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (getBehavior() instanceof FloatingBlockBehavior.Thrown)
            return super.canCollideWith(entity);
        else return false;
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
        // FIXME research: why doesn't syncBehavior cause an update to client?
        if (behavior == null) throw new IllegalArgumentException("Cannot have null behavior");
        dataManager.set(SYNC_BEHAVIOR, behavior);
    }

    @Override
    public EntityLivingBase getController() {
        return getOwner();
    }


    @Override
    public double getExpandedHitboxWidth() {
        return 0.35;
    }

    @Override
    public double getExpandedHitboxHeight() {
        return 0.35;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double d) {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public float getDamage() {
        return (float) (AvatarUtils.getMagnitude(velocity().toMinecraft()) / 20 * getDamageMult());
    }

    @Override
    public void setDead() {
        super.setDead();
        removeStatCtrl();
    }

    @Override
    public BendingStyle getElement() {
        return new Earthbending();
    }

    private void removeStatCtrl() {
        if (getOwner() != null) {
            BendingData bD = BendingData.getFromEntity(getOwner());
            if (bD != null)
                bD.removeStatusControls(THROW_BLOCK, PLACE_BLOCK);
        }

    }
}
