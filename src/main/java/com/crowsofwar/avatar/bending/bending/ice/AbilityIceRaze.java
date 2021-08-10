package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.START_ICE_RAZE;

public class AbilityIceRaze extends Ability {

    public static final String
            RANDOMNESS = "randomness",
            FLAMES_PER_SECOND = "particles";

    public AbilityIceRaze() {
        super(Icebending.ID, "ice_raze");
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        if (bender.consumeChi(getChiCost(ctx) / 4)) {
            data.addStatusControl(START_ICE_RAZE);
            ctx.getAbilityData().setRegenBurnout(false);
        }
    }

    @Override
    public void init() {
        super.init();
        addProperties(FIRE_TIME, FIRE_R, FIRE_G, FIRE_B, FADE_R, FADE_G, FADE_B, RANDOMNESS,  FLAMES_PER_SECOND);
        addBooleanProperties(SMELTS, SETS_FIRES);
    }

    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public boolean isOffensive() {
        return true;
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
