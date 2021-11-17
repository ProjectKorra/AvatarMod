package com.crowsofwar.avatar.bending.bending.custom.demonic;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_HELL_BASTION;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_HELL_BASTION;

//Creates a field, with a spirit bomb, then drops and detonates.
//Absorbs energy from nearby enemies
public class AbilityHellBastion extends Ability {
    public static final String
            BLAST_LEVEL = "blastLevel",
            SLOW_MULT = "slowMult";

    public AbilityHellBastion() {
        super(Demonbending.ID, "hell_bastion");
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

        boolean hasDemonicCharge = data.hasStatusControl(RELEASE_HELL_BASTION);


        if (bender.consumeChi(getChiCost(ctx) / 4) && !hasDemonicCharge) {
            data.addStatusControl(CHARGE_HELL_BASTION);
        } else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            if (!hasDemonicCharge) {
                data.addStatusControl(CHARGE_HELL_BASTION);
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
