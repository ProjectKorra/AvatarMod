package com.crowsofwar.avatar.bending.bending.custom.ki;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_SPIRIT_BOMB;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_SPIRIT_BOMB;

public class AbilitySpiritBomb extends Ability {
    public static final String
            BLAST_LEVEL = "blastLevel",
            SLOW_MULT = "slowMult";

    public AbilitySpiritBomb() {
        super(Kibending.ID, "spirit_bomb");
    }

    @Override
    public void init() {
        super.init();
        addProperties(BLAST_LEVEL, EFFECT_RADIUS, EFFECT_DAMAGE, SLOW_MULT);
    }

    @Override
    public void execute(AbilityContext ctx) {
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();

        //The charge status control adds the release status control, but the release status control doesn't activate until the right click button is released.

        boolean hasKiCharge = data.hasStatusControl(RELEASE_SPIRIT_BOMB);


        if (bender.consumeChi(getChiCost(ctx) / 4) && !hasKiCharge) {
            data.addStatusControl(CHARGE_SPIRIT_BOMB);
        } else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            if (!hasKiCharge) {
                data.addStatusControl(CHARGE_SPIRIT_BOMB);
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
