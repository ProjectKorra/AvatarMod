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
package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;
import static com.crowsofwar.avatar.common.data.StatusControlController.BUBBLE_CONTRACT;
import static com.crowsofwar.avatar.common.data.StatusControlController.BUBBLE_EXPAND;

/**
 * @author CrowsOfWar
 */
public class AbilityAirBubble extends Ability {

	public AbilityAirBubble() {
		super(Airbending.ID, "air_bubble");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();

		ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		boolean elytraOk = (STATS_CONFIG.allowAirBubbleElytra || chest.getItem() != Items.ELYTRA);

		if (!elytraOk) {
			ctx.getBender().sendMessage("avatar.airBubbleElytra");
		}

		if (!data.hasStatusControl(BUBBLE_CONTRACT) && elytraOk) {

			if (!bender.consumeChi(STATS_CONFIG.chiAirBubble)) return;

			float powerRating = (float) bender.calcPowerRating(Airbending.ID) / 100;

			float size = 1.5f + powerRating;
			float health = 12 + powerRating;
			if (ctx.getLevel() > 1) {
				size = 2.5f + powerRating;
				health = 18 + powerRating;
			}
			if (ctx.isMasterLevel(FIRST)) {
				size = 4f + powerRating;
				health = 24 + powerRating;
			}
			if (ctx.isMasterLevel(SECOND)) health = 10f + powerRating;

			EntityAirBubble bubble = new EntityAirBubble(world);
			bubble.setOwner(entity);
			bubble.setPosition(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
			bubble.setHealth(health);
			bubble.setMaxHealth(health);
			bubble.setSize(size);
			bubble.rotationYaw = entity.rotationYaw;
			bubble.rotationPitch = entity.rotationPitch;
			bubble.motionX = bubble.motionY = bubble.motionZ = 0;
			bubble.setAllowHovering(ctx.isMasterLevel(SECOND));
			bubble.setAbility(this);
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

}
