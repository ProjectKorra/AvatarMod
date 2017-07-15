/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTBendingStyle or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.ctx.Bender;
import net.minecraft.entity.EntityLiving;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class BendingStyles {


    private static final List<BendingStyle> bendingStyles = new ArrayList<>();
    private static final Map<UUID, BendingStyle> bendingStylesById = new HashMap<>();
    private static final Map<String, BendingStyle> bendingStylesByName = new HashMap<>();

    @Nullable
    public static BendingStyle get(UUID id) {
        return bendingStylesById.get(id);
    }

    @Nullable
    public static BendingStyle get(String name) {
        return bendingStylesByName.get(name);
    }

    @Nullable
    public static String getName(UUID id) {
        BendingStyle bendingStyle = get(id);
        return bendingStyle != null ? bendingStyle.getName() : null;
    }

    public static List<BendingStyle> all() {
        return bendingStyles;
    }

    public static void register(BendingStyle bendingStyle) {
        bendingStyles.add(bendingStyle);
        bendingStylesById.put(bendingStyle.getId(), bendingStyle);
        bendingStylesByName.put(bendingStyle.getName(), bendingStyle);
    }


}
