package com.crowsofwar.avatar.bending.bending.custom.demonic;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

public class AbilityInfernalField  extends Ability {

    //ALso add blindness and wither
    public static final String
            PULL_ENEMIES = "pullsEnemies",
            SLOWNESS_LEVEL = "slownessLevel",
            SLOWNESS_DURATION = "slownessDuration",
            WEAKNESS_LEVEL = "weaknessLevel",
            WEAKNESS_DURATION = "weaknessDuration",
            BLAST_LEVEL = "blastLevel",
            SLOW_MULT = "slowMult";

    public AbilityInfernalField() {
        super(Demonbending.ID, "infernal_field");
    }

    @Override
    public void init() {
        super.init();
        addProperties(SLOWNESS_LEVEL, SLOWNESS_DURATION, WEAKNESS_LEVEL, WEAKNESS_DURATION,
                BLAST_LEVEL, SLOW_MULT);
        addBooleanProperties(PULL_ENEMIES, POTION_EFFECTS);
    }

    @Override
    public void execute(AbilityContext ctx) {
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();

        //The charge status control adds the release status control, but the release status control doesn't activate until the right click button is released.

        boolean hasShadowCharge = data.hasStatusControl(RELEASE_INFERNAL_FIELD);


        if (!hasShadowCharge) {
            data.addStatusControl(CHARGE_INFERNAL_FIELD);
        }
        super.execute(ctx);
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
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
