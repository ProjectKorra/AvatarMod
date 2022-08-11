package com.crowsofwar.avatar.bending.bending.combustion;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

//Based on BNHA howitzer impact
public class AbilityNuclearImpact extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     */
    public AbilityNuclearImpact() {
        super(Combustionbending.ID, "nuclear_impact");
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public void execute(AbilityContext ctx) {
        super.execute(ctx);
        BendingData data = ctx.getData();
        boolean hasHandler = data.hasTickHandler(TickHandlerController.NUCLEAR_IMPACT_HANDLER);
        if (!hasHandler) {
            data.addTickHandler(TickHandlerController.NUCLEAR_IMPACT_HANDLER, ctx);
        }
    }
}
