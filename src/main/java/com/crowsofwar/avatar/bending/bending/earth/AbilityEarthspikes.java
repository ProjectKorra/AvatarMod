package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityEarthspikes extends Ability {

    public static final String
            TRACE_SPIKES = "traceSpikes",
            SPREAD_CIRCULAR = "spreadCircular";

    public AbilityEarthspikes() {
        super(Earthbending.ID, "earth_spikes");
    }

    @Override
    public void init() {
        super.init();
        addProperties(CHARGE_TIME, MAX_DAMAGE, MAX_SIZE, RANGE, RADIUS);
        addBooleanProperties(TRACE_SPIKES, SPREAD_CIRCULAR);
    }

    @Override
    public void execute(AbilityContext ctx) {
        AbilityData abilityData = ctx.getAbilityData();
        Bender bender = ctx.getBender();
        BendingData data = ctx.getData();

        if (bender.consumeChi(getChiCost(ctx) / 4))
            data.addStatusControl(StatusControlController.CHARGE_EARTH_SPIKE);
        abilityData.setRegenBurnout(false);
        super.execute(ctx);

    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public int getBaseTier() {
        return 3;
    }

    @Override
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getBurnOut(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getExhaustion(AbilityContext ctx) {
        return 0;
    }
}
