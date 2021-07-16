package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.GLACIAL_GLIDE;
import static com.crowsofwar.avatar.util.data.TickHandlerController.GLACIAL_GLIDE_HANDLER;

public class AbilityGlacialGlide extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     */
    public AbilityGlacialGlide() {
        super(Icebending.ID, "glacial_glide");
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

    //Todo: Add waves of fire that follow the player
    @Override
    public void execute(AbilityContext ctx) {

        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getAbilityData();

        if (!data.hasTickHandler(GLACIAL_GLIDE_HANDLER)) {
            if (!data.hasStatusControl(GLACIAL_GLIDE) && bender.consumeChi(getChiCost(abilityData) / 8)) {
                data.addStatusControl(GLACIAL_GLIDE);
                if (data.hasTickHandler(GLACIAL_GLIDE_HANDLER)) {
                    StatusControl sc = GLACIAL_GLIDE;
                    Raytrace.Result raytrace = Raytrace.getTargetBlock(ctx.getBenderEntity(), -1);
                    if (sc.execute(
                            new BendingContext(data, ctx.getBenderEntity(), ctx.getBender(), raytrace))) {
                        data.removeStatusControl(sc);
                    }
                }
            }
        } else {
            data.removeTickHandler(GLACIAL_GLIDE_HANDLER, ctx);
            if (data.hasStatusControl(GLACIAL_GLIDE))
                data.removeStatusControl(GLACIAL_GLIDE);
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
