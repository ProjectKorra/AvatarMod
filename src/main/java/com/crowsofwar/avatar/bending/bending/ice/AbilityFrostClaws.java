package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityFrostClaws extends Ability {

    public AbilityFrostClaws() {
        super(Icebending.ID, "frost_claws");
    }

    @Override
    public void execute(AbilityContext ctx) {

        
        super.execute(ctx);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }
}
