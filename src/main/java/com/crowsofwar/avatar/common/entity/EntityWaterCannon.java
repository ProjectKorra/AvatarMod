package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterCannon;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.bending.water.tickhandlers.WaterChargeHandler;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;


public class EntityWaterCannon extends EntityArc<EntityWaterCannon.CannonControlPoint> {

    private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
            (EntityWaterCannon.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_SPEED = EntityDataManager.createKey
            (EntityWaterCannon.class, DataSerializers.FLOAT);
    private final ParticleSpawner particles;
    private float damage;
    private float lifeTime;
    private double maxRange;
    private double range;
    private Vec3d knockBack;

    public EntityWaterCannon(World world) {
        super(world);
        setSize(1.5f * getSizeMultiplier(), 1.5f * getSizeMultiplier());
        damage = 0.5F;
        this.putsOutFires = true;
        this.noClip = false;
        this.particles = new NetworkParticleSpawner();
        this.setInvisible(false);
        this.range = 0;
    }

    public float getSpeed() {
        return dataManager.get(SYNC_SPEED);
    }

    public void setSpeed(float speed) {
        dataManager.set(SYNC_SPEED, speed);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getSizeMultiplier() {
        return dataManager.get(SYNC_SIZE);
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        dataManager.set(SYNC_SIZE, sizeMultiplier);
    }

    public void setMaxRange(float range) {
        this.maxRange = range;
    }

    public void setKnockBack(Vec3d knockBack) {
        this.knockBack = knockBack;
    }


    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_SIZE, 1f);
        dataManager.register(SYNC_SPEED, 0F);
        range = 0;
    }

    @Override
    public int getAmountOfControlPoints() {
        return super.getAmountOfControlPoints();
    }


    @Override
    public void onUpdate() {
        super.onUpdate();

        world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
                SoundCategory.PLAYERS, 1, 2);


