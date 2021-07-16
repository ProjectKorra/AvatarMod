package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;

import java.util.UUID;

public class AbilityFrostForm extends Ability {
    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     * @param bendingType
     * @param name
     */
    public AbilityFrostForm(UUID bendingType, String name) {
        super(bendingType, name);
    }
}
