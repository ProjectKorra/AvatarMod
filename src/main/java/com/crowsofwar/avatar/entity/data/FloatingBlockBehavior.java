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

package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author CrowsOfWar
 */
public abstract class FloatingBlockBehavior extends OffensiveBehaviour {

    public static final DataSerializer<FloatingBlockBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

    public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLACE, ID_PLAYER_CONTROL, ID_THROWN;

    public static void register() {
        DataSerializers.registerSerializer(DATA_SERIALIZER);
        ID_NOTHING = registerBehavior(Idle.class);
        ID_FALL = registerBehavior(Fall.class);
        ID_PICKUP = registerBehavior(PickUp.class);
        ID_PLACE = registerBehavior(Place.class);
        ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
        ID_THROWN = registerBehavior(Thrown.class);
    }

    public static class Idle extends FloatingBlockBehavior {

        @Override
        public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
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

    public static class Place extends FloatingBlockBehavior {

        private BlockPos placeAt;

        public Place() {
        }

        public Place(BlockPos placeAt) {
            this.placeAt = placeAt;
        }

        @Override
        public FloatingBlockBehavior onUpdate(EntityOffensive entity) {

            if (entity instanceof EntityFloatingBlock) {
                entity.noClip = true;

                Vector placeAtVec = new Vector(placeAt.getX() + 0.5, placeAt.getY(), placeAt.getZ() + 0.5);
                Vector thisPos = new Vector(entity);
                Vector force = placeAtVec.minus(thisPos);
                force = force.normalize().times(3);
                entity.setVelocity(force);

                if (placeAtVec.sqrDist(thisPos) < 0.005) {
                    ((EntityFloatingBlock) entity).placeBlock();
                    entity.Dissipate();
                    SoundType sound = ((EntityFloatingBlock) entity).getBlock().getSoundType();
                    if (sound != null) {
                        entity.world.playSound(null, entity.getPosition(), sound.getPlaceSound(),
                                SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
                    }

                }
            }
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {
            placeAt = buf.readBlockPos();
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeBlockPos(placeAt);
        }

        @Override
        public void load(NBTTagCompound nbt) {
            placeAt = new BlockPos(nbt.getInteger("PlaceX"), nbt.getInteger("PlaceY"),
                    nbt.getInteger("PlaceZ"));
        }

        @Override
        public void save(NBTTagCompound nbt) {
            nbt.setInteger("PlaceX", placeAt.getX());
            nbt.setInteger("PlaceY", placeAt.getY());
            nbt.setInteger("PlaceZ", placeAt.getZ());
        }

    }

    public static class Thrown extends FloatingBlockBehavior {

        @Override
        public FloatingBlockBehavior onUpdate(EntityOffensive entity) {

            entity.setEntitySize(1.0F);
            if (entity.collided && entity instanceof EntityFloatingBlock) {

                World world = entity.world;
                Block block = ((EntityFloatingBlock) entity).getBlockState().getBlock();
                SoundType sound = block.getSoundType();
                if (sound != null) {
                    world.playSound(null, entity.getPosition(), sound.getBreakSound(),
                            SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
                }

            }

            entity.addVelocity(Vector.DOWN.times(9.81 / 30));

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

    public static class PickUp extends FloatingBlockBehavior {

        @Override
        public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
            entity.addVelocity(Vector.DOWN.times(9.81 / 20));

            Vector velocity = entity.velocity();
            if (velocity.y() <= 0 && entity.getOwner() != null) {
                entity.setVelocity(velocity.withY(0));

                Vec3d forward = entity.getOwner().getLook(1.0F);
                Vec3d eye = entity.getOwner().getPositionEyes(1.0F);
                Vec3d target = forward.scale(2.5).add(eye);
                Vec3d motion = target.subtract(entity.getPositionVector()).scale(0.05);
                entity.setVelocity(motion);

                return new PlayerControlled();
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

    public static class PlayerControlled extends FloatingBlockBehavior {

        public PlayerControlled() {
        }

        @Override
        public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
            EntityLivingBase owner = entity.getOwner();

            if (owner == null || !(entity instanceof EntityFloatingBlock)) return this;

            entity.setEntitySize(0.9F);

            Vec3d forward = owner.getLook(1.0F);
            Vec3d eye = owner.getPositionEyes(1.0F);
            Vec3d target = forward.scale(2.5).add(eye);
            Vec3d motion = target.subtract(AvatarEntityUtils.getBottomMiddleOfEntity(entity)).scale(0.5);
            int angle = (int) entity.world.getWorldTime();
            List<EntityFloatingBlock> blocks = entity.world.getEntitiesWithinAABB(EntityFloatingBlock.class,
                    owner.getEntityBoundingBox().grow(4, 4, 4));
            //Drillgon200: Sort the list by id so the blocks will always have the same orbit order.
            blocks.sort((b1, b2) -> b1.getID() > b2.getID() ? 1 : -1);
            int index = blocks.indexOf(entity);
            if (index < 0)
                return this;
            //S P I N
            if (!blocks.isEmpty() && blocks.size() > 1) {
                angle *= 5;
                angle += ((360 / blocks.size()) * index);
                double radians = Math.toRadians(angle);
                double x = 2.5 * Math.cos(radians);
                double z = 2.5 * Math.sin(radians);
                Vec3d pos = new Vec3d(x, 0, z);
                pos = pos.add(owner.posX, owner.getEntityBoundingBox().minY + 1.5, owner.posZ);
                motion = pos.subtract(AvatarEntityUtils.getBottomMiddleOfEntity(entity)).scale(.5);
            }

            entity.setVelocity(motion);

            BendingData data = BendingData.getFromEntity(owner);
            if (data != null)
                if (!data.hasStatusControl(StatusControlController.THROW_BLOCK) || !data.hasStatusControl(StatusControlController.PLACE_BLOCK)) {
                    data.addStatusControls(StatusControlController.THROW_BLOCK, StatusControlController.PLACE_BLOCK);
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

    public static class Fall extends FloatingBlockBehavior {

        @Override
        public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityFloatingBlock) {
                entity.addVelocity(Vector.DOWN.times(9.81 / 20));
                if (entity.onCollideWithSolid()) {
                    if (!entity.world.isRemote)
                        entity.Dissipate();

                }
                entity.setEntitySize(0.9F);
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
