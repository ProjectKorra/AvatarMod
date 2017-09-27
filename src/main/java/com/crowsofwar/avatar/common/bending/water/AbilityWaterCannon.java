package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityWaterCannon extends Ability{
    public AbilityWaterCannon() {
        super(Waterbending.ID, "water_cannon");
    }

    @Override
    public void execute(AbilityContext ctx) {

        Bender bender = ctx.getBender();
        BendingData data = ctx.getData();

        boolean hasChi = bender.consumeChi(STATS_CONFIG.chiLightning);
        boolean hasLightningCharge = data.hasTickHandler(TickHandler.LIGHTNING_CHARGE);

        if (hasChi && !hasLightningCharge) {
            ctx.getData().addTickHandler(TickHandler.LIGHTNING_CHARGE);
        }

    }

}
