package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class SlipstreamPowerModifier extends PowerRatingModifier {
    @Override
    public double get(BendingContext ctx) {
        AbilitySlipstream slipstream = new AbilitySlipstream();
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(slipstream);
        return 10*2*abilityData.getLevel();

        }
    }

