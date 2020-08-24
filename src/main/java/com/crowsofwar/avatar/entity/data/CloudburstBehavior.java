package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.entity.EntityCloudBall;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class CloudburstBehavior extends OffensiveBehaviour {
    public static final DataSerializer<CloudburstBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

    public static int ID_NOTHING, ID_PLAYER_CONTROL, ID_THROWN;

    public static void register() {
        DataSerializers.registerSerializer(DATA_SERIALIZER);
        ID_NOTHING = registerBehavior(CloudburstBehavior.Idle.class);
        ID_PLAYER_CONTROL = registerBehavior(CloudburstBehavior.PlayerControlled.class);
        ID_THROWN = registerBehavior(CloudburstBehavior.Thrown.class);
    }

    public static class Idle extends CloudburstBehavior {

        @Override
        public CloudburstBehavior onUpdate(EntityOffensive entity) {
            return this;
        }


        @Override
        public void fromBytes(PacketBuffer buf) {
        }

        @Override
        public void toBytes(PacketBuffer buf) {
        }

        @Override
        public void load(NBTTagCompound nbt) {
        }

        @Override
        public void save(NBTTagCompound nbt) {
        }

    }

    public static class Thrown extends CloudburstBehavior {

        int time = 0;

        @Override
        public CloudburstBehavior onUpdate(EntityOffensive entity) {

            time++;
            entity.addVelocity(0, -1F / 120, 0);

            return this;

        }

        @Override
        public void fromBytes(PacketBuffer buf) {
        }

        @Override
        public void toBytes(PacketBuffer buf) {
        }

        @Override
        public void load(NBTTagCompound nbt) {
        }

        @Override
        public void save(NBTTagCompound nbt) {
        }

    }

    public static class PlayerControlled extends CloudburstBehavior {

        public PlayerControlled() {
        }

        @Override
        public CloudburstBehavior onUpdate(EntityOffensive entity) {

            if (entity instanceof EntityCloudBall) {
                EntityLivingBase owner = entity.getOwner();

                if (owner == null) return this;

                Vector forward = Vector.getLookRectangular(owner);
                Vector eye = Vector.getEyePos(owner).minusY(0.5);
                Vector target = forward.times(1.5).plus(eye);
                Vector motion = target.minus(Vector.getEntityPos(entity)).times(6);
                entity.setVelocity(motion);
            }
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {
        }

        @Override
        public void toBytes(PacketBuffer buf) {
        }

        @Override
        public void load(NBTTagCompound nbt) {
        }

        @Override
        public void save(NBTTagCompound nbt) {
        }

    }

}
