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
package com.crowsofwar.gorecore.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Contains utility methods for parsing JSON, using Google's GSON API
 * </p>
 * 
 * @author Mahtaran
 */
public final class JsonUtils {
	/**
	 * <p>
	 * Gets the JsonElement at a certain path.
	 * </p>
	 * 
	 * @param jsonString
	 *            The JSON String
	 * @param path
	 *            The path where to get the element from, with subsections divided by dots (<code>.</code>)
	 * @return The JsonElement at the path
	 */
	public static JsonElement fromString(String jsonString, String path) throws JsonSyntaxException {
		JsonObject json = new GsonBuilder().create().fromJson(jsonString, JsonObject.class);
		// Prefixed by two slashes because otherwise it's a special delimiter
    		String[] segments = path.split("\\.");
    		for (String segment : segments) {
			if (json != null) {
				JsonElement element = json.get(segment);
				if (!element.isJsonObject()) {
					return element;
				} else {
					json = element.getAsJsonObject();
				}
			} else {
				return null;
			}
		}
		return json;
	}
}
