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

import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.data.AvatarWorldData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

/**
 * @author CrowsOfWar
 */
public abstract class WaterBubbleBehavior extends OffensiveBehaviour {

    protected WaterBubbleBehavior() {
    }

    public static void register() {
        registerBehavior(Drop.class);
        registerBehavior(PlayerControlled.class);
        registerBehavior(Lobbed.class);
        //When you use the water bubble like a bucket
    }

    public static class Drop extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            entity.addVelocity(Vector.DOWN.times(0.981));
            if (entity.collided) {
                entity.setDead();
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

    public static class PlayerControlled extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            EntityLivingBase owner = entity.getOwner();

            if (owner == null) return this;

            Vec3d pos = Vector.getEntityPos(owner).toMinecraft();
            Vec3d look = owner.getLookVec().scale(2.5).add(0, owner.getEyeHeight() / 2, 0);
            AvatarEntityUtils.dragEntityTowardsPoint(entity, pos.add(look), 0.125);
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

    public static class Lobbed extends WaterBubbleBehavior {
        //For when you use the water bubble like a bucket
        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            entity.addVelocity(Vector.DOWN.times(0.8));
            if (entity.getOwner() == null) return this;
            if (entity.collided && entity instanceof EntityWaterBubble) {

                IBlockState state = Blocks.FLOWING_WATER.getDefaultState();

                if (!entity.world.isRemote) {

                    entity.world.setBlockState(entity.getPosition(), state, 3);
                    entity.setDead();

                    if (!((EntityWaterBubble) entity).isSourceBlock()) {
                        AvatarWorldData wd = AvatarWorldData.getDataFromWorld(entity.world);
                        wd.addTemporaryWaterLocation(entity.getPosition());
                    }

                }

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
