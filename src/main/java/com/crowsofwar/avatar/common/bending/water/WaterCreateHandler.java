package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import javax.annotation.Nullable;

public class WaterCreateHandler extends WaterChargeHandler{

    @Override
    @Nullable
    protected AbilityData getLightningData(BendingContext ctx) {
        return ctx.getData().getAbilityData("water_cannon");
    }
}

