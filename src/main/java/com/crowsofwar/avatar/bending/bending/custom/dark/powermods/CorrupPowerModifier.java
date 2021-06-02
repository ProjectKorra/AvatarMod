package com.crowsofwar.avatar.bending.bending.custom.dark.powermods;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.custom.dark.AbilityCorrupt;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;

import java.util.Objects;

public class CorrupPowerModifier extends PowerRatingModifier {

    @Override
    public double get(BendingContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(new AbilityCorrupt().getName());

        //Powerrating should be an integer but I'll leave it as a double toa count for user error
        return Objects.requireNonNull(Abilities.get("corrupt")).getProperty(Ability.POWERRATING, abilityData).doubleValue();

    }

    @Override
    public boolean onUpdate(BendingContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = AbilityData.get(entity, "corrupt");

        return super.onUpdate(ctx);
    }

}

