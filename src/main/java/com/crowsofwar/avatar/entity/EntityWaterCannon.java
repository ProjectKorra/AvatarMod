package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;


public class EntityWaterCannon extends EntityArc<EntityWaterCannon.WaterControlPoint> implements IShieldEntity {

    private static final DataParameter<Float> SYNC_SPEED = EntityDataManager
            .createKey(EntityWaterArc.class, DataSerializers.FLOAT);
    private final float velocityMultiplier;
    /**
     * The amount of ticks since last played splash sound. -1 for splashable.
     */
    private int lastPlayedSplash;
    private float damageMult;


    public EntityWaterCannon(World world) {
        super(world);
        setSize(0.2F, 0.2F);
        this.lastPlayedSplash = -1;
        this.noClip = true;
        this.damageMult = 1;
        this.putsOutFires = true;
        this.velocityMultiplier = 5;
        this.ignoreFrustumCheck = true;
    }

    public float getDamageMult() {
        return damageMult;
    }

    public void setDamageMult(float mult) {
        this.damageMult = mult;
    }

    public float getSpeed() {
        return dataManager.get(SYNC_SPEED);
    }

    public void setSpeed(float speed) {
        dataManager.set(SYNC_SPEED, speed);
    }


    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_SPEED, 30F);
    }


    @Override
    public void spawnExplosionParticles(World world, Vec3d pos) {
        if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
                for (int h = 0; h < 6; h++) {
                    Vec3d circlePos = Vector.getOrthogonalVector(getLookVec(), (ticksExisted % 360) * 20 + h * 60, getAvgSize() / 2F).toMinecraft().add(pos);
                    Vec3d vel = new Vec3d(world.rand.nextGaussian() / 10 * getExplosionSize(), world.rand.nextGaussian() / 10
                            * getExplosionSize(), world.rand.nextGaussian() / 10 * getExplosionSize());

                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(circlePos).spawnEntity(this).vel(vel)
                            .clr(0, 102, 255, 85).scale(getAvgSize())
                            .time(16 + AvatarUtils.getRandomNumberInRange(0, 4)).collide(true).collideParticles(true).element(new Waterbending()).spawn(world);
                }
            }
        }
    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        SoundEvent[] events = new SoundEvent[1];
        events[0] = SoundEvents.ENTITY_GENERIC_SPLASH;
        return events;
    }

    @Override
    public void applyElementalContact(AvatarEntity entity) {
        super.applyElementalContact(entity);
        if (entity.getTier() <= getTier())
            entity.onMajorWaterContact();
        else entity.onMinorWaterContact();
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    protected void updateCpBehavior() {
        getLeader().setPosition(Vector.fromVec3d(AvatarEntityUtils.getMiddleOfEntity(this)));
        getLeader().setVelocity(velocity());

        // Move control points to follow leader

        for (int i = 1; i < points.size(); i++) {

            ControlPoint leader = points.get(i - 1);
            ControlPoint p = points.get(i);
            Vector leadPos = leader.position();
            double sqrDist = p.position().sqrDist(leadPos);

            //Sets the last point to in front of the player
            if (i < points.size() - 1) {
                if (sqrDist > getControlPointTeleportDistanceSq() && getControlPointTeleportDistanceSq() != -1) {

                    Vector toFollowerDir = p.position().minus(leader.position()).normalize();

                    double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
                    if (idealDist > 1) idealDist -= 1; // Make sure there is some room

                    Vector revisedOffset = leader.position().plus(toFollowerDir.times(idealDist));
                    p.setPosition(revisedOffset);
                    leader.setPosition(revisedOffset);
                    p.setVelocity(Vector.ZERO);

                } else if (sqrDist > getControlPointMaxDistanceSq() && getControlPointMaxDistanceSq() != -1) {

                    Vector diff = leader.position().minus(p.position());
                    diff = diff.normalize().times(getVelocityMultiplier());
                    p.setVelocity(p.velocity().plus(diff));

                }
            }

        }
        if (getOwner() != null)
            getControlPoint(points.size() - 1).setPosition(Vector.getEyePos(getOwner()).minusY(getOwner().getEyeHeight() / 2));
    }

    @Override
    public Vec3d getKnockback() {
        return super.getKnockback();
    }

    @Override
    public Vec3d getKnockbackMult() {
        return new Vec3d(0.75, 0.5, 0.75);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();
        if (lastPlayedSplash > -1) {
            lastPlayedSplash++;
            if (lastPlayedSplash > 20) lastPlayedSplash = -1;
        }


        if (getOwner() == null) {
            this.setDead();
        }

        if (ticksExisted >= getLifeTime())
            setDead();

        setEntitySize(getAvgSize());
        System.out.println(getAvgSize());


        if (world.isRemote && getOwner() != null && ticksExisted % 2 == 0) {
            //Expanding vortex???
            double dist = getControlPoint(getAmountOfControlPoints() - 1).getDistance(getLeader());

//            //Spawns a spinning vortex near the player. Let's hope this works.
//            AvatarParticleUtils.spawnSpinningDirectionalVortex(world, this,
//                    position().plus(Vector.getLookRectangular(this).times(-dist)).toMinecraft(),
//                    (int) (dist * getAvgSize() * 60), dist, 0.1F,
//                    Math.min(getAvgSize() * 2 * dist / getLifeTime(), getAvgSize() * 2),
//                    ParticleBuilder.Type.CUBE, Vec3d.ZERO,
//                    Vec3d.ZERO, true,
//                    20, 120, 255, 120,4, BendingStyles.get(Waterbending.ID),
//                    false, getAvgSize() / 2);
            float size = getAvgSize() * 0.75F;
            //Overall sphere shape/swirl
            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 50).gravity(true)
                    .time(16).scale(0.5F).spawnEntity(this).element(BendingStyles.get(Waterbending.ID))
                    .spin(size / 10, world.rand.nextGaussian() / 20)
                    .swirl((int) (size * 12), (int) (size * 4 * Math.PI),
                            size, size * 5, (float) (velocity().magnitude() * size * 6F),
                            (float) (world.rand.nextGaussian() / 8F), this, world, false, AvatarEntityUtils.getBottomMiddleOfEntity(this),
                            ParticleBuilder.SwirlMotionType.OUT, false, true);
            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 90).gravity(true)
                    .time(16).scale(0.5F).spawnEntity(this).element(BendingStyles.get(Waterbending.ID))
                    .spin(size / 10, world.rand.nextGaussian() / 20)
                    .swirl((int) (size * 12), (int) (size * 4 * Math.PI),
                            size, size * 3, (float) (velocity().magnitude() * size * 6),
                            (float) (world.rand.nextGaussian() / 8F), this, world, true, AvatarEntityUtils.getBottomMiddleOfEntity(this),
                            ParticleBuilder.SwirlMotionType.OUT, false, true);


            Vec3d[] points = new Vec3d[getAmountOfControlPoints()];
            for (int i = 0; i < points.length; i++)
                points[i] = getControlPoint(i).position().toMinecraft();
            //Particles! Let's do this.
            //First, we need a bezier curve. Joy.
            //0 is the leader/front one
            for (int i = 0; i < getAmountOfControlPoints(); i++) {
                Vec3d pos = getControlPoint(points.length - i - 1).position().toMinecraft();
                Vec3d pos2 = i < points.length - 1 ? getControlPoint(Math.max(points.length - i - 2, 0)).position().toMinecraft() : Vec3d.ZERO;

                for (int h = 0; h < 4; h++) {
                    pos = pos.add(AvatarUtils.bezierCurve(((points.length - i - 1D / (h + 1)) / points.length), points));

                    //Flow animation
                    pos2 = pos2.add(AvatarUtils.bezierCurve(Math.min((((i + 1) / (h + 1D)) / points.length), 1), points));
                    Vec3d circlePos = Vector.getOrthogonalVector(getLookVec(), (ticksExisted % 360) * 20 + h * 90, getAvgSize() / 2F).toMinecraft().add(pos);
                    Vec3d targetPos = i < points.length - 1 ? Vector.getOrthogonalVector(getLookVec(),
                            (ticksExisted % 360) * 20 + h * 90 + 20, getAvgSize() / 2F).toMinecraft().add(pos2)
                            : Vec3d.ZERO;
                    Vec3d vel = new Vec3d(world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240);

                    if (targetPos != circlePos)
                        vel = targetPos == Vec3d.ZERO ? vel : targetPos.subtract(circlePos).normalize().scale(0.15).add(vel);
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(circlePos).spawnEntity(this).vel(vel)
                            .clr(0, 102, 255, 85).scale(getAvgSize()).target(targetPos == Vec3d.ZERO ? pos : targetPos)
                            .time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).collide(true).collideParticles(true).element(new Waterbending()).spawn(world);
                }

                //Particles along the line
                for (int h = 0; h < 5; h++) {
                    pos = pos.add(AvatarUtils.bezierCurve(((points.length - i - 1D / (h + 1)) / points.length), points));
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(pos).spawnEntity(this).vel(world.rand.nextGaussian() / 40 * getAvgSize(),
                            world.rand.nextGaussian() / 40 * getAvgSize(), world.rand.nextGaussian() / 40 * getAvgSize()).scale(getAvgSize()).clr(0, 102, 255, 105)
                            .time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).collide(true).element(BendingStyles.get(Waterbending.ID)).spawn(world);

                }
                //Dripping water particles
                for (int h = 0; h < 1; h++) {
                    pos = pos.add(AvatarUtils.bezierCurve(((points.length - i - 1D / (h + 1)) / points.length), points));
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(pos).spawnEntity(this).vel(world.rand.nextGaussian() / 20,
                            world.rand.nextDouble() / 12, world.rand.nextGaussian() / 20).clr(0, 102, 255, 85)
                            .time(6 + AvatarUtils.getRandomNumberInRange(0, 3)).target(pos).scale(getAvgSize()).gravity(true).collide(true).collideParticles(true).element(new Waterbending()).spawn(world);
                }
            }
        }
    }


    @Override
    public int getFireTime() {
        return 0;
    }

    @Override
    protected EntityWaterCannon.WaterControlPoint createControlPoint(float size, int index) {
        return new EntityWaterCannon.WaterControlPoint(this, size, posX, posY, posZ);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean multiHit() {
        return true;
    }

    @Override
    public EntityLivingBase getController() {
        return getOwner();
    }

    @Override
    protected double getVelocityMultiplier() {
        return velocityMultiplier;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean shouldDissipate() {
        return true;
    }

    @Override
    public float getHealth() {
        return 0;
    }

    @Override
    public void setHealth(float health) {

    }

    @Override
    public float getMaxHealth() {
        return 0;
    }

    @Override
    public void setMaxHealth(float maxHealth) {

    }

    @Override
    protected double getControlPointTeleportDistanceSq() {
        return getControlPointMaxDistanceSq() * getAmountOfControlPoints();
    }

    @Override
    protected double getControlPointMaxDistanceSq() {
        return getAvgSize() * 20;
    }

    @Override
    public int getAmountOfControlPoints() {
        return 8;
    }

    @Override
    public boolean shouldExplode() {
        return true;
    }

    @Override
    public boolean setVelocity() {
        return false;
    }

    @Override
    public UUID getElement() {
        return Waterbending.ID;
    }

    static class WaterControlPoint extends ControlPoint {

        private WaterControlPoint(EntityWaterCannon arc, float size, double x, double y, double z) {
            super(arc, size, x, y, z);
        }

    }

}
