package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollWater extends ItemScroll {
    private static ItemScrollWater instance = null;

    public ItemScrollWater() {
        super(Scrolls.ScrollType.WATER);
    }

    public static ItemScrollWater getInstance() {
        if(instance == null) {
            instance = new ItemScrollWater();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}