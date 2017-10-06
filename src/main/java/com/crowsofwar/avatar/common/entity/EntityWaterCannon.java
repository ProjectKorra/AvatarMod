package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

public class EntityWaterCannon extends EntityArc<EntityWaterCannon.CannonControlPoint> {
    private static final DataParameter<Vector> SYNC_ENDPOS = EntityDataManager.createKey
            (EntityWaterCannon.class, AvatarDataSerializers.SERIALIZER_VECTOR);

    private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
            (EntityWaterCannon.class, DataSerializers.FLOAT);

    /**
     * If the lightning hits an entity, the lightning "sticks to" that entity and continues to
     * damage it.
     */
    @Nullable
    private EntityLivingBase stuckTo;

    /**
     * If the lightning hits an entity or the ground, the lightning "sticks to" that position and
     * will die after some time after getting stuck.
     */
    private int stuckTime;

    /**
     * Whether the lightning was <b>attempted to be redirected</b> by stuckTo (ie, the targeted
     * player). Redirected lightning will no longer attempt to damage the target; this prevents issues where target
     * redirects lightning multiple times.
     * <p>
     * It is possible stuckTo failed to redirect the lightning; in this case wasRedirected will
     * remain true, and stuckTo will not attempt to redirect lightning again (only gets one chance).
     */
    private boolean wasRedirected;

    /**
     * Whether the lightning was created through redirecting a first lightning. Not to be
     * confused with {@link #wasRedirected}, which is whether a first lightning got redirected to
     * make a second lightning.
     */
    private boolean createdByRedirection;

    private float damage;



    public EntityWaterCannon(World world) {
        super(world);
        setSize(1.5f, 1.5f);
        damage = 10;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_ENDPOS, Vector.ZERO);
        dataManager.register(SYNC_SIZE, 3f);
    }

    //controls how lasery the entity is; the more control points, the more it will look like a laser.
    @Override
    public int getAmountOfControlPoints() {
        return 40;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getOwner() != null) {
            Vector controllerPos = Vector.getEyePos(getOwner());
            Vector endPosition = getEndPos();
            Vector position = controllerPos;

            // position slightly below eye height
            position = position.minusY(0.3);
            // position slightly away from controller
            position = position.plus(endPosition.minus(position).dividedBy(15));

            setEndPos(position);

            Vector newRotations = Vector.getRotationTo(position(), getEndPos());
            rotationYaw = (float) Math.toDegrees(newRotations.y());
            rotationPitch = (float) Math.toDegrees(newRotations.x());
        }

        if (stuckTo != null) {
            setPosition(Vector.getEyePos(stuckTo));
            setVelocity(Vector.ZERO);
              damageEntity(stuckTo, 0.5f);
            }


        if (velocity().equals(Vector.ZERO)) {
            stuckTime++;
            if (stuckTime == 1) {
                world.playSound(null, getPosition(), SoundEvents.BLOCK_WATER_AMBIENT,
                        SoundCategory.PLAYERS, 1, 1);
            }
        }

        boolean existTooLong = stuckTime >= 20 || ticksExisted >= 200;
        boolean stuckIsDead = stuckTo != null && stuckTo.isDead;
        if (existTooLong || stuckIsDead) {
            setDead();
        }

        setSize(3.0f * getSizeMultiplier(), 3.0f * getSizeMultiplier());

    }

    @Override
    protected void updateCpBehavior() {
        for (EntityWaterCannon.CannonControlPoint controlPoint : getControlPoints()) {

            controlPoint.setPosition(controlPoint.getPosition
                    (ticksExisted));

        }
    }

    @Override
    protected void onCollideWithEntity(Entity entity){
            if (stuckTo == null && entity instanceof EntityLivingBase) {

                stuckTo = (EntityLivingBase) entity;

            }
        }

    /**
     * Custom lightning collision detection which uses raytrace. Required since lightning moves
     * quickly and can sometimes "glitch" through an entity without detecting the collision.
     */
    @Override
    protected void collideWithNearbyEntities() {

        List<Entity> collisions = Raytrace.entityRaytrace(world, position(), velocity(), velocity
                ().magnitude() / 20, entity -> entity != getOwner() && entity != this);

        for (Entity collided : collisions) {
            onCollideWithEntity(collided);
        }

    }



    private void damageEntity(EntityLivingBase entity, float damageModifier) {

        if (world.isRemote) {
            return;
        }

        DamageSource damageSource = createDamageSource(entity);
        if (entity.attackEntityFrom(damageSource, damage *
                damageModifier)) {


            Vector velocity = getEntityPos(entity).minus(this.position()).normalize();
            velocity = velocity.times(2);
            entity.addVelocity(velocity.x(), 0.4, velocity.z());
            AvatarUtils.afterVelocityAdded(entity);

            // Add Experience
            // Although 2 lightning entities are fired in each lightning ability, this won't
            // cause 2x XP rewards as this only happens when the entity is successfully attacked
            // (hurtResistantTime prevents the 2 lightning entities from both damaging at once)
            if (getOwner() != null) {
                BendingData data = BendingData.get(getOwner());
                AbilityData abilityData = data.getAbilityData("water_cannon");
                abilityData.addXp(SKILLS_CONFIG.struckWithLightning);
            }
        }

    }

    private DamageSource createDamageSource(EntityLivingBase target) {
        if (createdByRedirection) {
            return AvatarDamageSource.causeRedirectedLightningDamage(target, getOwner());
        } else {
            return AvatarDamageSource.causeLightningDamage(target, getOwner());
        }
    }

    @Override
    protected boolean canCollideWith(Entity entity) {
        return entity != getOwner();
    }

    @Override
    public boolean onCollideWithSolid() {
//		setDead();
        setVelocity(Vector.ZERO);
        return false;
//		return true;
    }

    @Override
    protected EntityWaterCannon.CannonControlPoint createControlPoint(float size, int index) {
        return new EntityWaterCannon.CannonControlPoint(this, index);
    }

    public Vector getEndPos() {
        return dataManager.get(SYNC_ENDPOS);
    }

    public void setEndPos(Vector endPos) {
        dataManager.set(SYNC_ENDPOS, endPos);
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

    public class CannonControlPoint extends ControlPoint {

        private final int index;

        public CannonControlPoint(EntityArc arc, int index) {
            super(arc, 1.0f, 0, 0, 0);
            this.index = index;
        }

        public Vector getPosition(float ticks) {

            float partialTicks = ticks - (int) ticks;
            Vector arcPos = arc.position().plus(arc.velocity().dividedBy(20).times(partialTicks));

            double targetDist = arcPos.dist(getEndPos()) / getControlPoints().size();
            Vector dir = Vector.getLookRectangular(arc);

            Vector normalPosition = arcPos.plus(dir.times(targetDist).times(index));
            Vector randomize = Vector.ZERO;


            return normalPosition;
        }

        @Override
        public Vector getInterpolatedPosition(float partialTicks) {
            return getPosition(arc.ticksExisted + partialTicks);
        }

    }
}


