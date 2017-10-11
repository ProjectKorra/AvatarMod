package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class CleansePowerModifier extends PowerRatingModifier {
    @Override
    public double get(BendingContext ctx) {
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(new AbilityCleanse());
        return 10+(3*abilityData.getLevel());

    }
}

