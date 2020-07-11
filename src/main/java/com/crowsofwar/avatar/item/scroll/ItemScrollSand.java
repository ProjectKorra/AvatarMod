package com.crowsofwar.avatar.item.scroll;

import com.crowsofwar.avatar.registry.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollSand extends ItemScroll {

    private static ItemScrollSand instance = null;

    public ItemScrollSand() {
        super(Scrolls.ScrollType.SAND);
    }

    public static ItemScrollSand getInstance() {
        if(instance == null) {
            instance = new ItemScrollSand();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}