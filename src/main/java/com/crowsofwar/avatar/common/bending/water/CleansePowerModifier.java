package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.air.AbilitySlipstream;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class CleansePowerModifier extends PowerRatingModifier {
    @Override
    public double get(BendingContext ctx) {
        AbilityCleanse cleanse = new AbilityCleanse();
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(cleanse);
        return 10+(3*abilityData.getLevel());

    }
}

