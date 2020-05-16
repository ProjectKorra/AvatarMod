package com.crowsofwar.avatar.common.item.scroll;

import java.util.UUID;

import javax.annotation.Nullable;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.ice.Icebending;
import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.bending.sand.Sandbending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;

import net.minecraft.item.ItemStack;

public class Scrolls {

    public static ItemScroll WATER;
    public static ItemScroll EARTH;
    public static ItemScroll FIRE;
    public static ItemScroll AIR;
    public static ItemScroll COMBUSTION;
    public static ItemScroll SAND;
    public static ItemScroll ICE;
    public static ItemScroll LIGHTNING;
    public static ItemScroll ALL;

    public static ItemScroll getItemForType(ScrollType type) {
        switch (type) {
        case ALL:
            return ALL;
        case EARTH:
            return EARTH;
        case FIRE:
            return FIRE;
        case AIR:
            return AIR;
        case COMBUSTION:
            return COMBUSTION;
        case SAND:
            return SAND;
        case ICE:
            return ICE;
        case LIGHTNING:
            return LIGHTNING;
        case WATER:
            return WATER;
        default:
            return null;
        }
    }

    public static ScrollType getTypeForStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemScroll) {
            return ((ItemScroll) stack.getItem()).getScrollType();
        } else {
            return null;
        }
    }

    public static int getTierForStack(ItemStack stack) {
        return stack.getMetadata() + 1;
    }

    @Nullable
    public static ScrollType getTypeFromElement(BendingStyle style) {
        if (style instanceof Airbending)
            return ScrollType.AIR;
        if (style instanceof Firebending)
            return ScrollType.FIRE;
        if (style instanceof Waterbending)
            return ScrollType.WATER;
        if (style instanceof Earthbending)
            return ScrollType.EARTH;
        if (style instanceof Combustionbending)
            return ScrollType.COMBUSTION;
        if (style instanceof Icebending)
            return ScrollType.ICE;
        if (style instanceof Sandbending)
            return ScrollType.SAND;
        if (style instanceof Lightningbending)
            return ScrollType.LIGHTNING;
        else return ScrollType.ALL;
    }

    public static ScrollType getTypeFromElement(String element) {
        switch (element) {
            case "air":
                return ScrollType.AIR;
            case "fire":
                return ScrollType.FIRE;
            case "water":
                return ScrollType.WATER;
            case "earth":
                return ScrollType.EARTH;
            case "combustion":
                return ScrollType.COMBUSTION;
            case "ice":
                return ScrollType.ICE;
            case "sand":
                return ScrollType.SAND;
            case "lightning":
                return ScrollType.LIGHTNING;
            default:
                return ScrollType.ALL;
        }
    }
    public enum ScrollType {
        ALL(null), // 0
        EARTH(Earthbending.ID), // 1
        FIRE(Firebending.ID), // 2
        WATER(Waterbending.ID), // 3
        AIR(Airbending.ID), // 4
        LIGHTNING(Lightningbending.ID), // 5
        ICE(Icebending.ID), // 6
        SAND(Sandbending.ID), // 7
        COMBUSTION(Combustionbending.ID); // 8

        private final UUID bendingId;

        ScrollType(UUID bendingId) {
            this.bendingId = bendingId;
        }

        @Nullable
        public static ScrollType get(int id) {
            if (id < 0 || id >= values().length)
                return null;
            return values()[id];
        }

        public static int amount() {
            return values().length;
        }

        public boolean isCompatibleWith(ScrollType other) {
            return other == this || this == ALL || other == ALL;
        }

        public String displayName() {
            return name().toLowerCase();
        }

        public boolean accepts(UUID bendingId) {
            // Universal scroll
            if (this.bendingId == null)
                return true;
            // Same type
            if (this.bendingId == bendingId)
                return true;

            // Trying to use parent-type bending scroll on specialty bending style
            return BendingStyles.get(bendingId).getParentBendingId() == this.bendingId;
        }

        public String getBendingName() {
            return bendingId == null ? "all" : BendingStyles.get(bendingId).getName();
        }

        /**
         * Gets the corresponding bending ID from this scroll. Returns null in the case
         * of ALL.
         */
        @Nullable
        public UUID getBendingId() {
            return bendingId;
        }

        /**
         * Returns whether this scroll is for a specialty bending type, like
         * lightningbending. For universal scrolls, returns false.
         */
        public boolean isSpecialtyType() {
            BendingStyle style = BendingStyles.get(bendingId);
            if (style == null)
                return false;
            return style.isSpecialtyBending();

        }
    }
}