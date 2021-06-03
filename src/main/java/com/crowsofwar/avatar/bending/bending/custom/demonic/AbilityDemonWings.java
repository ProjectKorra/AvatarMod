package com.crowsofwar.avatar.bending.bending.custom.demonic;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.DEMON_WINGS;
import static com.crowsofwar.avatar.util.data.TickHandlerController.DEMON_WINGS_HANDLER;

public class AbilityDemonWings extends Ability {

    public AbilityDemonWings() {
        super(Demonbending.ID, "demon_wings");
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
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getAbilityData();

        if (!data.hasTickHandler(DEMON_WINGS_HANDLER)) {
            if (!data.hasStatusControl(DEMON_WINGS) && bender.consumeChi(getChiCost(abilityData) / 8)) {
                data.addStatusControl(DEMON_WINGS);
                if (data.hasTickHandler(DEMON_WINGS_HANDLER)) {
                    StatusControl sc = DEMON_WINGS;
                    Raytrace.Result raytrace = Raytrace.getTargetBlock(ctx.getBenderEntity(), -1);
                    if (sc.execute(
                            new BendingContext(data, ctx.getBenderEntity(), ctx.getBender(), raytrace))) {
                        data.removeStatusControl(sc);
                    }
                }
            }
        } else {
            data.removeTickHandler(DEMON_WINGS_HANDLER, ctx);
            if (data.hasStatusControl(DEMON_WINGS))
                data.removeStatusControl(DEMON_WINGS);
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
