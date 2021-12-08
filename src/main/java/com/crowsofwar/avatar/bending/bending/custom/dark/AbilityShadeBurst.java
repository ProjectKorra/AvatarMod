package com.crowsofwar.avatar.bending.bending.custom.dark;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_SHADE_BURST;
import static com.crowsofwar.avatar.util.data.StatusControlController.SHOOT_SHADE_BURST;

//Essentially functions as a shadow ball from pokemon. Slight charge time,
//small projectile, expands then implodes.
public class AbilityShadeBurst extends Ability {
    public static final String
            PULL_ENEMIES = "pullsEnemies",
            SLOWNESS_LEVEL = "slownessLevel",
            SLOWNESS_DURATION = "slownessDuration",
            WEAKNESS_LEVEL = "weaknessLevel",
            WEAKNESS_DURATION = "weaknessDuration",
            BLAST_LEVEL = "blastLevel",
            SLOW_MULT = "slowMult";

    public AbilityShadeBurst() {
        super(Darkbending.ID, "shade_burst");
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

        boolean hasShadowCharge = data.hasStatusControl(SHOOT_SHADE_BURST);


        if (!hasShadowCharge && !ctx.getWorld().isRemote) {
            data.addStatusControl(CHARGE_SHADE_BURST);
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
