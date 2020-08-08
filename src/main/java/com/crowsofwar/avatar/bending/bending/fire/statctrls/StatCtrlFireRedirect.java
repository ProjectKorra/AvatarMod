package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

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
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(new AbilityFireRedirect());
        AbilityFireRedirect redirect = (AbilityFireRedirect) Abilities.get("fire_redirect");

        if (redirect != null) {
            int cooldown = redirect.getCooldown(abilityData);
            float exhaustion = redirect.getExhaustion(abilityData);
            float burnout = redirect.getBurnOut(abilityData);
            float chiCost = redirect.getChiCost(abilityData);
            float xp = redirect.getProperty(Ability.XP_USE, abilityData).floatValue();
            float radius = redirect.getProperty(Ability.RADIUS, abilityData).floatValue();
            int redirectTier = redirect.getProperty(REDIRECT_TIER, abilityData).intValue();

            xp *= abilityData.getDamageMult() * abilityData.getXpModifier();

            if (entity instanceof EntityBender || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = exhaustion = burnout = cooldown = 0;

            List<EntityOffensive> redirectables = world.getEntitiesWithinAABB(EntityOffensive.class, entity.getEntityBoundingBox().grow(radius));
            redirectables = redirectables.stream().filter(entityOffensive -> entityOffensive.canCollideWith(entity) && entityOffensive.isRedirectable()
                    && entityOffensive.getElement() instanceof Firebending).collect(Collectors.toList());

            if (!redirectables.isEmpty()) {
                for (EntityOffensive e : redirectables) {
                    if (e.getTier() <= redirectTier) {
                        if (bender.consumeChi(chiCost)) {
                            e.setOwner(entity);
                            e.setBehaviour(new OffensiveBehaviour.Redirect());
                            abilityData.setAbilityCooldown(cooldown);
                            abilityData.setBurnOut(burnout);
                            if (entity instanceof EntityPlayer)
                                ((EntityPlayer) entity).addExhaustion(exhaustion);
                            abilityData.addXp(xp);
                        }
                    }
                }
            }

        }
        return true;
    }
}
