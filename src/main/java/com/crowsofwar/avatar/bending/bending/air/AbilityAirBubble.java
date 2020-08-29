/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.entity.EntityAirBubble;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.BUBBLE_CONTRACT;
import static com.crowsofwar.avatar.util.data.StatusControlController.BUBBLE_EXPAND;

/**
 * @author CrowsOfWar
 */
public class AbilityAirBubble extends Ability {

    private static final String HOVER = "hover";

    public AbilityAirBubble() {
        super(Airbending.ID, "air_bubble");
    }

    @Override
    public void init() {
        super.init();
        addProperties(CHI_PER_SECOND, CHI_PERCENT, BURNOUT_HIT, EXHAUSTION_HIT, MAX_HEALTH, SIZE);
        addBooleanProperties(HOVER);
    }

    @Override
    public void execute(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        AbilityData abilityData = ctx.getAbilityData();

        ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        boolean elytraOk = (STATS_CONFIG.allowAirBubbleElytra || chest.getItem() != Items.ELYTRA);

        if (!elytraOk) {
            ctx.getBender().sendMessage("avatar.airBubbleElytra");
        }

        if (!data.hasStatusControl(BUBBLE_CONTRACT) && elytraOk) {

            if (!bender.consumeChi(getChiCost(ctx))) return;

            float health = getProperty(MAX_HEALTH, ctx).floatValue();
            float size = getProperty(SIZE, ctx).floatValue();


            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            if (health > 0)
                health *= abilityData.getDamageMult() * abilityData.getXpModifier();

            EntityAirBubble bubble = new EntityAirBubble(world);
            bubble.setOwner(entity);
            bubble.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
            bubble.setMaxHealth(health);
            bubble.setHealth(health);
            bubble.setSize(size);
            bubble.rotationYaw = entity.rotationYaw;
            bubble.rotationPitch = entity.rotationPitch;
            bubble.motionX = bubble.motionY = bubble.motionZ = 0;
            bubble.setAllowHovering(getBooleanProperty(HOVER, ctx));
            bubble.setAbility(this);
            bubble.setTier(getCurrentTier(ctx));

            if (!world.isRemote)
                world.spawnEntity(bubble);

            data.addStatusControl(BUBBLE_EXPAND);
            data.addStatusControl(BUBBLE_CONTRACT);
        }
        super.execute(ctx);

    }

    @Override
    public boolean isUtility() {
        return true;
    }

    @Override
    public int getBaseTier() {
        return 3;
    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiAirBubble(this, entity, bender);
    }

    //We want cooldown applied when the entity dies
    @Override
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }
}
