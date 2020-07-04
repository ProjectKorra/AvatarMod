package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.item.AvatarItems;

/**
 * @author Aang23
 */
public class ItemScrollAll extends ItemScroll {
    public static ItemScrollAll instance = null;

    public ItemScrollAll() {
        super(Scrolls.ScrollType.ALL);
    }

    public ItemScrollAll getInstance() {
        if(instance == null) {
            instance = new ItemScrollAll();
            AvatarItems.addItem(this);
        }
        return instance;
    }
}