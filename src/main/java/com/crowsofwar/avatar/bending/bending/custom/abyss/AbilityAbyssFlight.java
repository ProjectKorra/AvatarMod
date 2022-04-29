package com.crowsofwar.avatar.bending.bending.custom.abyss;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.ABYSS_FLIGHT;
import static com.crowsofwar.avatar.util.data.TickHandlerController.ABYSS_FLIGHT_HANDLER;

public class AbilityAbyssFlight extends Ability {

    public AbilityAbyssFlight() {
        super(Abyssbending.ID, "abyss_flight");
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

        if (!data.hasTickHandler(ABYSS_FLIGHT_HANDLER) && !ctx.getWorld().isRemote) {
            if (!data.hasStatusControl(ABYSS_FLIGHT) && bender.consumeChi(getChiCost(abilityData) / 8)) {

                data.addStatusControl(ABYSS_FLIGHT);
                if (data.hasTickHandler(ABYSS_FLIGHT_HANDLER)) {
                    StatusControl sc = ABYSS_FLIGHT;
                    Raytrace.Result raytrace = Raytrace.getTargetBlock(ctx.getBenderEntity(), -1);
                    if (sc.execute(
                            new BendingContext(data, ctx.getBenderEntity(), ctx.getBender(), raytrace))) {
                        data.removeStatusControl(sc);
                    }
                }
            }
        } else {
            data.removeTickHandler(ABYSS_FLIGHT_HANDLER, ctx);
            if (data.hasStatusControl(ABYSS_FLIGHT))
                data.removeStatusControl(ABYSS_FLIGHT);
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