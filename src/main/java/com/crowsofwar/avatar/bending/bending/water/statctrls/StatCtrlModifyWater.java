package com.crowsofwar.avatar.bending.bending.water.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.water.AbilityFlowControl;
import com.crowsofwar.avatar.bending.bending.water.AbilityWaterArc;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.item.ItemWaterPouch;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StatCtrlModifyWater extends StatusControl {

    public StatCtrlModifyWater() {
        super(3, AvatarControl.CONTROL_MIDDLE_CLICK, CrosshairPosition.ABOVE_CROSSHAIR);
    }


    //Does 2 things:
    //1) Changes the player's flow control into a water arc and back
    //2) Puts water back into the water pouch
    @Override
    public boolean execute(BendingContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityFlowControl control = (AbilityFlowControl) Abilities.get("flow_control");
        AbilityWaterArc arc = (AbilityWaterArc) Abilities.get("water_arc");

        EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class, entity);

        if (bubble != null) {
            //First checks for the water pouch
            if (entity.getHeldItemMainhand().getItem() instanceof ItemWaterPouch
                    || entity.getHeldItemOffhand().getItem() instanceof ItemWaterPouch) {
                ItemStack pouch = ItemStack.EMPTY;

                if (entity.getHeldItemMainhand().getItem() instanceof ItemWaterPouch) {
                    pouch = entity.getHeldItemMainhand();
                } else if (entity.getHeldItemOffhand().getItem() instanceof ItemWaterPouch) {
                    pouch = entity.getHeldItemOffhand();
                }

                if (pouch != ItemStack.EMPTY) {
                    pouch.setItemDamage(Math.min((int) (pouch.getMetadata() + bubble.getHealth()), 4));
                }

                bubble.Dissipate();

            }
            //Then switches states
            else if (control != null && arc != null) {
                AbilityData controlData = AbilityData.get(entity, control.getName());
                AbilityData arcData = AbilityData.get(entity, arc.getName());

                //Bubble to arc
                if (bubble.getDefaultState().equals(EntityWaterBubble.State.BUBBLE)) {
                    setBubbleProperties(bubble, arc, arcData);
                    bubble.setDefaultState(EntityWaterBubble.State.ARC);
                    bubble.setState(EntityWaterBubble.State.ARC);
                }

                //Arc to bubble
                else if (bubble.getDefaultState().equals(EntityWaterBubble.State.ARC)) {
                    setBubbleProperties(bubble, control, controlData);
                    bubble.setDefaultState(EntityWaterBubble.State.BUBBLE);
                    bubble.setState(EntityWaterBubble.State.BUBBLE);
                }
            }
        }
        return true;
    }

    private void setBubbleProperties(EntityWaterBubble bubble, Ability ability, AbilityData abilityData) {
        float damage = ability.getProperty(Ability.DAMAGE, abilityData).floatValue();
        float size = ability.getProperty(Ability.SIZE, abilityData).floatValue();
        float chiHit = ability.getProperty(Ability.CHI_HIT, abilityData).floatValue();
        float distance = ability.getProperty(Ability.RANGE, abilityData).floatValue();
        float xp = ability.getProperty(Ability.XP_HIT, abilityData).floatValue();
        int performance = ability.getProperty(Ability.PERFORMANCE, abilityData).intValue();
        int lifetime = ability.getProperty(Ability.LIFETIME, abilityData).intValue();
        int tier = ability.getCurrentTier(abilityData);

        bubble.setDamage(damage);
        bubble.setDistance(distance);
        bubble.setMaxSize(size);
        bubble.setChiHit(chiHit);
        bubble.setXp(xp);
        bubble.setAbility(ability);
        bubble.setLifeTime(lifetime);
        bubble.setPerformanceAmount(performance);
        bubble.setTier(tier);
    }
}
