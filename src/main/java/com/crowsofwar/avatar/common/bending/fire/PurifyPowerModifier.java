package com.crowsofwar.avatar.common.bending.fire;


import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class PurifyPowerModifier extends PowerRatingModifier {
    @Override
    public double get(BendingContext ctx) {
        AbilityPurify purify = new AbilityPurify();
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(purify);
        setTicks(20+(20*abilityData.getLevel()));
        return 10+(3*abilityData.getLevel());

    }
}