        if (getOwner() != null) {
            //Todo: Behaviour for cylinder to change its length, pitch, and yaw onUpdate
            if (getAbility() instanceof AbilityWaterCannon) {
                Vec3d startPos = getControlPoint(getAmountOfControlPoints() - 1).position().toMinecraft();
                Vec3d distance = getOwner().getLookVec().scale(range);
                Vec3d endPos = startPos.add(distance);
                range += range < maxRange ? maxRange / lifeTime : 0;

                Vec3d speed = endPos.subtract(startPos);

                if (!world.isRemote) {
                    if (onCollideWithSolid()) {
                        this.motionX = this.motionY = this.motionZ = 0;
                        setVelocity(Vector.ZERO);
                    } else {
                        this.motionX = speed.x / 60;
                        this.motionY = speed.y / 60;
                        this.motionZ = speed.z / 60;
                    }
                }

            }

            //Increases the amount of control points as the water cannon
            //stays alive; basically lengthens the water laser
            setNumberofPoints((int) (getSpeed() / 20 * ticksExisted) + 2);

            if (ticksExisted % 4 == 0 && !this.isDead && STATS_CONFIG.waterCannonSettings.useWaterCannonParticles) {
                double dist = this.getDistance(getOwner());
                int particleController = 20;
                if (getAbility() instanceof AbilityWaterCannon && !world.isRemote) {
                    AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
                    particleController = 23;
                    if (data.getLevel() == 1) {
                        particleController = 20;
                    }
                    if (data.getLevel() >= 2) {
                        particleController = 17;
                    }

                    if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                        particleController = 120;
                    }
                }
               /* for (double i = 0; i < 1; i += 1 / dist) {
                    Vector startPos = getControlPoint(points.size() - 1).position();
                    Vector distance = this.position().minus(getControlPoint(points.size() - 1).position());
                    distance = distance.times(i);
                    for (double angle = 0; angle < 360; angle += particleController) {
                        Vector position = Vector.getOrthogonalVector(this.position().minus(getControlPoint(points.size() - 1).position()), angle, getSizeMultiplier() * 1.4);
                        particles.spawnParticles(world, EnumParticleTypes.WATER_WAKE, 1, 1,
                                position.x() + startPos.x() + distance.x(), position.y() + startPos.y() + distance.y(), position.z() + startPos.z() + distance.z(), 0, 0, 0, true);

                    }
                    particles.spawnParticles(world, EnumParticleTypes.WATER_WAKE, 1, 1,
                            startPos.x() + distance.x(), startPos.y() + distance.y(), startPos.z() + distance.z(), 0, 0, 0, true);
                }**/

            }
        }

        Vec3d targetPos = getOwner().getPositionVector().add(0, getOwner().getEyeHeight() - 0.3, 0)
                .add(getOwner().getLookVec().scale(0.25));
        setVelocity(targetPos.subtract(getPositionVector()).scale(0.5));


        if (ticksExisted > 150) {
            setDead();
        }

        if (getOwner() == null) {
            setDead();
        }

        if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
                ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(getControlPoint(points.size() - 1).position().toMinecraft()).spawnEntity(this).vel(world.rand.nextGaussian() / 20,
                        world.rand.nextDouble() / 12, world.rand.nextGaussian() / 20).clr(0, 102, 255, 185)
                        .time(14 + AvatarUtils.getRandomNumberInRange(0, 3)).scale(1.25F).collide(true).element(new Waterbending()).spawn(world);
            }
        }

        rotationYaw = getOwner().rotationYaw;
        rotationPitch = getOwner().rotationPitch;

        setSize(getSizeMultiplier(), getSizeMultiplier());
    }

    @Override
    public boolean shouldDissipate() {
        return true;
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

    @Override
    public EntityLivingBase getController() {
        return getOwner();
    }

    @Override
    protected double getControlPointMaxDistanceSq() {
        return 0.625;
    }

    @Override
    protected double getControlPointTeleportDistanceSq() {
        return getControlPointMaxDistanceSq() * points.size();
    }


    @Override
    protected void updateCpBehavior() {
        if (getOwner() != null) {
            for (int i = 1; i < points.size(); i++) {

                ControlPoint leader = points.get(i - 1);
                ControlPoint p = points.get(i);
                Vector leadPos = leader.position();
                Vector lookPos = Vector.getLookRectangular(getOwner()).times(getControlPointMaxDistanceSq() * i);
                lookPos = lookPos.plus(Vector.getEntityPos(getOwner()).plus(0, getOwner().getEyeHeight() - 0.3, 0));

                double sqrDist = p.position().sqrDist(lookPos);

                if (sqrDist > getControlPointTeleportDistanceSq() * getControlPointTeleportDistanceSq() && getControlPointTeleportDistanceSq() != -1) {

                    Vector toFollowerDir = p.position().minus(leader.position()).normalize();

                    double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
                    if (idealDist > 1) idealDist -= 1; // Make sure there is some room

                    Vector revisedOffset = leader.position().plus(toFollowerDir.times(idealDist));
                    p.setPosition(revisedOffset);
                    leader.setPosition(revisedOffset);
                    p.setVelocity(Vector.ZERO);

                } else if (sqrDist > getControlPointMaxDistanceSq() * getControlPointMaxDistanceSq() && getControlPointMaxDistanceSq() != -1) {

                    Vector diff = (lookPos).minus(p.position());
                    diff = diff.times(getVelocityMultiplier() * 200);
                    p.setVelocity(diff);

                }
            }
        }

    }



    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);
        setNumberofPoints((int) (entity.getDistance(this) / getControlPointMaxDistanceSq()));
    }


    /**
     * Custom water cannon collision detection which uses raytrace. Required since water cannon moves
     * quickly and can sometimes "glitch" through an entity without detecting the collision.
     * That's because the hitbox is wonky, and also because the hitbox is well, a box, at the end of the water cannon. If
     * it were to extend across the entire entity.... Well let's just say that minecraft
     * wouldn't be happy.
     */
    @Override
    protected void collideWithNearbyEntities() {

       /* if (getOwner() != null) {
            BendingData data = BendingData.get(getOwner());


            double dist = this.getDistance(getOwner());
            List<Entity> collisions = Raytrace.entityRaytrace(world, getControlPoint(getAmountOfControlPoints() - 1).position(), this.position().minus(getControlPoint(getAmountOfControlPoints() - 1).position()), dist, entity -> entity != getOwner());

            if (!collisions.isEmpty()) {
                for (Entity collided : collisions) {
                    if (canCollideWith(collided) && collided != getOwner()) {
                        onCollideWithEntity(collided);
                        //Needed because the water cannon will still glitch through the entity
                        if (!(data.getAbilityData("water_cannon").isMasterPath(AbilityData.AbilityTreePath.SECOND))) {
                            this.setPosition(collided.posX, this.posY, collided.posZ);
                        }
                    }
                }
            }
        }**/
        super.collideWithNearbyEntities();
    }

    @Override
    public boolean setVelocity() {
        return false;
    }

    @Override
    public boolean onCollideWithSolid() {
        if (getOwner() != null) {
            Vector lastPos = getControlPoint(points.size() - 1).position();
            Vector pos = getControlPoint(points.size() - 2).position();
            Vector dir = lastPos.minus(pos);
            RayTraceResult result = Raytrace.rayTrace(world,
                    lastPos.toMinecraft(), dir.toMinecraft(), 0.75F * getSizeMultiplier(),
                    true, true, false, Entity.class, this::canCollideWith);
            if (result != null) {
                if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (result.hitVec.distanceTo(lastPos.toMinecraft()) < 0.125)
                        setNumberofPoints((int) (result.hitVec.distanceTo(getPositionVector()) / getControlPointMaxDistanceSq()));
                }
            }
        }
        return false;
    }

    @Override
    protected CannonControlPoint createControlPoint(float size, int index) {
        return new CannonControlPoint(this, getSizeMultiplier(),
                posX, posY, posZ);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected double getVelocityMultiplier() {
        return 0.05;
    }

    @Override
    public void setDead() {
        super.setDead();
        if (getOwner() != null) {
            AttributeModifier modifier = getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(WaterChargeHandler.MOVEMENT_MODIFIER_ID);
            if (modifier != null) {
                getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(modifier);
            }
        }
    }


   static class CannonControlPoint extends ControlPoint {

        private CannonControlPoint(EntityWaterCannon arc, float size, double x, double y, double z) {
            super(arc, size, x, y, z);
        }


    }
}


