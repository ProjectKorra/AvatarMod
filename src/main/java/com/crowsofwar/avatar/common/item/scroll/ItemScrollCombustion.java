package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollCombustion extends ItemScroll {
    private static ItemScrollCombustion instance = null;

    public ItemScrollCombustion() {
        super(Scrolls.ScrollType.COMBUSTION);
    }

    public static ItemScrollCombustion getInstance() {
        if(instance == null) {
            instance = new ItemScrollCombustion();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}