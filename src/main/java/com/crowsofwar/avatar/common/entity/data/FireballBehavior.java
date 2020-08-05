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

import com.crowsofwar.avatar.bending.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

/**
 * @author CrowsOfWar
 */
public abstract class FireballBehavior extends OffensiveBehaviour {

    public static void register() {
        registerBehavior(PlayerControlled.class);
        registerBehavior(Thrown.class);
        registerBehavior(AbilityFireball.FireballOrbitController.class);
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
        public FireballBehavior onUpdate(EntityOffensive entity) {
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
