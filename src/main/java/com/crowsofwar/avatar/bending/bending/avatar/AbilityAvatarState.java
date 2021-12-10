package com.crowsofwar.avatar.bending.bending.avatar;

import com.crowsofwar.avatar.bending.bending.Ability;

import java.util.UUID;

public class AbilityAvatarState extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     * @param bendingType
     * @param name
     */
    public AbilityAvatarState(UUID bendingType, String name) {
        super(bendingType, name);
    }

    @Override
    public boolean isBuff() {
        return true;
    }

    @Override
    public boolean isUtility() {
        return true;
    }
}
