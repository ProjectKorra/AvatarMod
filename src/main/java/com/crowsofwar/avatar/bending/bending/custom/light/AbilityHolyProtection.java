package com.crowsofwar.avatar.bending.bending.custom.light;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

public class AbilityHolyProtection extends Ability {
    public static final String
            BLAST_LEVEL = "blastLevel",
            SLOW_MULT = "slowMult";

    public AbilityHolyProtection() {
        super(Lightbending.ID, "holy_protection");
    }

    @Override
    public void init() {
        super.init();
        addProperties(BLAST_LEVEL, EFFECT_RADIUS, EFFECT_DAMAGE, SLOW_MULT);
        addBooleanProperties(POTION_EFFECTS);
    }

    @Override
    public void execute(AbilityContext ctx) {
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();

        //The charge status control adds the release status control, but the release status control doesn't activate until the right click button is released.

        boolean hasAirCharge = data.hasStatusControl(RELEASE_HOLY_PROTECTION);


        if (!ctx.getWorld().isRemote) {
            if (bender.consumeChi(getChiCost(ctx) / 4) && !hasAirCharge) {
                data.addStatusControl(CHARGE_HOLY_PROTECTION);
            } else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                if (!hasAirCharge) {
                    data.addStatusControl(CHARGE_HOLY_PROTECTION);
                }
            }
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
