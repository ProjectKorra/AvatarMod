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
package com.crowsofwar.avatar.bending.bending.custom.ki.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.custom.ki.AbilityKamehameha;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.CHARGE_TIME;
import static com.crowsofwar.avatar.bending.bending.Ability.CHI_COST;

/**
 * @author CrowsOfWar
 */
public class ChargeKamehameha extends TickHandler {

    public static final UUID KAMEHAMEHA_MOVEMENT_ID = UUID.randomUUID();

    public ChargeKamehameha(int id) {
        super(id);
    }


    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("kamehameha");
        AbilityKamehameha kamehameha = (AbilityKamehameha) Abilities.get("kamehameha");
        int chargeDuration = data.getTickHandlerDuration(this);
        if (kamehameha == null)
            return false;

        float requiredChi = kamehameha.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;


        float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(KAMEHAMEHA_MOVEMENT_ID) == null)
            applyMovementModifier(entity, movementModifier);

        entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                0.6F, 0.8F + world.rand.nextFloat() / 10);
        return chargeDuration >= kamehameha.getProperty(CHARGE_TIME, abilityData).intValue();
    }


    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("kamehameha");
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(KAMEHAMEHA_MOVEMENT_ID) != null)
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(KAMEHAMEHA_MOVEMENT_ID);
        abilityData.setRegenBurnout(true);
        ctx.getData().addTickHandler(TickHandlerController.KAMEHAMEHA_HANDLER, ctx);

    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(KAMEHAMEHA_MOVEMENT_ID);

        moveSpeed.applyModifier(new AttributeModifier(KAMEHAMEHA_MOVEMENT_ID, "Kamehameha Movement Modifier", multiplier - 1, 1));

    }

}
