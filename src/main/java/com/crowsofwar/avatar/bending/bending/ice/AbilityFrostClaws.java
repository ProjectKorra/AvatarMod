package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;

public class AbilityFrostClaws extends Ability {

    public AbilityFrostClaws() {
        super(Icebending.ID, "frost_claws");
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
