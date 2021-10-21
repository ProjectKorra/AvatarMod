package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.dark.AbilityWorldEnd;
import com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityInfernalField;
import com.crowsofwar.avatar.bending.bending.custom.light.AbilityDivineJudgement;
import com.crowsofwar.avatar.bending.bending.custom.light.AbilityHolyProtection;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarParticleUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityBuff extends AvatarEntity {
    private static final DataParameter<Integer> SYNC_LIFETIME = EntityDataManager.createKey(
            EntityBuff.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SYNC_RADIUS = EntityDataManager.createKey(
            EntityBuff.class, DataSerializers.FLOAT);

    /**
     * @param world
     */
    public EntityBuff(World world) {
        super(world);
        this.setSize(0.1F, 0.1F);
    }

    public int getLifetime() {
        return dataManager.get(SYNC_LIFETIME);
    }

    public void setLifetime(int lifetime) {
        dataManager.set(SYNC_LIFETIME, lifetime);
    }

    public float getRadius() {
        return dataManager.get(SYNC_RADIUS);
    }

    public void setRadius(float radius) {
        dataManager.set(SYNC_RADIUS, radius);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_LIFETIME, 1);
        dataManager.register(SYNC_RADIUS, 3F);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    protected void collideWithNearbyEntities() {

    }

    @Override
    public void applyEntityCollision(Entity entity) {

    }

    @Override
    public void onCollideWithEntity(Entity entity) {

    }

    @Override
    public boolean onCollideWithSolid() {
        return false;
    }

    @Override
    public void setDead() {
        if (getAbility() instanceof AbilityWorldEnd && getOwner() != null) {
            Explode(world, this, getOwner());
            if (world.isRemote) {
                //Spawn Particles
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(10, 0, 40, 80).scale(getRadius() / 2)
                        .time(40)
                        .glow(true).ability(getAbility()).collide(false).spawnEntity(this)
                        .swirl((int) getRadius(), (int) (getRadius() * Math.PI * 2), getRadius(),
                                getRadius() / 2, getRadius() * 30,
                                10, this, world, false, AvatarEntityUtils.getBottomMiddleOfEntity(this),
                                ParticleBuilder.SwirlMotionType.IN, false, true);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(10, 0, 40, 30).scale(getRadius() / 2)
                        .time(20)
                        .glow(false).ability(getAbility()).collide(false).spawnEntity(this)
                        .swirl((int) getRadius(), (int) (getRadius() * Math.PI * 2), getRadius(),
                                getRadius() / 2, getRadius() * 30,
                                10, this, world, false, AvatarEntityUtils.getBottomMiddleOfEntity(this),
                                ParticleBuilder.SwirlMotionType.IN, false, true);

            }
        }
        super.setDead();
    }

    public void Explode(World world, AvatarEntity entity, EntityLivingBase owner) {
        if (owner != null) {
            //Having a general method means you can use either particle system! Hooray!
            List<Entity> collided = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(getRadius()),
                    entity1 -> entity1 != entity.getOwner());

            if (!collided.isEmpty()) {
                for (Entity entity1 : collided) {
                    if (entity.getOwner() != null && entity1 != entity.getOwner() && entity1 != entity && !world.isRemote) {
                        if (DamageUtils.canDamage(owner, entity1)) {
                            DamageUtils.attackEntity(owner, entity1, AvatarDamageSource.DARK, 12,
                                    20, getAbility(), 4);
                            //Divide the result of the position difference to make entities fly
                            //further the closer they are to the player/entity.
                            double dist = (getRadius() - entity1.getDistance(entity)) > 1 ? (getRadius() - entity1.getDistance(entity)) : 1;
                            Vec3d velocity = entity1.getPositionVector().subtract(entity.getPositionVector());
                            velocity = velocity.scale((1 / 40F)).scale(dist).add(0, getRadius() / 50, 0);

                            double x = velocity.x;
                            double y = velocity.y > 0 ? velocity.z : 0.15F;
                            double z = velocity.z;
                            x *= getRadius() / 2;
                            y *= getRadius() / 2;
                            z *= getRadius() / 2;

                            velocity = velocity.add(x, y, z);

                            entity1.addVelocity(velocity.x, velocity.y, velocity.z);
                            AvatarUtils.afterVelocityAdded(entity1);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (ticksExisted >= getLifetime())
            setDead();

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                getEntityBoundingBox().grow(getRadius()));
        if (!entities.isEmpty() && getOwner() != null) {
            if (!world.isRemote) {
                for (EntityLivingBase toBuff : entities) {
                    if (!DamageUtils.canDamage(getOwner(), toBuff)) {
//                        toBuff.addPotionEffect(new PotionEffect(MobEffects.SPEED,
//                                120, 1));
                        toBuff.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,
                                120, 1));
                    }
                }
            }
        }
        if (getAbility() instanceof AbilityHolyProtection) {
            if (getOwner() != null) {
                List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                        getEntityBoundingBox().grow(getRadius()));
                if (!targets.isEmpty()) {
                    for (EntityLivingBase target : targets) {
                        if (!DamageUtils.canDamage(getOwner(), target)) {
                            target.addPotionEffect(new PotionEffect(MobEffects.SPEED,
                                    120, 2));
                            target.addPotionEffect(new PotionEffect(MobEffects.GLOWING,
                                    120, 2));
                            target.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,
                                    120, 0));
                        }
                    }
                }

                if (world.isRemote) {
                    Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(getOwner()).add(0, getRadius() / 2, 0);
                    float size = 0.75F * getRadius() * (1 / getRadius());
                    int rings = (int) (getRadius() * 8);
                    int particles = (int) (getRadius() * 2 * Math.PI);

                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(12 + AvatarUtils.getRandomNumberInRange(0, 4))
                            .element(BendingStyles.get(getElement())).clr(0.95F, 0.95F, 0.275F, 0.075F).spawnEntity(this).glow(true)
                            .swirl(rings, particles, getRadius(), size * 5, getRadius() * 10, (-1 / size),
                                    this, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);

                }
            }
        }

        if (getAbility() instanceof AbilityWorldEnd && getOwner() != null) {

            if (!world.isRemote) {
                AxisAlignedBB box = new AxisAlignedBB(posX + getRadius(), posY + getRadius(), posZ + getRadius(), posX - getRadius(),
                        posY - getRadius(), posZ - getRadius());
                List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box, entity1 -> entity1 != this);
                if (!collided.isEmpty()) {
                    for (Entity e : collided) {
                        if (e.canBePushed() && e.canBeCollidedWith() && e != this && DamageUtils.canDamage(this, e)) {
                            pullEntities(e, this, 0.05);
                        }
                    }
                }
            }
            if (world.isRemote) {
                AvatarParticleUtils.spawnSpinningDirectionalVortex(world,
                        getOwner(), Vec3d.ZERO, (int) (getRadius() * 9),
                        getRadius(), 0.1F, getRadius(), ParticleBuilder.Type.FLASH,
                        AvatarEntityUtils.getBottomMiddleOfEntity(this), new Vec3d(0.075, 0.075, 0.075), Vec3d.ZERO, world.rand.nextBoolean(), 30, 0, 50, 80,
                        false, 12,
                        getRadius() / 5, true, 0);

                if (ticksExisted % 3 == 0) {
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(30, 0, 50, 80).scale(getRadius() / 5)
                            .time(14)
                            .glow(true).ability(getAbility()).collide(false).spawnEntity(this)
                            .swirl((int) getRadius(), (int) (getRadius() * Math.PI), getRadius(),
                                    getRadius() / 1.35F, getRadius() * 30,
                                    3, this, world, false, AvatarEntityUtils.getBottomMiddleOfEntity(this),
                                    ParticleBuilder.SwirlMotionType.IN, false, true);
                }
            }
        }

        if (getAbility() instanceof AbilityInfernalField && getOwner() != null) {
            if (getOwner() != null) {
                List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                        getEntityBoundingBox().grow(getRadius()));
                if (!targets.isEmpty()) {
                    for (EntityLivingBase target : targets) {
                        if (DamageUtils.canDamage(getOwner(), target)) {
                            target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS,
                                    120, 2));
                            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,
                                    120, 2));
                            target.addPotionEffect(new PotionEffect(MobEffects.WITHER,
                                    120, 0));
                        }
                    }
                }

                if (world.isRemote) {
                    if (ticksExisted % 2 == 0) {
                        Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(this);
                        float size = 0.65F * getRadius() * (1 / getRadius());
                        int rings = (int) (getRadius() * 4);
                        int particles = (int) (getRadius() * Math.PI);

                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(12 + AvatarUtils.getRandomNumberInRange(0, 4))
                                .element(BendingStyles.get(getElement())).clr(120 / 255F, 20 / 255F, 20 / 255F, 0.075F).spawnEntity(this).glow(true)
                                .swirl(rings, particles, getRadius(), size * 25, getRadius() * 10, (-1 / size),
                                        this, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(12 + AvatarUtils.getRandomNumberInRange(0, 4))
                                .element(BendingStyles.get(getElement())).clr(10 / 255F, 10 / 255F, 10 / 255F, 0.075F).spawnEntity(this).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 60)
                                .swirl(rings, particles, getRadius(), size * 25, getRadius() * 10, (-1 / size),
                                        this, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);

                    }
                }
            }
        }

        if (world.isRemote && getOwner() != null && getAbility() instanceof AbilityDivineJudgement) {
            Vec3d startPos = getPositionVector().add(0, 200, 0);
            if (ticksExisted < 20)
                ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(1.0F, 1.0F, 0.8F)
                        .time(20).pos(startPos).scale(getRadius() / 15 * ticksExisted * 3).target(startPos.subtract(0,
                                ticksExisted * 10, 0)).spawnEntity(getOwner()).spawn(world);
            else if (ticksExisted == 20)
                ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(1.0F, 1.0F, 0.8F)
                        .time(getLifetime() - 20).pos(startPos).scale(getRadius() * 4).target(startPos.subtract(0,
                                200, 0)).spawnEntity(getOwner()).spawn(world);
        }
    }

    private void pullEntities(Entity collided, Entity attacker, double suction) {
        Vector velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
        velocity = velocity.times(suction).times(-1);

        double x = (velocity.x());
        double y = (velocity.y());
        double z = (velocity.z());

        if (!collided.world.isRemote) {
            collided.addVelocity(x, y, z);

            if (collided instanceof AvatarEntity) {
                if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
                    AvatarEntity avent = (AvatarEntity) collided;
                    avent.addVelocity(x, y, z);
                }
                collided.isAirBorne = true;
                AvatarUtils.afterVelocityAdded(collided);
            }
        }
    }
}
