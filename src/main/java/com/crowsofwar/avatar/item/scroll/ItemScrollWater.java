package com.crowsofwar.avatar.item.scroll;

import com.crowsofwar.avatar.registry.AvatarItems;

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