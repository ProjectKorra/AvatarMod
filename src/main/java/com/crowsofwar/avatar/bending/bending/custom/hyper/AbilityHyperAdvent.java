package com.crowsofwar.avatar.bending.bending.custom.hyper;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

//Ability is meant to simulate the advent of a god
public class AbilityHyperAdvent extends Ability {

    public AbilityHyperAdvent() {
        super(Hyperbending.ID, "hyper_advent");
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

        boolean hasHyperCharge = data.hasStatusControl(RELEASE_HYPER_ADVENT);


        if (bender.consumeChi(getChiCost(ctx) / 4) && !hasHyperCharge) {
            data.addStatusControl(CHARGE_HYPER_ADVENT);
        } else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            if (!hasHyperCharge) {
                data.addStatusControl(CHARGE_HYPER_ADVENT);
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
