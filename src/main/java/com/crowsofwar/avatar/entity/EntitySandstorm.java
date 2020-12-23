package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.bending.bending.sand.Sandbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.config.ConfigSkills;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.SandstormMovementHandler;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EntitySandstorm extends EntityOffensive {

    private static final DataParameter<Float> SYNC_VELOCITY_MULT = EntityDataManager.createKey(EntitySandstorm.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_STRENGTH = EntityDataManager.createKey(EntitySandstorm.class, DataSerializers.FLOAT);

    private final SandstormMovementHandler movementHandler;

    private boolean damageFlungTargets;
    private boolean damageContactingTargets;
    private boolean vulnerableToAirbending;

    /**
     * How many ticks has the sandstorm been alive, used for animations. Normally this is just ticksExisted. However, when the
     * {@link #getStrength() strength} becomes low, the animation slows and animationProgress increases slower than normal.
     */
    @SideOnly(Side.CLIENT)
    private float animationProgress;

    /**
     * The sandstorm's previous ticksExisted the last time it was rendered. Note that the lastRenderAge also factors in
     * partial ticks.
     */
    @SideOnly(Side.CLIENT)
    private float lastRenderAge;

    public EntitySandstorm(World world) {
        super(world);
        setSize(2.2f, 5.2f);
        movementHandler = new SandstormMovementHandler(this);
        vulnerableToAirbending = true;
        stepHeight = 1;
    }

    @Override
    public void setDead() {
        super.setDead();
        removeStatCtrl();
        if (this.isDead && !world.isRemote)
            Thread.dumpStack();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_VELOCITY_MULT, 1f);
        dataManager.register(SYNC_STRENGTH, 1f);
    }

    @Override
    public double getExpandedHitboxHeight() {
        return super.getExpandedHitboxHeight();
    }

    @Override
    public void onUpdate() {

        // For "onGround = true":
        // Hacky way to ensure stepHeight is respected. If onGround is false (like it would be), the stepHeight is
        // ignored. This doesn't affect other logic since onUpdate reassigns onGround to the actual/correct value
        onGround = true;

        super.onUpdate();
        if (!world.isRemote) {
            movementHandler.update();
        }


        // If a gap is detected between the ground (i.e. the sandstorm is hovering above the ground), move down
        // and close the gap
        if (!world.isRemote && isGroundGap()) {
            setPosition(posX, posY - 1, posZ);
        }

        if (!world.isRemote) {
            IBlockState groundBlockState = getGroundBlock();
            Block groundBlock = groundBlockState == null ? null : groundBlockState.getBlock();

            if (STATS_CONFIG.sandBlocks.contains(groundBlock)) {
                setStrength(getStrength() - 0.003f);
                setVelocityMultiplier(getVelocityMultiplier() - 0.001f);
            } else {
                setStrength(getStrength() - 0.02f);
                setVelocityMultiplier(getVelocityMultiplier() - 0.01f);
            }

            if (getStrength() == 0) {
                setDead();
            }

        }

        //Render code
        //ArrayLists cause I'm lazy
        ArrayList<Vec3d> tornadoPoints = new ArrayList<>();
        //While the previous list stores points to use to render, this list contains the velocities.
        //We then bezier curve through the velocities as well. Is it calculus? Yes. But shhh
        ArrayList<Vec3d> tornadoVelocity = new ArrayList<>();
        if (world.isRemote && getOwner() != null) {
            //Essentially, creates a rough outline of a vortex, which is then bezier-curved.
            //It optimises everything and looks cooler. I think.

            //Creates the points to put into the bezier curve.
            //High angle ensures it actually rotates around and looks cool
            //Creates a vortex to the right and to the left
            for (int angle = 0; angle < 1440; angle += 30) {
                double radAngle = Math.toRadians(angle);
                double radius = 0.01 + (angle / (1440 / (getWidth() / 2)));
                double x = radius * cos(radAngle);
                double y = angle / (1440 / (getExpandedHitboxHeight() + getHeight()));
                double z = radius * sin(radAngle);
                double speed = world.rand.nextDouble() * 2 + 1;
                double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
                Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(this);
                tornadoPoints.add(new Vec3d(x + centre.x + world.rand.nextGaussian() / 5, y + centre.y, z + centre.z + world.rand.nextGaussian() / 5));
                tornadoVelocity.add(new Vec3d(x * omega * 0.05, ((1440 / (getExpandedHitboxHeight() + getHeight())) - y) / 2500F, z * omega * 0.05F));
            }
            //Draws the bezier curve
            for (int i = 0; i < tornadoPoints.size() - 1; i++) {
                Vec3d vel = tornadoVelocity.get(i);
                Vec3d pos = tornadoPoints.get(i);
                Vec3d pos2 = tornadoPoints.get(i + 1);
                Vec3d pos3 = null;
                if (i < tornadoPoints.size() - 2)
                    pos3 = tornadoPoints.get(i + 2);

                Vec3d[] points = new Vec3d[pos3 == null ? 2 : 3];
                points[0] = pos;
                points[1] = pos2;
                if (pos3 != null)
                    points[2] = pos3;

                //Iterate for the amount of particles in between each line.
                for (int h = 0; h < 360; h += (360 / 4)) {
                    Vec3d middle = AvatarEntityUtils.getBottomMiddleOfEntity(this);
                    pos = pos.add(AvatarUtils.bezierCurve(h / 360F, points));

                    double radius = pos.x - middle.x;
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.x, pos.y, pos.z).clr(220, 180, 130, 25)
                            .vel(vel.x + world.rand.nextGaussian() / 60 + motionX, vel.y + motionY, vel.z + world.rand.nextGaussian() / 60 +
                                    motionZ).element(new Sandbending()).spin(Math.abs(radius) / 2,
                            world.rand.nextGaussian() * 0.125F).spawnEntity(getOwner())
                            .time(8 + AvatarUtils.getRandomNumberInRange(0, 4)).scale(0.5F).spawn(world);
                }
                //Only curves through 3 points to optimise the shape of the curve.

            }

        }

    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    /**
     * Returns whether there is a gap between the sandstorm and the ground, between 1-3 blocks. Returns false if the
     * sandstorm is touching the ground, or the gap is larger than 3 blocks.
     */
    private boolean isGroundGap() {

        BlockPos pos = getPosition();
        BlockPos belowPos = pos.down();

        // Make sure there is an actual gap - there should be an empty block below the sandstorm
        IBlockState belowBlock = world.getBlockState(belowPos);
        boolean liquid = belowBlock.getBlock() instanceof BlockLiquid;
        if (belowBlock.getCollisionBoundingBox(world, belowPos) == null && !liquid) {

            // Look up to 3 blocks under the sandstorm
            for (int i = 0; i < 3; i++) {
                BlockPos moreDown = pos.down(i + 2);
                if (world.isSideSolid(moreDown, EnumFacing.UP)) {
                    // Found the ground block, and it's within 3 blocks from the sandstorm
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * Gets the first "ground" block that is up to 3 blocks under the sandstorm. If there are no "ground" blocks up to 3
     * meters directly under the sandstorm, returns null. Ground blocks are defined as any solid or liquid block.
     */
    @Nullable
    private IBlockState getGroundBlock() {

        BlockPos pos = getPosition();

        for (int i = 1; i <= 3; i++) {
            BlockPos belowPos = pos.down(i);
            IBlockState belowBlock = world.getBlockState(belowPos);

            boolean liquid = belowBlock.getBlock() instanceof BlockLiquid;
            if (world.isSideSolid(belowPos, EnumFacing.UP) || liquid) {
                // Hit a ground block
                return world.getBlockState(belowPos);
            }

        }

        return null;

    }

    @Override
    public boolean canPush() {
        return vulnerableToAirbending;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {

        // Number of blocks that the target "floats" above the ground
        final double floatingDistance = 2;
        // The maximum distance between a sandstorm and an orbiting mob before the mob is thrown
        final double maxPickupRange = 1.3;

        if (entity == getOwner()) {
            return;
        }

        // Rotates the entity around this sandstorm
        // First: calculates current angle, and the next angle
        // Then, calculates position with that next angle
        // Finally, finds a velocity which will move towards that point

        double currentAngle = Vector.getRotationTo(position(), Vector.getEntityPos(entity)).y();
        double nextAngle = currentAngle + Math.toRadians(360 / 20F);

        double currentDistance = entity.getDistance(this.posX, this.posY + floatingDistance, this
                .posZ);
        double nextDistance = currentDistance + 0.01;

        // Prevent entities from orbiting too closely
        if (nextDistance < 0.8) {
            nextDistance = 0.8;
        }

        // Below conditions handle cases when entity was just picked up or needs to be flung off

        if (nextDistance > 2) {
            // Entities recently picked up typically have very large distances, over maxPickupRange
            // Bring them close to the center
            nextDistance = 0.8;
            onPickupEntity();
        } else if (nextDistance > maxPickupRange) {
            // If the distance is large, but not very large(>2), it has probably just been here
            // for a while
            // Fling entity to be far away quickly
            nextDistance = 3;
            // Fling in the current direction
            nextAngle = Vector.getRotationTo(Vector.ZERO, velocity()).y();
            onFlingEntity(entity);
        }

        Vector nextPos = position().plus(Vector.toRectangular(nextAngle, 0).times(nextDistance))
                .plusY(floatingDistance);
        Vector delta = nextPos.minus(Vector.getEntityPos(entity));

        Vector nextVelocity = velocity().plus(delta.times(20));
        entity.motionX = nextVelocity.x() / 20;
        entity.motionY = nextVelocity.y() / 20;
        entity.motionZ = nextVelocity.z() / 20;

        AvatarUtils.afterVelocityAdded(entity);
        onContact(entity);

    }


    public SandstormMovementHandler getMovementHandler() {
        return movementHandler;
    }

    /**
     * Called when an entity is picked up by the sandstorm
     */
    private void onPickupEntity() {
        if (getOwner() != null) {
            AbilityData.get(getOwner(), "sandstorm").addXp(ConfigSkills.SKILLS_CONFIG.sandstormPickedUp);
            BattlePerformanceScore.addMediumScore(getOwner());
        }
    }

    /**
     * Called when the sandstorm "flings" an entity away after it's been orbiting for a while
     */
    private void onFlingEntity(Entity entity) {
        if (!world.isRemote && damageFlungTargets && canDamageEntity(entity)) {
            DamageSource ds = AvatarDamageSource.causeSandstormDamage(entity, getOwner());
            entity.attackEntityFrom(ds, 5);
        }
    }

    /**
     * Called when another entity is picked up and orbits the sandstorm
     */
    private void onContact(Entity entity) {
        if (!world.isRemote && damageContactingTargets) {
            DamageSource ds = AvatarDamageSource.causeSandstormDamage(entity, getOwner());
            entity.attackEntityFrom(ds, 1);
        }
    }

    @Override
    public boolean onAirContact() {
        if (vulnerableToAirbending) {
            setDead();
            return true;
        }
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    public boolean isDamageFlungTargets() {
        return damageFlungTargets;
    }

    public void setDamageFlungTargets(boolean damageFlungTargets) {
        this.damageFlungTargets = damageFlungTargets;
    }

    public boolean isDamageContactingTargets() {
        return damageContactingTargets;
    }

    public void setDamageContactingTargets(boolean damageContactingTargets) {
        this.damageContactingTargets = damageContactingTargets;
    }

    public boolean isVulnerableToAirbending() {
        return vulnerableToAirbending;
    }

    public void setVulnerableToAirbending(boolean vulnerableToAirbending) {
        this.vulnerableToAirbending = vulnerableToAirbending;
    }

    public float getVelocityMultiplier() {
        return dataManager.get(SYNC_VELOCITY_MULT);
    }

    public void setVelocityMultiplier(float velocityMultiplier) {
        dataManager.set(SYNC_VELOCITY_MULT, velocityMultiplier);
    }

    /**
     * Gets the current strength of the sandstorm. This represents how powerful it is at any given moment. The strength is between 0 and 1.
     */
    public float getStrength() {
        return dataManager.get(SYNC_STRENGTH);
    }


    public void setStrength(float strength) {
        dataManager.set(SYNC_STRENGTH, MathHelper.clamp(strength, 0, 1));
    }

    @Override
    public boolean shouldDissipate() {
        return false;
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
    public void spawnExplosionParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @SideOnly(Side.CLIENT)
    public float getAnimationProgress() {
        return animationProgress;
    }

    @SideOnly(Side.CLIENT)
    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
    }

    @SideOnly(Side.CLIENT)
    public float getLastRenderAge() {
        return lastRenderAge;
    }

    @SideOnly(Side.CLIENT)
    public void setLastRenderAge(float lastRenderAge) {
        this.lastRenderAge = lastRenderAge;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setDamageFlungTargets(nbt.getBoolean("DamageFlungTargets"));
        setDamageContactingTargets(nbt.getBoolean("DamageContactingTargets"));
        setVulnerableToAirbending(nbt.getBoolean("VulnerableToAirbending"));
        setVelocityMultiplier(nbt.getFloat("VelocityMultiplier"));
        setStrength(nbt.getFloat("Strength"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("DamageFlungTargets", isDamageFlungTargets());
        nbt.setBoolean("DamageContactingTargets", isDamageContactingTargets());
        nbt.setBoolean("VulnerableToAirbending", isVulnerableToAirbending());
        nbt.setFloat("VelocityMultiplier", getVelocityMultiplier());
        nbt.setFloat("Strength", getStrength());
    }

    private void removeStatCtrl() {
        if (getOwner() != null) {
            BendingData bD = BendingData.get(getOwner());
            if (bD.hasStatusControl(StatusControlController.SANDSTORM_REDIRECT)) {
                bD.removeStatusControl(StatusControlController.SANDSTORM_REDIRECT);
            }
        }
    }


}
