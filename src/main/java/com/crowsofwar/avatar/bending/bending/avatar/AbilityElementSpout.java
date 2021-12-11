package com.crowsofwar.avatar.bending.bending.avatar;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityElementSpout extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityElementSpout() {
        super(Avatarbending.ID, "element_spout");
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void execute(AbilityContext ctx) {
        super.execute(ctx);
    }

    @Override
    public boolean isUtility() {
        return super.isUtility();
    }
}
