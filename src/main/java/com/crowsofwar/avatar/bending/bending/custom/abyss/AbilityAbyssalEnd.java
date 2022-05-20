package com.crowsofwar.avatar.bending.bending.custom.abyss;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.custom.hyper.Hyperbending;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

//Ability is meant to simulate the advent of a god
public class AbilityAbyssalEnd extends Ability {

    public AbilityAbyssalEnd() {
        super(Abyssbending.ID, "abyss_end");
    }

    @Override
    public void init() {
        super.init();
        addProperties(RADIUS);
    }

    @Override
    public void execute(AbilityContext ctx) {
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();

        //The charge status control adds the release status control, but the release status control doesn't activate until the right click button is released.

        boolean hasHyperCharge = data.hasStatusControl(RELEASE_ABYSS_END);
        ctx.getAbilityData().setUseNumber(0);

        if (bender.consumeChi(getChiCost(ctx) / 4) && !hasHyperCharge) {
            data.addStatusControl(CHARGE_ABYSS_END);
        } else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            if (!hasHyperCharge) {
                data.addStatusControl(CHARGE_ABYSS_END);
            }
        }
        super.execute(ctx);
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}
