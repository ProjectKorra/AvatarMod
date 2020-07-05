package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollEarth extends ItemScroll {
    private static ItemScrollEarth instance = null;

    public ItemScrollEarth() {
        super(Scrolls.ScrollType.EARTH);
    }

    public static ItemScrollEarth getInstance() {
        if(instance == null) {
            instance = new ItemScrollEarth();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}