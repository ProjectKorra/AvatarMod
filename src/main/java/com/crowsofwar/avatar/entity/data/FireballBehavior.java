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

import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CrowsOfWar
 */
public abstract class FireballBehavior extends OffensiveBehaviour {

    public static void register() {
        registerBehavior(PlayerControlled.class);
        registerBehavior(Thrown.class);
    }


    public static class Thrown extends FireballBehavior {

        @Override
        public FireballBehavior onUpdate(EntityOffensive entity) {

            entity.addVelocity(Vector.DOWN.times(1F / 40));
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

    public static class PlayerControlled extends FireballBehavior {

        public PlayerControlled() {
        }

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            EntityLivingBase owner = entity.getOwner();

            if (owner == null || !(entity instanceof EntityFireball)) return this;

            Vec3d pos = Vector.getEntityPos(owner).toMinecraft();
            Vec3d look = owner.getLookVec().scale(2.5).add(0, owner.getEyeHeight() / 2, 0);
            Vec3d dragPos = pos.add(look);

            int angle = (int) entity.world.getWorldTime();

            List<EntityFireball> fireballs = entity.world.getEntitiesWithinAABB(EntityFireball.class,
                    owner.getEntityBoundingBox().grow(4, 4, 4));
            fireballs = fireballs.stream().filter(fireball -> fireball.getOwner() == owner).collect(Collectors.toList());
            //Drillgon200: Sort the list by id so the fireballs will always have the same orbit order.
            fireballs.sort((b1, b2) -> b1.getOrbitID() > b2.getOrbitID() ? 1 : -1);
            int index = fireballs.indexOf(entity);
            if (index < 0)
                return this;
            //S P I N
            if (!fireballs.isEmpty() && fireballs.size() > 1) {
                angle *= 10;
                dragPos = AvatarEntityUtils.circularMotion(owner, angle, index, fireballs.size());
            }

            AvatarEntityUtils.dragEntityTowardsPoint(entity, dragPos, 0.20);

            BendingData data = BendingData.getFromEntity(owner);

            //Readds the status control while also giving enough space so all fireballs
            //aren't fired simultaneously.
            if (owner.ticksExisted % 2 == 0)
                if (data != null)
                    if (!data.hasStatusControl(StatusControlController.THROW_FIREBALL))
                        data.addStatusControl(StatusControlController.THROW_FIREBALL);

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
