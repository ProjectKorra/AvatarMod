package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollLightning extends ItemScroll {
    private static ItemScrollLightning instance = null;

    public ItemScrollLightning() {
        super(Scrolls.ScrollType.LIGHTNING);
    }

    public static ItemScrollLightning getInstance() {
        if(instance == null) {
            instance = new ItemScrollLightning();
            AvatarItems.addItem(instance);
        }

        return instance;
    }
}