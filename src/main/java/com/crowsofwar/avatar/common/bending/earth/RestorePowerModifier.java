package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.air.AbilitySlipstream;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class RestorePowerModifier extends PowerRatingModifier {
    @Override
    public double get(BendingContext ctx) {
        AbilityRestore restore = new AbilityRestore();
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(restore);
        return 10+(3*abilityData.getLevel());

    }
}

