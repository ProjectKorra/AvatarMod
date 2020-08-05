package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollIce extends ItemScroll {
    private static ItemScrollIce instance = null;

    public ItemScrollIce() {
        super(Scrolls.ScrollType.ICE);
    }

    public static ItemScrollIce getInstance() {
        if(instance == null) {
            instance = new ItemScrollIce();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}