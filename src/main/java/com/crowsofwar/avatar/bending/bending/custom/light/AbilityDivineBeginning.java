package com.crowsofwar.avatar.bending.bending.custom.light;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_DIVINE_BEGINNING;

/**
 * Creates orbs of light around you which converge to create a beam.
 */
public class AbilityDivineBeginning extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityDivineBeginning() {
        super(Lightbending.ID, "divine_beginning");
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        if (!ctx.getWorld().isRemote)
            data.addStatusControl(CHARGE_DIVINE_BEGINNING);
        ctx.getAbilityData().setRegenBurnout(false);
    }

    @Override
    public void init() {
        super.init();
        addProperties(R, G, B, FADE_R, FADE_G, FADE_B, CHARGE_TIME);
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
