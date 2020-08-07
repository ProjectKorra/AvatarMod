package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect.REDIRECT_TIER;

public class StatCtrlFireRedirect extends StatusControl {

    public StatCtrlFireRedirect() {
        super(100, AvatarControl.CONTROL_SHIFT, CrosshairPosition.BELOW_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(new AbilityFireRedirect());
        AbilityFireRedirect redirect = (AbilityFireRedirect) Abilities.get("fire_redirect");

        if (redirect != null) {
            int cooldown = redirect.getCooldown(abilityData);
            float exhaustion = redirect.getExhaustion(abilityData);
            float burnout = redirect.getBurnOut(abilityData);
            float chiCost = redirect.getChiCost(abilityData);
            float xp = redirect.getProperty(Ability.XP_USE, abilityData).floatValue();
            float radius = redirect.getProperty(Ability.RADIUS, abilityData).floatValue();
            float range = redirect.getProperty(Ability.RANGE, abilityData).floatValue();
            float aimAssist = redirect.getProperty(Ability.AIM_ASSIST, abilityData).floatValue();
            int tier = redirect.getProperty(REDIRECT_TIER, abilityData).intValue();

            xp *= abilityData.getDamageMult() * abilityData.getXpModifier();

            List<EntityOffensive> redirectables = world.getEntitiesWithinAABB(EntityOffensive.class, entity.getEntityBoundingBox().grow(radius));



        }
        return true;
    }
}
