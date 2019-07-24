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

package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import com.crowsofwar.avatar.common.item.scroll.ItemScrollLightning;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * Encapsulates all logic required for a bending ability. There is 1 instance of
 * a bending ability for each ability present - similar to BendingController.
 *
 * @author CrowsOfWar
 */
public abstract class Ability {

	private final UUID type;
	private final String name;
	private Raytrace.Info raytrace;

	public Ability(UUID bendingType, String name) {
		this.type = bendingType;
		this.name = name;
		this.raytrace = new Raytrace.Info();
	}

	protected BendingStyle controller() {
		return BendingStyles.get(type);
	}

	/**
	 * Get the id of the bending style that this ability belongs to
	 */
	public final UUID getBendingId() {
		return type;
	}

	/**
	 * Execute this ability. Only called on server.
	 *
	 * @param ctx Information for the ability
	 */
	public abstract void execute(AbilityContext ctx);

	/**
	 * Get cooldown after the ability is activated.
	 */
	public int getCooldown(AbilityContext ctx) {
		return 0;
	}

	/*
	 * Generally used for abilities that grant you stat boosts.
	 */
	public boolean isBuff() {
		return false;
	}

	/**
	 * Generally used for abilities that help with evreryday tasks, such as mining,
	 * moving water sources, or just moving around. Ex: Mine Blocks, Air Jump, and
	 * Water Bubble are all utility Abilities.
	 */

	public boolean isUtility() {
		return false;
	}

	/**
	 * Require that a raycast be sent prior to {@link #execute(AbilityContext)}.
	 * Information for the raytrace will then be available through the
	 * {@link AbilityContext}.
	 *
	 * @param range          Range to raycast. -1 for player's reach.
	 * @param raycastLiquids Whether to keep going on hit liquids
	 */
	protected void requireRaytrace(double range, boolean raycastLiquids) {
		this.raytrace = new Raytrace.Info(range, raycastLiquids);
	}

	/**
	 * Get the request raytrace requirements for when the ability is activated.
	 */
	public final Raytrace.Info getRaytrace() {
		return raytrace;
	}

	/**
	 * Gets the name of this ability. Will be all lowercase with no spaces.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Whether the ability is visible in the radial menu. Note that this doesn't
	 * hide the ability from the skills menu.
	 */
	public boolean isVisibleInRadial() {
		return true;
	}

	public int getTier() {
		return 1;
	}

	public BendingStyle getElement() {
		return BendingStyles.get(getBendingId());
	}

	public int getParentTier() {
		if (this.getElement().getParentBendingId() != null) {
			return 1;
		}
		return 0;
	}

	public boolean isCompatibleScroll(ItemStack stack, int level, AbilityData.AbilityTreePath path) {
		if (getBendingId() != null) {
			if (stack.getItem() instanceof ItemScroll) {
				Scrolls.ScrollType type = ((ItemScroll) stack.getItem()).getScrollType();
				ItemScroll scroll = (ItemScroll) stack.getItem();
				if (type.getBendingId() == getBendingId() || type == Scrolls.ScrollType.ALL) {
					if (level < 1) {
						return /*scroll.getTier()*/10 >= getTier();
					}
					if (level == 1) {
						return /*scroll.getTier()*/10 >= getTier() + 1;
					}
					return /*scroll.getTier()*/10 >= getTier() + 2;

				}
				if (getParentTier() > 0) {
					if (Objects.requireNonNull(BendingStyles.get(getBendingId())).getParentBendingId() == type
							.getBendingId()) {
						if (level < 1) {
							return /*scroll.getTier()*/10 >= getParentTier();
						}
						if (level == 1) {
							return /*scroll.getTier()*/10 >= getParentTier() + 1;
						}
						return /*scroll.getTier()*/10 >= getParentTier() + 2;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Creates a new instance of AI for the given entity/bender.
	 */
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new DefaultAbilityAi(this, entity, bender);
	}

}
