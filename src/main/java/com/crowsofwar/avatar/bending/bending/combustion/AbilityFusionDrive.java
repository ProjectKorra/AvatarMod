package com.crowsofwar.avatar.bending.bending.combustion;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityFusionDrive extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     */
    public AbilityFusionDrive() {
        super(Combustionbending.ID, "fusion_drive");
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        addProperties(FIRE_TIME);
    }

    @Override
    public void execute(AbilityContext ctx) {
        super.execute(ctx);
        ctx.getData().addTickHandler(TickHandlerController.FUSION_DRIVE_HANDLER, ctx);
    }
}
