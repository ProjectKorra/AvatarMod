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

import java.util.UUID;

import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.util.Raytrace;

import net.minecraft.entity.EntityLiving;

/**
 * Encapsulates all logic required for a bending ability. There is 1 instance of
 * a bending ability for each ability present - similar to BendingController.
 * 
 * @author CrowsOfWar
 */
public abstract class Ability {
	
	private final int type;
	private final String name;
	private Raytrace.Info raytrace;
	
	public Ability(int bendingType, String name) {
		this.type = bendingType;
		this.name = name;
		this.raytrace = new Raytrace.Info();
	}
	
	protected BendingStyle controller() {
		return BendingManager.getBending(type);
	}
	
	/**
	 * Get the bending type that this ability belongs to
	 */
	public final int getBendingId() {
		return type;
	}
	
	/**
	 * Execute this ability. Only called on server.
	 * 
	 * @param ctx
	 *            Information for the ability
	 */
	public abstract void execute(AbilityContext ctx);
	
	/**
	 * Get cooldown after the ability is activated.
	 */
	public int getCooldown(AbilityContext ctx) {
		return 15;
	}
	
	/**
	 * Get the Id of this ability. It is unique from all other abilities, and is
	 * the same every time Minecraft runs (i.e. is not dynamically generated)
	 */
	public abstract UUID getId();
	
	/**
	 * Require that a raycast be sent prior to {@link #execute(AbilityContext)}.
	 * Information for the raytrace will then be available through the
	 * {@link AbilityContext}.
	 * 
	 * @param range
	 *            Range to raycast. -1 for player's reach.
	 * @param raycastLiquids
	 *            Whether to keep going on hit liquids
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
	 * Creates a new instance of AI for the given entity/bender.
	 */
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new DefaultAbilityAi(this, entity, bender);
	}
	
}
