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

import com.crowsofwar.avatar.common.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.common.bending.air.AbilityAirJump;
import com.crowsofwar.avatar.common.bending.earth.AbilityPickUpBlock;
import com.crowsofwar.avatar.common.bending.earth.AbilityRavine;
import com.crowsofwar.avatar.common.bending.earth.AbilityWall;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireArc;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.common.bending.fire.AbilityLightFire;
import com.crowsofwar.avatar.common.bending.water.AbilityCreateWave;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterArc;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterBubble;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterSkate;
import com.crowsofwar.avatar.common.gui.AbilityIcon;
import com.crowsofwar.avatar.common.util.Raytrace;

/**
 * Encapsulates all logic required for a bending ability. There is 1 instance of
 * a bending ability for each ability present - similar to BendingController.
 * 
 * @param <STATE>
 *            The BendingState this ability uses
 * 
 * @author CrowsOfWar
 */
public abstract class BendingAbility {
	
	public static BendingAbility ABILITY_AIR_GUST, ABILITY_AIR_JUMP, ABILITY_PICK_UP_BLOCK, ABILITY_RAVINE,
			ABILITY_LIGHT_FIRE, ABILITY_FIRE_ARC, ABILITY_FLAMETHROWER, ABILITY_WATER_ARC, ABILITY_WAVE,
			ABILITY_WATER_BUBBLE, ABILITY_WALL, ABILITY_WATER_SKATE, ABILITY_FIREBALL;
	
	/**
	 * Creates all abilities. Done before bending controllers are created.
	 */
	public static void registerAbilities() {
		ABILITY_AIR_GUST = new AbilityAirGust();
		ABILITY_AIR_JUMP = new AbilityAirJump();
		ABILITY_PICK_UP_BLOCK = new AbilityPickUpBlock();
		ABILITY_RAVINE = new AbilityRavine();
		ABILITY_LIGHT_FIRE = new AbilityLightFire();
		ABILITY_FIRE_ARC = new AbilityFireArc();
		ABILITY_FLAMETHROWER = new AbilityFlamethrower();
		ABILITY_WATER_ARC = new AbilityWaterArc();
		ABILITY_WAVE = new AbilityCreateWave();
		ABILITY_WATER_BUBBLE = new AbilityWaterBubble();
		ABILITY_WALL = new AbilityWall();
		ABILITY_WATER_SKATE = new AbilityWaterSkate();
		ABILITY_FIREBALL = new AbilityFireball();
	}
	
	private static int nextId = 1;
	
	private final BendingType type;
	protected final int id;
	private final String name;
	private final AbilityIcon icon;
	private Raytrace.Info raytrace;
	
	public BendingAbility(BendingType bendingType, String name) {
		this.type = bendingType;
		this.id = nextId++;
		this.name = name;
		BendingManager.registerAbility(this);
		this.icon = new AbilityIcon(getIconIndex() == -1 ? 255 : getIconIndex());
		this.raytrace = new Raytrace.Info();
	}
	
	protected BendingController controller() {
		return BendingManager.getBending(type);
	}
	
	/**
	 * Execute this ability. Only called on server.
	 * 
	 * @param ctx
	 *            Information for the ability
	 */
	public abstract void execute(AbilityContext ctx);
	
	/**
	 * Get the Id of this ability.
	 */
	public final int getId() {
		return id;
	}
	
	/**
	 * Get the texture index of this bending ability. -1 for no texture.
	 */
	public abstract int getIconIndex();
	
	/**
	 * Get the icon for this ability; null for no icon
	 */
	public AbilityIcon getIcon() {
		return icon;
	}
	
	/**
	 * Returns whether this bending ability has an icon.
	 */
	public boolean hasTexture() {
		return getIconIndex() > -1;
	}
	
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
	
}
