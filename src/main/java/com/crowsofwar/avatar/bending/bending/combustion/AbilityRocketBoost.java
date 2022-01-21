package com.crowsofwar.avatar.bending.bending.combustion;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityRocketBoost extends Ability {
    public AbilityRocketBoost() {
        super(Combustionbending.ID, "rocket_boost");
    }

    @Override
    public void init() {
        addProperties(TIER, CHI_COST, BURNOUT, BURNOUT_REGEN, COOLDOWN, EXHAUSTION,
                SPEED, CHI_HIT, PERFORMANCE, XP_HIT, SIZE, KNOCKBACK, DAMAGE, XP_USE, FIRE_TIME);
        addProperties(DURATION, R, G, B, FADE_R, FADE_G, FADE_B,
                FALL_ABSORPTION);
        addBooleanProperties(STOP_SHOCKWAVE);
    }

    @Override
    public boolean isUtility() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }


    @Override
    public void execute(AbilityContext ctx) {

        BendingData data = ctx.getData();

        if (!ctx.getWorld().isRemote) {
            if (data.hasStatusControl(StatusControlController.ROCKET_BOOST)) {
                data.removeTickHandler(TickHandlerController.ROCKET_BOOST_HANDLER, ctx);
                data.removeStatusControl(StatusControlController.ROCKET_BOOST);
            } else
                data.addStatusControl(StatusControlController.ROCKET_BOOST);
        }
    }

    //Override the AbilityContext inhibitors as we don't want them to be applied upon executing the abilit
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
