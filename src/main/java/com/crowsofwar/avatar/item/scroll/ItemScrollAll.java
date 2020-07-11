package com.crowsofwar.avatar.item.scroll;

import com.crowsofwar.avatar.registry.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollAll extends ItemScroll {
    public static ItemScrollAll instance = null;

    public ItemScrollAll() {
        super(Scrolls.ScrollType.ALL);
    }

    public static ItemScrollAll getInstance() {
        if(instance == null) {
            instance = new ItemScrollAll();
            AvatarItems.addItem(instance);
        }
        return instance;
    }
}