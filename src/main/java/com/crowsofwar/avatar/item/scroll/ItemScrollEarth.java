package com.crowsofwar.avatar.item.scroll;

import com.crowsofwar.avatar.registry.AvatarItems;

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