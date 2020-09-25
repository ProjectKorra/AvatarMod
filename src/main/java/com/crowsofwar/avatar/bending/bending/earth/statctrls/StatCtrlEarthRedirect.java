package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthRedirect;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.bending.bending.Ability.AIM_ASSIST;
import static com.crowsofwar.avatar.bending.bending.Ability.RANGE;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect.REDIRECT_TIER;

public class StatCtrlEarthRedirect extends StatusControl {

    public StatCtrlEarthRedirect() {
        super(100, AvatarControl.CONTROL_SHIFT, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    //This doesn't give me depression yet
    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(new AbilityEarthRedirect());
        AbilityEarthRedirect redirect = (AbilityEarthRedirect) Abilities.get("earth_redirect");

        if (abilityData.getAbilityCooldown(entity) > 0)
            return true;

        if (redirect != null) {
            int cooldown = redirect.getCooldown(abilityData);
            float exhaustion = redirect.getExhaustion(abilityData);
            float burnout = redirect.getBurnOut(abilityData);
            float chiCost = redirect.getChiCost(abilityData);
            float xp = redirect.getProperty(Ability.XP_USE, abilityData).floatValue();
            float radius = redirect.getProperty(Ability.RADIUS, abilityData).floatValue();
            float aimAssist = redirect.getProperty(AIM_ASSIST, abilityData).floatValue();
            float range = redirect.getProperty(RANGE, abilityData).floatValue();
            int redirectTier = redirect.getProperty(REDIRECT_TIER, abilityData).intValue();
            boolean applyInhibitors = false;


            if (entity instanceof EntityBender || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = exhaustion = burnout = cooldown = 0;

            List<EntityOffensive> redirectables = world.getEntitiesWithinAABB(EntityOffensive.class, entity.getEntityBoundingBox().grow(radius));
            redirectables = redirectables.stream().filter(entityOffensive -> entityOffensive.canCollideWith(entity) && entityOffensive.isRedirectable()
                    && entityOffensive.getElement() instanceof Earthbending).collect(Collectors.toList());
            List<Entity> rangedRedirectables = new ArrayList<>(Raytrace.entityRaytrace(world, entity.getPositionVector().add(0, entity.getEyeHeight(), 0),
                    entity.getLookVec(), aimAssist, range, entity1 -> entity1 instanceof EntityOffensive && ((EntityOffensive) entity1).getElement()
                            instanceof Earthbending && ((EntityOffensive) entity1).canCollideWith(entity)));

            if (!redirectables.isEmpty()) {
                for (EntityOffensive e : redirectables) {
                    if (e.getTier() <= redirectTier) {
                        if (bender.consumeChi(chiCost)) {
                            e.setOwner(entity);
                            e.setBehaviour(new OffensiveBehaviour.Redirect());
                            abilityData.addXp(xp);
                            applyInhibitors = true;
                        }
                    }
                }
            } else if (!rangedRedirectables.isEmpty()) {
                for (Entity e : rangedRedirectables) {
                    if (e instanceof EntityOffensive) {
                        if (((EntityOffensive) e).getTier() <= redirectTier) {
                            if (bender.consumeChi(chiCost)) {
                                ((EntityOffensive) e).setOwner(entity);
                                ((EntityOffensive) e).setBehaviour(new OffensiveBehaviour.Redirect());
                                abilityData.addXp(xp);
                                applyInhibitors = true;
                            }
                        }
                    }
                }
            }

            if (applyInhibitors) {
                abilityData.setAbilityCooldown(cooldown);
                abilityData.addBurnout(burnout);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
            }

        }
        return true;
    }
}
