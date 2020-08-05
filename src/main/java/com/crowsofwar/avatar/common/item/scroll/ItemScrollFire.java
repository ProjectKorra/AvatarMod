package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.entity.AvatarEntityItem;

import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Aang23
 */
public class ItemScrollFire extends ItemScroll {
    private static ItemScrollFire instance = null;

    public ItemScrollFire() {
        super(Scrolls.ScrollType.FIRE);
    }

    public static ItemScrollFire getInstance() {
        if(instance == null) {
            instance = new ItemScrollFire();
            AvatarItems.addItem(instance);
        }

        return instance;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity old, ItemStack stack) {
        AvatarEntityItem custom = new AvatarEntityItem(world, old.posX, old.posY, old.posZ, stack);
        custom.setResistFire(true);
        custom.motionX = old.motionX;
        custom.motionY = old.motionY;
        custom.motionZ = old.motionZ;
        custom.setDefaultPickupDelay();
        return custom;
    }
}