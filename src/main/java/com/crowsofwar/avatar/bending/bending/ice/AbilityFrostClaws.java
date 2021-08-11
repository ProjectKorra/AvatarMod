package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.entity.EntityIceClaws;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class AbilityFrostClaws extends Ability {

    //Exists for how long the particles are around your fists
    public static final String FADE_DURATION = "fadeDuration";

    public AbilityFrostClaws() {
        super(Icebending.ID, "frost_claws");
    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getAbilityData();
        BendingData data = ctx.getData();


        //Main hand is even for combo, off hand is odd
        if (abilityData.getUseNumber() % 2 == 0)
            data.addTickHandler(TickHandlerController.FROST_CLAW_MAIN_HAND_HANDLER, ctx);
        else data.addTickHandler(TickHandlerController.FROST_CLAW_OFF_HAND_HANDLER, ctx);

        //This goes at the end; spawning goes above.
        if (abilityData.getUseNumber() >=
                getProperty(MAX_COMBO, abilityData).intValue()) {
            //Resets the combo
            abilityData.setUseNumber(0);
        }
        else abilityData.incrementUseNumber();
        super.execute(ctx);
    }

    @Override
    public void init() {
        super.init();
        addProperties(MAX_COMBO, FADE_DURATION);
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    //Correctly positions the entity and sets its orientation
    public void orientate(EntityIceClaws claws, EnumClawDirection direction) {

    }

    //Not all of these will be used but I'll leave them in just in case.
    //Translation:
    /**
     * T = Top
     * B = Bottom
     * R = Right
     * L = Left.
     *
     * Handle in arcs of 90 degrees.
     * The underscore separates start point and destination.
     * Should probably be a handler method for this so I can just plug it in and have fun.
     */
    public enum EnumClawDirection {
        TR_BL,
        L_R,
        B_T,
        T_B,
        R_L,
        TL_BR,
        BL_TR,
        BR_TL
    }
}
