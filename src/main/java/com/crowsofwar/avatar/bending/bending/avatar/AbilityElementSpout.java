package com.crowsofwar.avatar.bending.bending.avatar;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityElementSpout extends Ability {

    public static final String MAX_HEIGHT = "maxHeight";
    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityElementSpout() {
        super(Avatarbending.ID, "element_spout");
    }

    @Override
    public void init() {
        super.init();
        addProperties(MAX_HEIGHT, EFFECT_RADIUS, SPEED);
    }

    @Override
    public void execute(AbilityContext ctx) {
        super.execute(ctx);

        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();


        boolean hasSpout = data.hasTickHandler(TickHandlerController.ELEMENT_SPOUT_HANDLER);
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!hasSpout) {
                player.capabilities.isFlying = true;
                //Add the tick handler
                data.addTickHandler(TickHandlerController.ELEMENT_SPOUT_HANDLER, ctx);
            }
            else {
                player.capabilities.isFlying = false;
                data.removeTickHandler(TickHandlerController.ELEMENT_SPOUT_HANDLER, ctx);
            }
        }
    }

    @Override
    public boolean isUtility() {
        return true;
    }

    @Override
    public float getChiCost(AbilityContext ctx) {
        return 0;
    }
}
