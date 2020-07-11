package com.crowsofwar.avatar.item.scroll;

import com.crowsofwar.avatar.registry.AvatarItems;

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