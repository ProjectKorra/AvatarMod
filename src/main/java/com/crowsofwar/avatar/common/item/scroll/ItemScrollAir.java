package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollAir extends ItemScroll {
    private static ItemScrollAir instance = null;

    public ItemScrollAir() {
        super(Scrolls.ScrollType.AIR);
    }

    public static ItemScrollAir getInstance() {
        if(instance == null) {
            instance = new ItemScrollAir();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}