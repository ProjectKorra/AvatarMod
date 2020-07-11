package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.client.particle.AvatarParticles;
import com.crowsofwar.avatar.bending.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.bending.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Predicate;

public abstract class EntityOffensive extends AvatarEntity implements IOffensiveEntity {

    //Used for all entities that damage things
    private static final DataParameter<Float> SYNC_DAMAGE = EntityDataManager
            .createKey(EntityOffensive.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> SYNC_LIFETIME = EntityDataManager
            .createKey(EntityOffensive.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SYNC_HEIGHT = EntityDataManager
            .createKey(EntityOffensive.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_WIDTH = EntityDataManager
            .createKey(EntityOffensive.class, DataSerializers.FLOAT);
    private static final DataParameter<OffensiveBehaviour> SYNC_BEHAVIOR = EntityDataManager
            .createKey(EntityOffensive.class, OffensiveBehaviour.DATA_SERIALIZER);

    /**
     * The fraction of the impact velocity that should be the maximum spread speed added on impact.
     */
    private static final double SPREAD_FACTOR = 0.1;
    /**
     * Lateral velocity is reduced by this factor on impact, before adding random spread velocity.
     */
    private static final double IMPACT_FRICTION = 0.4;

    private float xp;
    private int fireTime;
    private boolean dynamicSpreadingCollision;
    private boolean collidedWithSolid;
    private int performanceAmount;
    private int ticks = 0, ticksMoving = 0;
    private double prevVelX, prevVelY, prevVelZ;
    private Predicate<Entity> solidEntities;
    private String damageSource;


    public EntityOffensive(World world) {
        super(world);
        this.performanceAmount = 20;
        this.fireTime = 3;
        this.xp = 3;
        this.dynamicSpreadingCollision = false;
        this.prevVelX = prevVelY = prevVelZ = 0;
        this.solidEntities = entity -> entity instanceof EntityWall || entity instanceof EntityWallSegment ||
                entity instanceof EntityShield && ((EntityShield) entity).getOwner() != getOwner();
        this.width = getWidth();
        this.height = getHeight();
        this.damageSource = AvatarDamageSource.FIRE.getDamageType();
    }

    public OffensiveBehaviour getBehaviour() {
        return dataManager.get(SYNC_BEHAVIOR);
    }

    public void setBehaviour(OffensiveBehaviour behaviour) {
        dataManager.set(SYNC_BEHAVIOR, behaviour);
    }

    public boolean getDynamicSpreadingCollision() {
        return this.dynamicSpreadingCollision;
    }

    public void setDynamicSpreadingCollision(boolean collision) {
        this.dynamicSpreadingCollision = collision;
    }

    public void setSolidEntityPredicate(Predicate<Entity> predicate) {
        this.solidEntities = predicate;
    }

    public void setSolidEntityPredicateOr(Predicate<Entity> predicate) {
        this.solidEntities = this.solidEntities.or(predicate);
    }

    public void setSolidEntityPredicateAnd(Predicate<Entity> predicate) {
        this.solidEntities = this.solidEntities.and(predicate);
    }

    public Predicate<Entity> getSolidEntities() {
        return this.solidEntities;
    }

    public float getHeight() {
        return dataManager.get(SYNC_HEIGHT);
    }

    public float getWidth() {
        return dataManager.get(SYNC_WIDTH);
    }

    public float getAvgSize() {
        if (getHeight() == getWidth()) {
            return getHeight();
        } else return (getHeight() + getWidth()) / 2;
    }

    public void setEntitySize(float height, float width) {
        dataManager.set(SYNC_HEIGHT, height);
        dataManager.set(SYNC_WIDTH, width);
    }

    public void setEntitySize(float size) {
        dataManager.set(SYNC_HEIGHT, size);
        dataManager.set(SYNC_WIDTH, size);
    }

    public float getDamage() {
        return dataManager.get(SYNC_DAMAGE);
    }

    public void setDamage(float damage) {
        dataManager.set(SYNC_DAMAGE, damage);
    }

    //This just makes the methods easier to use.
    public void Explode() {
        Explode(world, this, getOwner());
    }

    public void applyPiercingCollision() {
        applyPiercingCollision(this);
    }

    public void Dissipate() {
        Dissipate(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_DAMAGE, 1F);
        dataManager.register(SYNC_LIFETIME, 20);
        dataManager.register(SYNC_WIDTH, 1.0F);
        dataManager.register(SYNC_HEIGHT, 1.0F);
        dataManager.register(SYNC_BEHAVIOR, new OffensiveBehaviour.Idle());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setDamage(nbt.getFloat("Damage"));
        setLifeTime(nbt.getInteger("Lifetime"));
        setBehaviour((OffensiveBehaviour) Behavior.lookup(nbt.getInteger("Behaviour"), this));
        setDynamicSpreadingCollision(nbt.getBoolean("Dynamic Collision"));
        setXp(nbt.getFloat("XP"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("Damage", getDamage());
        nbt.setInteger("Lifetime", getLifeTime());
        nbt.setInteger("Behaviour", getBehaviour().getId());
        nbt.setBoolean("Dynamic Collision", getDynamicSpreadingCollision());
        nbt.setFloat("XP", getXpPerHit());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        setBehaviour((OffensiveBehaviour) getBehaviour().onUpdate(this));
        setSize(getWidth(), getHeight());

        List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, getExpandedHitbox());
        if (!targets.isEmpty()) {
            for (Entity hit : targets) {
                if (canCollideWith(hit) && this != hit) {
                    onCollideWithEntity(hit);
                }
            }
        }

        if (noClip) {
            IBlockState state = world.getBlockState(getPosition());
            if (state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock()) {
                ticks++;
            }
            if (ticks > 0 || onCollideWithSolid()) {
                //Checks whether to dissipate first.
                if (shouldDissipate())
                    Dissipate();
                else if (shouldExplode())
                    Explode();
            }
        }
        if (shouldDissipate() || shouldExplode())
            ticksMoving++;

        if (ticksMoving >= getLifeTime() && (shouldDissipate() || shouldExplode()) && getLifeTime() > 0) {
            if (shouldDissipate())
                Dissipate();
            else if (shouldExplode())
                Explode();
        }

        for (double x = 0; x <= 1; x += 0.5) {
            for (double z = 0; z <= 1; z += 0.5) {
                for (double y = 0; y <= 1; y += 0.5) {
                    double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
                    double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
                    double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
                    BlockPos pos = new BlockPos(xPos + x * getExpandedHitboxWidth() / 2,
                            yPos + y * getExpandedHitboxHeight() / 2, zPos + z * getExpandedHitboxWidth() / 2);
                    pushLevers(pos);
                    pushTrapDoors(pos);
                    pushButtons(pos);
                    pushDoors(pos);
                    pushGates(pos);
                }
            }
        }
        for (double x = 0; x >= -1; x -= 0.5) {
            for (double z = 0; z >= -1; z -= 0.5) {
                for (double y = 0; y >= -1; y -= 0.5) {
                    double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
                    double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
                    double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
                    BlockPos pos = new BlockPos(xPos + x * getExpandedHitboxWidth() / 2,
                            yPos + y * getExpandedHitboxHeight() / 2, zPos + z * getExpandedHitboxWidth() / 2);
                    pushLevers(pos);
                    pushTrapDoors(pos);
                    pushButtons(pos);
                    pushDoors(pos);
                    pushGates(pos);
                }
            }
        }

        //Dynamic Collision code.

        if (dynamicSpreadingCollision) {
            //Handles actual motion on colliding
            if (this.motionX == 0 && this.prevVelX != 0) { // If the particle just collided in x
                // Reduce lateral velocity so the added spread speed actually has an effect
                this.motionY *= IMPACT_FRICTION;
                this.motionZ *= IMPACT_FRICTION;
                // Add random velocity in y and z proportional to the impact velocity
                this.motionY += (rand.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
                this.motionZ += (rand.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
                if (setsFires)
                    setFires();
            }

            if (this.motionY == 0 && this.prevVelY != 0) { // If the particle just collided in y
                // Reduce lateral velocity so the added spread speed actually has an effect
                this.motionX *= IMPACT_FRICTION;
                this.motionZ *= IMPACT_FRICTION;
                // Add random velocity in x and z proportional to the impact velocity
                this.motionX += (rand.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
                this.motionZ += (rand.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
                if (setsFires)
                    setFires();
            }

            if (this.motionZ == 0 && this.prevVelZ != 0) { // If the particle just collided in z
                // Reduce lateral velocity so the added spread speed actually has an effect
                this.motionX *= IMPACT_FRICTION;
                this.motionY *= IMPACT_FRICTION;
                // Add random velocity in x and y proportional to the impact velocity
                this.motionX += (rand.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
                this.motionY += (rand.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
                if (setsFires)
                    setFires();
            }

            //Handles if it's colliding with something.
            double x = motionX, y = motionY, z = motionZ;
            double origX = x, origY = y, origZ = z;
            List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getEntityBoundingBox().expand(x, y, z).grow(0.1));
            List<Entity> entityList = this.world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().expand(x, y, z).grow(0.15));

            for (Entity hit : entityList) {
                if (hit != getOwner()) {
                    if (solidEntities.test(hit)) {
                        collidedWithSolid = true;
                    } else if (hit instanceof EntityThrowable || hit instanceof EntityArrow || hit instanceof EntityOffensive && canCollideWith(hit)) {
                        Vec3d hitVel = new Vec3d(hit.motionX, hit.motionY, hit.motionZ);
                        Vec3d pVel = new Vec3d(motionX, motionY, motionZ);
                        if (AvatarUtils.getMagnitude(hitVel) >= AvatarUtils.getMagnitude(pVel))
                            motionX = motionY = motionZ = 0;
                        else {
                            this.motionX += hit.motionX;
                            this.motionY += hit.motionY;
                            this.motionZ += hit.motionZ;
                        }
                    }
                }
            }
            if (!list.isEmpty() && !onGround) {
                for (AxisAlignedBB axisalignedbb : list) {
                    Vec3d mid = AvatarUtils.getMiddleVec3d(axisalignedbb);
                    BlockPos pos = new BlockPos(mid.x, mid.y, mid.z);
                    IBlockState state = world.getBlockState(pos);
                    if (!noClip || state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock() && state.isFullCube()
                            && (!pushDoor || !(state.getBlock() instanceof BlockDoor)))
                        y = axisalignedbb.calculateYOffset(this.getEntityBoundingBox(), y);
                }

                //TODO: Make this configurable. Ensures entities are killed when they hit the ground.

                if (y < posY && onGround) {
                    if (setsFires)
                        setFires();
                    if (shouldExplode())
                        Explode();
                    else Dissipate();
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

                for (AxisAlignedBB axisalignedbb1 : list) {
                    Vec3d mid = AvatarUtils.getMiddleVec3d(axisalignedbb1);
                    BlockPos pos = new BlockPos(mid.x, mid.y, mid.z);
                    IBlockState state = world.getBlockState(pos);
                    if (!noClip || state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock() && state.isFullCube()
                            && (!pushDoor || !(state.getBlock() instanceof BlockDoor)))
                        x = axisalignedbb1.calculateXOffset(this.getEntityBoundingBox(), x);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));

                for (AxisAlignedBB axisalignedbb2 : list) {
                    Vec3d mid = AvatarUtils.getMiddleVec3d(axisalignedbb2);
                    BlockPos pos = new BlockPos(mid.x, mid.y, mid.z);
                    IBlockState state = world.getBlockState(pos);
                    if (!noClip || state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock() && state.isFullCube()
                            && (!pushDoor || !(state.getBlock() instanceof BlockDoor)))
                        z = axisalignedbb2.calculateZOffset(this.getEntityBoundingBox(), z);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));
            }

            if (collidedWithSolid) {
                motionX = motionY = motionZ = 0.0D;
                if (setsFires)
                    setFires();
            }

            if (origX != x) this.motionX = 0.0D;
            if (origY != y) this.motionY = 0.0D; // Why doesn't Particle do this for y?
            if (origZ != z) this.motionZ = 0.0D;
        }

        if (onCollideWithSolid()) {
            if (isProjectile() && shouldExplode())
                Explode();
            if (isProjectile() && shouldDissipate())
                Dissipate();
        }

        //These values are only used for proper visual spread collision.
        prevVelX = motionX;
        prevVelY = motionY;
        prevVelZ = motionZ;
    }

    @Override
    public EntityLivingBase getController() {
        return getOwner();
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);

        if (!isPiercing() && isProjectile() && shouldExplode())
            Explode();
        else if (!isPiercing() && shouldDissipate()) {
            attackEntity(this, entity, false, getKnockback());
            Dissipate();
        } else if (isShockwave())
            attackEntity(this, entity, false, getKnockback(entity));
        else applyPiercingCollision();
        if (entity instanceof AvatarEntity)
            applyElementalContact((AvatarEntity) entity);
        if (getSolidEntities().test(entity))
            if (shouldExplode())
                Explode();
            else Dissipate();

    }

    @Override
    public Vec3d getKnockback() {
        double x = getKnockbackMult().x * motionX;
        double y = Math.min(0.5, (motionY + 0.15) * getKnockbackMult().y);
        double z = getKnockbackMult().z * motionZ;
        return new Vec3d(x, y, z);
    }

    @Override
    public float getXpPerHit() {
        return xp;
    }


    @Override
    public boolean onCollideWithSolid() {
        IBlockState state = world.getBlockState(getPosition());
        boolean collision = !world.getCollisionBoxes(this, getExpandedHitbox()).isEmpty();
        for (double x = 0; x <= 1; x += 0.5) {
            for (double z = 0; z <= 1; z += 0.5) {
                for (double y = 0; y <= 1; y += 0.5) {
                    double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
                    double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
                    double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
                    BlockPos pos = new BlockPos(xPos + x * getExpandedHitboxWidth() / 2,
                            yPos + y * getExpandedHitboxHeight() / 2, zPos + z * getExpandedHitboxWidth() / 2);
                    state = world.getBlockState(pos);
                    collision &= state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock() && state.isFullCube();
                    if (collision)
                        break;

                }
            }
        }
        for (double x = 0; x >= -1; x -= 0.5) {
            for (double z = 0; z >= -1; z -= 0.5) {
                for (double y = 0; y >= -1; y -= 0.5) {
                    double xPos = AvatarEntityUtils.getMiddleOfEntity(this).x;
                    double yPos = AvatarEntityUtils.getMiddleOfEntity(this).y;
                    double zPos = AvatarEntityUtils.getMiddleOfEntity(this).z;
                    BlockPos pos = new BlockPos(xPos + x * getExpandedHitboxWidth() / 2,
                            yPos + y * getExpandedHitboxHeight() / 2, zPos + z * getExpandedHitboxWidth() / 2);
                    state = world.getBlockState(pos);
                    collision &= state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock() && state.isFullCube();
                    if (collision)
                        break;
                }
            }
        }
        return collided || collision;
    }

    public int getLifeTime() {
        return dataManager.get(SYNC_LIFETIME);
    }

    public void setLifeTime(int lifeTime) {
        dataManager.set(SYNC_LIFETIME, lifeTime);
    }

    @Override
    public float getAoeDamage() {
        return 1;
    }

    @Override
    public Vec3d getKnockbackMult() {
        return new Vec3d(1, 2, 1);
    }

    @Override
    public EnumParticleTypes getParticle() {
        return AvatarParticles.getParticleFlames();
    }

    @Override
    public int getNumberofParticles() {
        return 50;
    }

    @Override
    public double getParticleSpeed() {
        return 0.02;
    }

    @Override
    public int getPerformanceAmount() {
        return this.performanceAmount;
    }

    public void setPerformanceAmount(int amount) {
        this.performanceAmount = amount;
    }

    @Override
    public float getVolume() {
        return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
    }

    @Override
    public float getPitch() {
        return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
    }

    @Override
    public void setDamageSource(String source) {
        this.damageSource = source;
    }

    @Override
    public DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
        DamageSource source = AvatarDamageSource.FIRE;
        if (damageSource.startsWith("avatar_")) {
            source = new EntityDamageSourceIndirect(damageSource, target, owner);
            if (isProjectile())
               source.setProjectile();
            source.setMagicDamage();
            if (getElement() instanceof Lightningbending)
                source.setDamageBypassesArmor();
            if (getElement() instanceof Combustionbending)
                source.setExplosion();
        }
        return source;
    }

    @Override
    public double getExpandedHitboxWidth() {
        return 0.25;
    }

    @Override
    public double getExpandedHitboxHeight() {
        return 0.25;
    }

    @Override
    public int getFireTime() {
        return this.fireTime;
    }

    public void setFireTime(int time) {
        this.fireTime = time;
    }

    @Override
    public boolean isPiercing() {
        return false;
    }

    @Override
    public boolean shouldDissipate() {
        return false;
    }

    @Override
    public boolean shouldExplode() {
        return true;
    }

    public AxisAlignedBB getExpandedHitbox() {
        return getExpandedHitbox(this);
    }

    @Override
    public double getExplosionHitboxGrowth() {
        return 1;
    }

    @Override
    public void applyElementalContact(AvatarEntity entity) {

    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    @Override
    public boolean canBePushed() {
        return !isPiercing();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    //No relation to getKnockback.
    @Override
    public Vec3d getExplosionKnockbackMult() {
        return new Vec3d(0.4, 0.4, 0.4);
    }

    //Only used in shockwaves
    @Override
    public Vec3d getKnockback(Entity target) {
        Vec3d knockback = Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().toMinecraft();
        return new Vec3d(knockback.x * getKnockbackMult().x, knockback.y * getKnockbackMult().y, knockback.z * getKnockbackMult().z);
    }
}
