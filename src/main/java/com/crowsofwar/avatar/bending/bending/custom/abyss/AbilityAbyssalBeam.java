package com.crowsofwar.avatar.bending.bending.custom.abyss;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_ABYSS_BEAM;
import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_HYPER_BEAM;

public class AbilityAbyssalBeam extends Ability {

    public static final String RANDOMNESS = "randomness";

    public AbilityAbyssalBeam() {
        super(Abyssbending.ID, "abyssal_beam");
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        if (bender.consumeChi(getChiCost(ctx) / 4)) {
            if (!ctx.getWorld().isRemote)
                data.addStatusControl(CHARGE_ABYSS_BEAM);
            ctx.getAbilityData().setRegenBurnout(false);
        }
    }

    @Override
    public void init() {
        super.init();
        addProperties(R, G, B, FADE_R, FADE_G, FADE_B, RANDOMNESS, CHARGE_TIME);
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
