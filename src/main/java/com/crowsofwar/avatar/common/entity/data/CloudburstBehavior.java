package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public abstract class CloudburstBehavior extends Behavior<EntityCloudBall>{
    public static final DataSerializer<CloudburstBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

    public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLAYER_CONTROL, ID_THROWN;

    public static void register() {
        DataSerializers.registerSerializer(DATA_SERIALIZER);
        ID_NOTHING = registerBehavior(CloudburstBehavior.Idle.class);
        ID_PLAYER_CONTROL = registerBehavior(CloudburstBehavior.PlayerControlled.class);
        ID_THROWN = registerBehavior(CloudburstBehavior.Thrown.class);
    }

    public static class Idle extends CloudburstBehavior {

        @Override
        public CloudburstBehavior onUpdate(EntityCloudBall entity) {
            return this;
        }
        @Override
        public void fromBytes(PacketBuffer buf) {}

        @Override
        public void toBytes(PacketBuffer buf) {}

        @Override
        public void load(NBTTagCompound nbt) {}

        @Override
        public void save(NBTTagCompound nbt) {}

    }

    public static class Thrown extends CloudburstBehavior {

        int time = 0;

        @Override
        public CloudburstBehavior onUpdate(EntityCloudBall entity) {

            time++;

            if (entity.isCollided || (!entity.world.isRemote && time > 100)) {
                entity.setDead();
                entity.onCollideWithSolid();
            }

            entity.addVelocity(0, -1 / 120, 0);

            World world = entity.world;
            if (!entity.isDead) {
                List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
                        entity.getExpandedHitbox());
                if (!collidedList.isEmpty()) {
                    Entity collided = collidedList.get(0);
                    if (collided instanceof EntityLivingBase && collided != entity.getOwner()) {
                        collision((EntityLivingBase) collided, entity);
                    } else if (collided != entity.getOwner()) {
                        Vector motion = new Vector(collided).minus(new Vector(entity));
                        motion = motion.times(0.3).withY(0.08);
                        collided.addVelocity(motion.x(), motion.y(), motion.z());

                    }

                }
            }

            return this;

        }

        private void collision(EntityLivingBase collided, EntityCloudBall entity) {
            double speed = entity.velocity().magnitude();

            if (collided.attackEntityFrom(AvatarDamageSource.causeFireballDamage(collided, entity.getOwner()),
                    entity.getDamage())) {
                BattlePerformanceScore.addMediumScore(entity.getOwner());
            }


            Vector motion = entity.velocity().dividedBy(20);
            motion = motion.times(STATS_CONFIG.fireballSettings.push).withY(0.09);
            collided.addVelocity(motion.x(), motion.y(), motion.z());

            BendingData data = Bender.get(entity.getOwner()).getData();
            if (!collided.world.isRemote && data != null) {
                float xp = SKILLS_CONFIG.cloudburstHit;
                data.getAbilityData("cloudburst").addXp(xp);
            }

            // Remove the fireball & spawn particles
            if (!entity.world.isRemote)

                entity.setDead();
            entity.onCollideWithSolid();

        }

        @Override
        public void fromBytes(PacketBuffer buf) {}

        @Override
        public void toBytes(PacketBuffer buf) {}

        @Override
        public void load(NBTTagCompound nbt) {}

        @Override
        public void save(NBTTagCompound nbt) {}

    }

    public static class PlayerControlled extends CloudburstBehavior {

        public PlayerControlled() {}

        @Override
        public CloudburstBehavior onUpdate(EntityCloudBall entity) {
            EntityLivingBase owner = entity.getOwner();

            if (owner == null) return this;

            BendingData data = Bender.get(owner).getData();

            double yaw = Math.toRadians(owner.rotationYaw);
            double pitch = Math.toRadians(owner.rotationPitch);
            Vector forward = Vector.toRectangular(yaw, pitch);
            Vector eye = Vector.getEyePos(owner);
            Vector target = forward.times(2).plus(eye);
            Vector motion = target.minus(Vector.getEntityPos(entity)).times(6);
            entity.setVelocity(motion);

            if (data.getAbilityData("cloudburst").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                int size = entity.getSize();
                if (size < 60 && entity.ticksExisted % 4 == 0) {
                    entity.setSize(size + 1);
                }
            }

            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {}

        @Override
        public void toBytes(PacketBuffer buf) {}

        @Override
        public void load(NBTTagCompound nbt) {}

        @Override
        public void save(NBTTagCompound nbt) {}

    }

}
