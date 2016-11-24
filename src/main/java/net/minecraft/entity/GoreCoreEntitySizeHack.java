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

package net.minecraft.entity;

/**
 * A hacky way of calling {@link Entity#setSize(float, float)}.
 * 
 * @author CrowsOfWar
 */
public final class GoreCoreEntitySizeHack {
	
	public static void setWidth(Entity entity, float width) {
		setSize(entity, width, entity.height);
	}
	
	public static void setHeight(Entity entity, float height) {
		setSize(entity, entity.width, height);
	}
	
	public static void setSize(Entity entity, float width, float height) {
		entity.setSize(width, height);
	}
	
}
