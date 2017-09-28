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

package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 *
 *
 * @author CrowsOfWar
 */
public class EntityEarthSpike extends AvatarEntity {

    private Vector initialPosition;

    private float damageMult;
    private double maxTravelDistanceSq;
    private boolean breakBlocks;
    private boolean dropEquipment;

    /**
     * @param world
     */
    public EntityEarthSpike(World world) {
        super(world);
        setSize(1, 1);
        this.damageMult = 1.6F;
    }

    public void setDamageMult(float mult) {
        this.damageMult = mult;
    }
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        setDead();
    }

    @Override
    public void onEntityUpdate() {

        super.onEntityUpdate();

        setVelocity(Vector.ZERO);

        if (ticksExisted >= 40) {
            this.setDead();
        }
        if (!world.isRemote && getOwner() != null) {
            BendingData data = BendingData.get(getOwner());
            if (data != null) {
                data.getAbilityData("earthspike").addXp(SKILLS_CONFIG.ravineHit);
            }
        }
    }

    private boolean attackEntity(Entity entity) {

        if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {


            DamageSource ds = AvatarDamageSource.causeRavineDamage(entity, getOwner());
            float damage = STATS_CONFIG.ravineSettings.damage * damageMult;
            return entity.attackEntityFrom(ds, damage);


        }

        return false;

    }

}






        // Destroy non-solid blocks in the ravine



