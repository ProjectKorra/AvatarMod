package com.crowsofwar.avatar.bending.bending.ice.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.ice.AbilityFrostClaws;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;

public class FrostClawHandler extends TickHandler {

    //So it spawns particles at main and off hand respectively
    private EnumHand hand;
    public FrostClawHandler(int id, EnumHand hand) {
        super(id);
        this.hand = hand;
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityFrostClaws claws = (AbilityFrostClaws) Abilities.get("frost_claws");

        if (claws != null) {
            AbilityData abilityData = ctx.getData().getAbilityData(claws);
            //This logic is simply used for determining when the tick handler dies
            int duration = data.getTickHandlerDuration(this);
            int maxDuration = claws.getProperty(AbilityFrostClaws.FADE_DURATION, abilityData).intValue();
            //Only used for spawning particles
            //Time to copy and paste some code ;))



            return duration >= maxDuration;
        }
        return true;
    }
}
