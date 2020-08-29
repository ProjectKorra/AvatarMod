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

package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.entity.EntityRavine;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class AbilityRavine extends Ability {

    private static final String
            DESTRUCTION = "destruction",
            DROP_EQUIPMENT = "dropEquipment";

    public AbilityRavine() {
        super(Earthbending.ID, "ravine");
    }

    @Override
    public void init() {
        super.init();
        addBooleanProperties(DESTRUCTION, DROP_EQUIPMENT);
    }

    @Override
    public void execute(AbilityContext ctx) {

        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();


        if (bender.consumeChi(getChiCost(ctx))) {

            AbilityData abilityData = ctx.getData().getAbilityData(this);


            Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

            double speed = getProperty(SPEED, ctx).floatValue() * 3;
            float damage = getProperty(DAMAGE, ctx).floatValue();
            int lifetime = getProperty(LIFETIME, ctx).intValue();
            float size = getProperty(SIZE, ctx).floatValue() / 2;

            speed *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();

            EntityRavine ravine = new EntityRavine(world);
            ravine.setOwner(entity);
            ravine.setDamage(damage);
            ravine.setPosition(Vector.getEntityPos(entity).plus(Vector.getLookRectangular(entity).withY(0)));
            ravine.setVelocity(look.times(speed));
            ravine.setAbility(this);
            ravine.setElement(new Earthbending());
            ravine.setLifeTime(lifetime);
            ravine.setEntitySize(size);
            ravine.setXp(getProperty(XP_HIT).floatValue());
            ravine.setDistance(speed);
            ravine.setBreakBlocks(getBooleanProperty(DESTRUCTION, ctx));
            ravine.setDropEquipment(getBooleanProperty(DROP_EQUIPMENT, ctx));
            if (!world.isRemote) {
                world.spawnEntity(ravine);
            }

        }
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public int getBaseTier() {
        return 2;
    }
}
